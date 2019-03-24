package cn.eatmedicine.minecraft.sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.Database.SignData;
import cn.eatmedicine.minecraft.utils.BlockAnalysis;

public class SignManager {
    private final JavaPlugin plugin;
    public List<SignData> signData = new ArrayList<>();
    public HashMap<Location, Block> attachBlockData = new HashMap<>();

    public SignManager(JavaPlugin plugin) {
        this.plugin = plugin;
        updateSignData();
    }

    public void updateSignData() {
        Database db = new Database(plugin);
        //Used to activate the database to prevent a database table from generating bugs
        db.InitTransData();

        signData = db.selectAllSign();
        updateAttachBlockData();
    }

    public void updateAttachBlockData() {
        attachBlockData = new HashMap<>();
        if (signData.isEmpty() == true)
            return;
        for (SignData s : signData) {
            Location l = new Location(Bukkit.getWorld(s.world), s.x, s.y, s.z);
            Block attach = getAttachedBlock(l.getBlock());
            attachBlockData.put(l, attach);
        }
    }

    //获取牌子所附着的方块
    public static Block getAttachedBlock(Block block) {
        Sign s = BlockAnalysis.GetSign(block);
        if (s != null) {
            org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getState().getData();
            return block.getRelative(sign.getAttachedFace());
        }
        return null;
    }

    //如果block是一个已记录的IXP牌子，获取其SignData
    public SignData getIXPSign(Block block) {
        if (block == null)
            return null;
        for (SignData sd : signData) {
            Location local = block.getLocation();
            if (sd.x == local.getBlockX() &&
                    sd.y == local.getBlockY() &&
                    sd.z == local.getBlockZ() &&
                    sd.world.equals(block.getWorld().getName())) {
                return sd;
            }
        }
        return null;
    }

    //block是否是一个已经记录的IXP牌子
    public boolean isIXPSign(Block block) {
        if (block == null)
            return false;
        for (SignData sd : signData) {
            if (sd.x == block.getX() &&
                    sd.y == block.getY() &&
                    sd.z == block.getZ() &&
                    sd.world.equals(block.getWorld().getName())) {
                return true;
            }
        }
        return false;
    }

    //判断Block是否是在被IXP附着
    public boolean isAttachBlock(Block block) {
        if (block == null)
            return false;
        return attachBlockData.containsValue(block);
    }
}
