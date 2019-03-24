package cn.eatmedicine.minecraft.command.factory;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.config.serverIds;
import cn.eatmedicine.minecraft.utils.BlockAnalysis;

public class CreateReceiveSign implements IHandleCommand {
    private Main plugin;
    CommandSender sender;

    public CreateReceiveSign(Main plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    @Override
    public boolean handleCommand() {
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
        db.addSign(site.getBlockX(), site.getBlockY(), site.getBlockZ(), worldName, null, 0, ixp.getFee(), 1);
        plugin.sm.updateSignData();
        plugin.sm.updateAttachBlockData();

        player.sendMessage(plugin.lang.format("message.command.ixpSign_create_success"));
        return true;
    }
}
