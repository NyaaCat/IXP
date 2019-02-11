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
            sender.sendMessage("You must be a player!");
            return false;
        }
        Block block = player.getTargetBlock(null, 20);
        Sign sign = BlockAnalysis.GetSign(block);
        if (sign == null) {
            sender.sendMessage("指向的方块不是一个牌子");
            return false;
        }

        IXPData ixp = BlockAnalysis.GetIXP(sign, plugin.getConfig().getString("id"), plugin);
        if (ixp == null) {
            player.sendMessage("该牌子不是IXP牌子");
            return false;
        }
        if (player.hasPermission("ixp.admin") == false) {
            player.sendMessage("无权限创建IXP牌子");
            return false;
        }

        Location site = block.getLocation();
        Database db = new Database(plugin);
        String worldName = block.getWorld().getName();
        if (db.selectSign(site.getBlockX(), site.getBlockY(), site.getBlockZ(), worldName) != null) {
            sender.sendMessage("该牌子已存在");
            return false;
        }
        db.addSign(site.getBlockX(), site.getBlockY(), site.getBlockZ(), worldName, null, 0, ixp.getFee(), 1);
        plugin.sm.updateSignData();
        plugin.sm.updateAttachBlockData();

        player.sendMessage("创建了一个IXP牌子");
        return true;
    }
}
