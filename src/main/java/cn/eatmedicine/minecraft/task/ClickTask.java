package cn.eatmedicine.minecraft.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cat.nyaa.nyaacore.http.client.HttpClient;
import cat.nyaa.nyaacore.utils.ItemStackUtils;
import cn.eatmedicine.minecraft.Database.TransData;
import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.command.factory.InputPsw;
import cn.eatmedicine.minecraft.config.serverIds;
import cn.eatmedicine.minecraft.http.IXPCallBack;
import cn.eatmedicine.minecraft.utils.Tools;
import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import cn.eatmedicine.minecraft.IXPData.IXPData;

public class ClickTask extends BukkitRunnable {

    public List<ClickTask> getDataList() {
        return dataList;
    }

    public void setDataList(List<ClickTask> dataList) {
        this.dataList = dataList;
    }

    public IXPData getData() {
        return data;
    }

    public void setData(IXPData data) {
        this.data = data;
    }

    public int getClickNum() {
        return clickNum;
    }

    public void setClickNum(int clickNum) {
        this.clickNum = clickNum;
    }

    public Main getPlugin() {
        return plugin;
    }

    public void setPlugin(Main plugin) {
        this.plugin = plugin;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private List<ClickTask> dataList;
    private IXPData data;
    private int clickNum;
    private Main plugin;
    private Player player;

    public List<InputPswTask> waitInputPswList;


    //需要输入一个任务列表、对应IXP牌子的信息、插件对象、还有右键的玩家对象
    public ClickTask(List<ClickTask> list, IXPData data, Main plugin, Player player)  {
        this.dataList = list;
        this.data = data;
        this.clickNum = 1;
        this.plugin = plugin;
        this.player = player;
        this.waitInputPswList = plugin.waitInputPswList;
    }

    @Override
    public void run() {
        // 单击事件
        if (clickNum == 1) {
            PlayerInventory pi = player.getInventory();
            InputPswTask tmp = new InputPswTask(data,player.getName(),plugin.waitInputPswList,plugin);
            int delay = plugin.cm.config.getInt("misc.password-timeout")*20;
            tmp.runTaskLaterAsynchronously(plugin,delay);
            if(plugin.waitInputPswList.size()!=0){
                for(InputPswTask task : plugin.waitInputPswList){
                    if(task.PlayerName.equals(player.getName())){
                        plugin.waitInputPswList.remove(task);
                    }
                }
            }

            waitInputPswList.add(tmp);
        }
        // 多击事件
        else if (clickNum > 1) {
            //开始发送信息
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item.getType()== Material.AIR){
                player.sendMessage("Your main hand needs to have an item");
                dataList.remove(this);
                return;
            }
            String itemBase64 = ItemStackUtils.itemToBase64(item);
            player.getInventory().setItemInMainHand(null);
            Date date = new Date();
            TransData tdata = new TransData();
            tdata.SetTransData(plugin.cm.localInfo.getName(),
                    player.getUniqueId().toString(), itemBase64,
                    "CannotUseThisPassword",date.getTime(),false);
            Gson gson = new Gson();
            //trans-id
            String transId = Tools.GetRandomString(16);

            //head
            IXPData ixpData = data;
            serverIds targetServer = null;
            for(serverIds server : plugin.cm.server){
                if(server.getName().equals(ixpData.getToServer())){
                    targetServer = server;
                }
            }
            if(targetServer == null){
                player.sendMessage("未找到目标服务器信息");
                player.getInventory().addItem(item);
                dataList.remove(this);
                return;
            }
            Map<String,String> head = new HashMap<>();
            head.put("x-ixp-psk",targetServer.getPsk());
            String url = "http://"+targetServer.getIp()+":"+targetServer.getPort()+"/ix/v1/"+ixpData.getToServer()+"/"+transId;
            HttpClient.postJson(url, head, gson.toJson(tdata),new IXPCallBack(plugin,tdata));
        }
        //运行完一次后删除自己
        dataList.remove(this);
    }

    public void addClickNum() {
        clickNum++;
    }

}
