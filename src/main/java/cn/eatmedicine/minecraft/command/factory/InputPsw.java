package cn.eatmedicine.minecraft.command.factory;

import cat.nyaa.nyaacore.http.client.HttpClient;
import cat.nyaa.nyaacore.utils.ItemStackUtils;
import cn.eatmedicine.minecraft.Database.TransData;
import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.config.serverIds;
import cn.eatmedicine.minecraft.http.IXPCallBack;
import cn.eatmedicine.minecraft.task.InputPswTask;
import cn.eatmedicine.minecraft.utils.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InputPsw implements IHandleCommand{
    private Main plugin;
    CommandSender sender;
    String psw;
    long time;
    public InputPsw(Main plugin, CommandSender sender,String psw){
        this.plugin = plugin;
        this.sender = sender;
        this.psw = psw;
        time = new Date().getTime();
    }


    @Override
    public boolean handleCommand() {
        Player player = Tools.GetPlayer(sender);
        if (player == null)
            return false;
        //To find whether the corresponding input command task
        InputPswTask info = null;
        for (InputPswTask tmp : plugin.waitInputPswList) {
            if (tmp.PlayerName.equals(player.getName())) {
                info = tmp;
            }
        }
        if (info == null) {
            player.sendMessage("需要右击发送牌后才可以输入密码");
            return false;
        }
        //This password is used as a passwordless identification field
        if(psw.equals("CannotUseThisPassword")){
            player.sendMessage("Cannot use this password, please try another : )");
            return false;
        }
        int maxLength = plugin.cm.config.getInt("misc.password-length");
        if(psw.length()>maxLength){
            player.sendMessage("Your password length needs to be less than 16");
            return false;
        }
        //Begin Send
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType()== Material.AIR){
            player.sendMessage("Your main hand needs to have an item");
            return false;
        }
        String itemBase64 = ItemStackUtils.itemToBase64(item);
        player.getInventory().setItemInMainHand(null);
        Date date = new Date();
        TransData data = new TransData();
        data.SetTransData(plugin.cm.localInfo.getName(),
                player.getUniqueId().toString(), itemBase64,
                psw,date.getTime(),false);
        Gson gson = new Gson();
        //trans-id
        String transId = Tools.GetRandomString(16);
        IXPData ixpData = info.data;
        serverIds targetServer = null;
        for(serverIds server : plugin.cm.server){
            if(server.getName().equals(ixpData.getToServer())){
                targetServer = server;
            }
        }
        if(targetServer == null){
            player.sendMessage("未找到目标服务器信息");
            plugin.waitInputPswList.remove(info);
            return false;
        }
        //Deducting money
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(player.getUniqueId());
        plugin.economy.withdrawPlayer(offlinePlayer,ixpData.getFee());
        //head
        Map<String,String> head = new HashMap<>();
        head.put("x-ixp-psk",targetServer.getPsk());
        String url = "http://"+targetServer.getIp()+":"+targetServer.getPort()+"/ix/v1/"+ixpData.getToServer()+"/"+transId;
        HttpClient.postJson(url, head, gson.toJson(data),new IXPCallBack(plugin,data));
        plugin.waitInputPswList.remove(info);
        return true;
    }
}
