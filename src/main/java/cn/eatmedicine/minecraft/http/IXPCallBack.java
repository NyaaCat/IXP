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
        plugin.getLogger().info(code+"");
        Player player = Bukkit.getServer().getPlayer(UUID.fromString(tdata.SenderUuid));
        player.sendMessage("Code:"+code);
        switch(code){
            case 201:break;
            case 400:
            case 401:
            case 500:
            case 503:
            default:
                player.sendMessage("Send Item Error:"+code);
                player.getInventory().addItem(ItemStackUtils.itemFromBase64(tdata.ItemData));
                break;
        }
    }
}
