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
import org.bukkit.OfflinePlayer;
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
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Sign sign = BlockAnalysis.GetSign(event.getClickedBlock());
            //如果分析Block是一个牌子则继续
            if (sign != null) {
                if (isSignEnable(event.getClickedBlock().getLocation()) == false) {
                    player.sendMessage(plugin.lang.format("message.interact.ixpSign_not_find_database"));
                    return;
                }
                //然后分析牌子的内容，得出信息
                Database db = new Database(plugin);
                SignData sdata = db.selectSign(sign.getX(), sign.getY(), sign.getZ(),
                        sign.getWorld().getName());
                Tools.updateSign(sign, sdata);
                IXPData data = BlockAnalysis.GetIXP(sign, plugin.cm.localInfo.getName(), plugin);
                if (data != null) {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(player.getUniqueId());
                    if(plugin.economy.has(offlinePlayer,data.getFee())==false){
                        player.sendMessage(plugin.lang.format("message.interact.not_enough_money"));
                        return;
                    }
                    //Send Sign
                    if(data.getType()== IXPType.SEND){
                        ClickTask task = Tools.findTask(taskList, data, player);
                        //如果尚未添加该任务
                        if (task == null) {
                            ClickTask tmp = new ClickTask(taskList, data, plugin, player);
                            taskList.add(tmp);
                            tmp.runTaskLater(plugin, 8);
                        }
                        //如果任务已经存在，则点击次数+1，用于区分单击任务和双击任务
                        else {
                            task.addClickNum();
                        }
                    }
                    //Receive Sign
                    else{
                        List<TransData> list = db.SelectTransDataByName(player.getName());
                        if(list.size()==0){
                            player.sendMessage(plugin.lang.format("message.interact.not_find_acquire_item"));
                            return;
                        }
                        for(TransData tdata : list){
                            //Check if the player account has money to get the next item
                            if(plugin.economy.has(offlinePlayer,data.getFee())==false){
                                player.sendMessage(plugin.lang.format("message.interact.not_enough_money"));
                                return;
                            }
                            //Check inventory has empty
                            Inventory inventory = player.getInventory();
                            if(inventory.firstEmpty()==-1){
                                player.sendMessage(plugin.lang.format("message.interact.inventory_full"));
                                break;
                            }
                            ItemStack item = ItemStackUtils.itemFromBase64(tdata.ItemData);
                            db.deleteTransData(tdata.SenderName,tdata.TimeStamp);
                            inventory.addItem(item);
                            //Take money
                            plugin.economy.withdrawPlayer(offlinePlayer,data.getFee());
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
            return false;
        }
        if (sd.isEnable == 0) {
            return false;
        }
        return true;
    }

}
