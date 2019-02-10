package cn.eatmedicine.minecraft.listener;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.IXPData.IXPType;
import cn.eatmedicine.minecraft.config.serverIds;
import cn.eatmedicine.minecraft.utils.*;

public class SignChangeEventListener implements Listener {
    private final Main plugin;

    public SignChangeEventListener(Main plugin) {
        this.plugin = plugin;
        //this.signData = Tools.loadFile(plugin, "sign.yml");
        //signFile = new File(plugin.getDataFolder(),"sign.yml");
    }

    @EventHandler
    public void SignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        String[] lines = event.getLines();
        if(lines[0].equals("[IXP]")==false){
            return;
        }
        Player player = event.getPlayer();
        if (player.hasPermission("ixp.admin") == false) {
            player.sendMessage("无权限创建IXP牌子");
            return;
        }
        IXPData ixp = BlockAnalysis.GetIXP(lines, plugin.getConfig().getString("id"), plugin);
        if (ixp == null) {
            player.sendMessage("IXP牌子格式错误");
            return;
        }
        //如果是发送牌，检测发送牌是否在服务器配置里*
        if(ixp.getType()==IXPType.SEND){
            serverIds server = plugin.cm.hasServer(lines[2]);
            if (server == null && ixp.getType() != IXPType.RECEIVE) {
                player.sendMessage("该服务器名无效");
                return;
            }
            if (server.isEnable() == false) {
                player.sendMessage("该服务器名不存在或已注销");
                return;
            }
        }
        player.sendMessage("创建一个IXP牌子");
        Location site = block.getLocation();
        Database db = new Database(plugin);
        String worldName = block.getWorld().getName();
        //如果是接收牌
        if (ixp.getType() == IXPType.RECEIVE)
            db.addSign(site.getBlockX(), site.getBlockY(), site.getBlockZ(), worldName, null, 1, ixp.getFee(), 1);
        else
            db.addSign(site.getBlockX(), site.getBlockY(), site.getBlockZ(), worldName, lines[2], 0, ixp.getFee(), 1);
        plugin.sm.updateSignData();
        plugin.sm.updateAttachBlockData();
    }
}
