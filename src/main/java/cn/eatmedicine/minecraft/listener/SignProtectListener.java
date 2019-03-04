package cn.eatmedicine.minecraft.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.Database.SignData;
import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.utils.BlockAnalysis;

public class SignProtectListener implements Listener {

    private final Main plugin;

    public SignProtectListener(Main plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("IXP牌子保护加载");
    }

    //保护方块破坏 包括牌子本身和依靠物
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        Player player = event.getPlayer();
        //破坏的是否是牌子
        Sign s = BlockAnalysis.GetSign(block);
        if (s != null) {
            //判断牌子是否是要包含的IXP牌子
            boolean tmp = plugin.sm.isIXPSign(block);
            if (tmp) {
                //如果破坏者有权限就可以破坏
                //破坏后删除数据库中的信息并且更新
                if (player.hasPermission("ixp.admin")) {
                    Database db = new Database(plugin);
                    db.deleteSign(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
                    plugin.sm.updateSignData();
                    plugin.sm.updateAttachBlockData();
                } else {
                    event.setCancelled(true);
                    return;
                }
            }

        }
        if (plugin.sm.isAttachBlock(block)) {
            event.setCancelled(true);
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.sm.isIXPSign(block) || plugin.sm.isAttachBlock(block));
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.sm.isIXPSign(block) || plugin.sm.isAttachBlock(block));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        if (plugin.sm.isIXPSign(event.getBlock()) || plugin.sm.isAttachBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (plugin.sm.isAttachBlock(block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (plugin.sm.isAttachBlock(block)) {
                event.setCancelled(true);
            }
        }
    }

}
