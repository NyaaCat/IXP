package cn.eatmedicine.minecraft.command.factory;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.Database.SignData;
import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.utils.BlockAnalysis;

public class RemoveSign implements IHandleCommand {
    private Main plugin;
    CommandSender sender;

    public RemoveSign(Main plugin, CommandSender sender) {
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
        Database db = new Database(plugin);
        SignData sdata = db.selectSign(block.getX(), block.getY(), block.getZ(),
                block.getWorld().getName());
        if (sdata == null || sdata.isEnable == 0) {
            sender.sendMessage(plugin.lang.format("message.command.ixpSign_not_find_database"));
            return false;
        }
        db.deleteSign(block.getX(), block.getY(), block.getZ(),
                block.getWorld().getName());
        plugin.sm.updateSignData();
        plugin.sm.updateAttachBlockData();
        sender.sendMessage(plugin.lang.format("message.command.ixpSign_delete_success"));
        return true;
    }
}
