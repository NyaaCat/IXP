package cn.eatmedicine.minecraft.http;

import cat.nyaa.nyaacore.http.client.HttpClient;
import cat.nyaa.nyaacore.utils.ItemStackUtils;
import cn.eatmedicine.minecraft.Database.TransData;
import cn.eatmedicine.minecraft.Main;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;


public class IXPCallBack implements HttpClient.HttpCallback {
    public Main plugin;
    public TransData tdata;

    public IXPCallBack(Main plugin, TransData tdata) {
        this.plugin = plugin;
        this.tdata = tdata;
    }

    @Override
    public void response(ChannelHandlerContext ctx, FullHttpResponse response, Throwable throwable) {
        int code = response.status().code();
        Player player = Bukkit.getServer().getPlayer(UUID.fromString(tdata.SenderUuid));
        String ErrorMsg = "";
        switch(code){
            case 201:return;
            case 400:ErrorMsg = "Bad Request";break;
            case 401:ErrorMsg = "Authentication Failure";break;
            case 500:ErrorMsg = "Internal plugin error";break;
            case 503:ErrorMsg = "Service unavailable - You don't have enough slots on the remote server";break;
            default:
                ErrorMsg = ("Send Item Error:"+code);
                break;
        }
        player.sendMessage(ErrorMsg);
        player.getInventory().addItem(ItemStackUtils.itemFromBase64(tdata.ItemData));
    }
}
