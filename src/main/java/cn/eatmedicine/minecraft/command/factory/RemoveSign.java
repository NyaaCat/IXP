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
            sender.sendMessage("You must be a player!\n");
            return false;
        }
        Block block = player.getTargetBlock(null, 20);
        Sign sign = BlockAnalysis.GetSign(block);
        if (sign == null) {
            sender.sendMessage("指向的方块不是一个牌子\n");
            return false;
        }
        IXPData ixp = BlockAnalysis.GetIXP(sign, plugin.getConfig().getString("id"), plugin);
        if (ixp == null) {
            player.sendMessage("该牌子不是IXP牌子\n");
            return false;
        }
        Database db = new Database(plugin);
        SignData sdata = db.selectSign(block.getX(), block.getY(), block.getZ(),
                block.getWorld().getName());
        if (sdata == null || sdata.isEnable == 0) {
            sender.sendMessage("该牌子不在数据库中或已关闭\n");
            return false;
        }
        db.deleteSign(block.getX(), block.getY(), block.getZ(),
                block.getWorld().getName());
        plugin.sm.updateSignData();
        plugin.sm.updateAttachBlockData();
        sender.sendMessage("删除成功\n");
        return true;
    }
}
