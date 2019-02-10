package cn.eatmedicine.minecraft.listener;

import java.util.ArrayList;
import java.util.List;

import cat.nyaa.nyaacore.utils.ItemStackUtils;
import cat.nyaa.nyaacore.utils.VaultUtils;
import cn.eatmedicine.minecraft.Database.TransData;
import cn.eatmedicine.minecraft.IXPData.IXPType;
import cn.eatmedicine.minecraft.task.ClickTask;
import cn.eatmedicine.minecraft.task.InputPswTask;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import cn.eatmedicine.minecraft.*;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.Database.SignData;
import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.utils.BlockAnalysis;
import cn.eatmedicine.minecraft.utils.Tools;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractEventListener implements Listener {

    public List<ClickTask> taskList;
    public Main plugin;
    public List<String> itemList;
    public List<InputPswTask> waitInputPswList;

    public PlayerInteractEventListener(Main plugin) {
        this.plugin = plugin;
        //初始化一个任务列表
        taskList = new ArrayList<ClickTask>();
        this.waitInputPswList = plugin.waitInputPswList;
    }

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        //检查事件是否和block有关   不是则返回
        if (!event.hasBlock()) {
            return;
        }
        VaultUtils.getVaultEconomy();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Sign sign = BlockAnalysis.GetSign(event.getClickedBlock());
            //如果分析Block是一个牌子则继续
            if (sign != null) {
                if (isSignEnable(event.getClickedBlock().getLocation()) == false) {
                    player.sendMessage("该牌子未设置或者未启用");
                    return;
                }
                //然后分析牌子的内容，得出信息
                Database db = new Database(plugin);
                SignData sdata = db.selectSign(sign.getX(), sign.getY(), sign.getZ(),
                        sign.getWorld().getName());
                Tools.updateSign(sign, sdata);
                IXPData data = BlockAnalysis.GetIXP(sign, plugin.cm.localInfo.getName(), plugin);
                if (data != null) {
                    player.sendMessage("这是一个可点击的[IXP]牌子");
                    if(data.getType()== IXPType.SEND){
                        ClickTask task = Tools.findTask(taskList, data, player);
                        //如果尚未添加该任务
                        if (task == null) {
                            ClickTask tmp = new ClickTask(taskList, data, plugin, player);
                            taskList.add(tmp);

                            tmp.runTaskLater(plugin, 8);
                        } else {
                            task.addClickNum();
                        }
                    }
                    else{
                        List<TransData> list = db.SelectTransDataByUuid(player.getUniqueId().toString());
                        if(list.size()==0){
                            player.sendMessage("There are no items to be acquired");
                            return;
                        }
                        for(TransData tdata : list){
                            Inventory inventory = player.getInventory();
                            if(inventory.firstEmpty()==-1){
                                player.sendMessage("Your inventory already full，please clear your inventory and try again");
                                break;
                            }
                            ItemStack item = ItemStackUtils.itemFromBase64(tdata.ItemData);
                            db.deleteTransData(tdata.SenderUuid,tdata.TimeStamp);
                            inventory.addItem(item);
                        }
                        return;
                    }
                }
                return;
            }
        }

    }

    public boolean isSignEnable(Location signSite) {
        //如果不存在对应路径
        int x = signSite.getBlockX();
        int y = signSite.getBlockY();
        int z = signSite.getBlockZ();
        Database db = new Database(plugin);
        SignData sd = db.selectSign(x, y, z, signSite.getWorld().getName());
        if (sd == null) {
            plugin.getLogger().info("该牌子不在配置文件中");
            return false;
        }
        if (sd.isEnable == 0) {
            plugin.getLogger().info("该牌子未启用");
            return false;
        }
        return true;
    }

}
