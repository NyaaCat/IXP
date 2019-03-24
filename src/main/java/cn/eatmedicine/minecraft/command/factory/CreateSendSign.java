package cn.eatmedicine.minecraft.command.factory;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.IXPData.IXPType;
import cn.eatmedicine.minecraft.config.serverIds;
import cn.eatmedicine.minecraft.utils.BlockAnalysis;

public class CreateSendSign implements IHandleCommand {
    public String serverName;
    private Main plugin;
    CommandSender sender;

    public CreateSendSign(Main plugin, CommandSender sender, String serverName) {
        this.plugin = plugin;
        this.sender = sender;
        this.serverName = serverName;
    }

    @Override
    public boolean handleCommand() {
        //如果是发送牌，检测发送牌是否在服务器配置里
        serverIds server = plugin.cm.hasServer(serverName);
        if (server == null) {
            sender.sendMessage(plugin.lang.format("message.command.not_find_server"));
            return false;
        }
        if (server.isEnable() == false) {
            sender.sendMessage(plugin.lang.format("message.command.server_disabled"));
            return false;
        }
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(plugin.lang.format("message.command.only_player_can_use"));
            return false;
        }
        Block block = player.getTargetBlock(null, 20);
        Sign sign = BlockAnalysis.GetSign(block);
        if (sign == null) {
            sender.sendMessage(plugin.lang.format("message.command.pointing_square_not_ixp"));
            return false;
        }

        IXPData ixp = BlockAnalysis.GetIXP(sign, plugin.getConfig().getString("id"), plugin);
        if (ixp == null) {
            player.sendMessage(plugin.lang.format("message.command.sign_not_ixpSign"));
            return false;
        }
        if (player.hasPermission("ixp.admin") == false) {
            player.sendMessage(plugin.lang.format("message.command.not_permission_create_ixpSign"));
            return false;
        }
        Location site = block.getLocation();
        Database db = new Database(plugin);
        String worldName = block.getWorld().getName();
        if (db.selectSign(site.getBlockX(), site.getBlockY(), site.getBlockZ(), worldName) != null) {
            sender.sendMessage(plugin.lang.format("message.command.ixpSign_already_exist"));
            return false;
        }
        sign.setLine(2, serverName);
        sign.update();
        db.addSign(site.getBlockX(), site.getBlockY(), site.getBlockZ(), worldName, serverName, 0, ixp.getFee(), 1);
        plugin.sm.updateSignData();
        plugin.sm.updateAttachBlockData();

        player.sendMessage(plugin.lang.format("message.command.ixpSign_create_success"));
        return true;
    }

}
