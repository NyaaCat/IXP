package cn.eatmedicine.minecraft.utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import cn.eatmedicine.minecraft.task.ClickTask;
import cn.eatmedicine.minecraft.Database.SignData;
import cn.eatmedicine.minecraft.IXPData.IXPData;

public class Tools {

    // 在list中寻找和data匹配的task
    public static ClickTask findTask(List<ClickTask> list, IXPData data, Player player) {
        Iterator<ClickTask> iter = list.iterator();
        while (iter.hasNext()) {
            ClickTask tmp = iter.next();
            IXPData ixp = tmp.getData();
            if (ixp.equals(data) == true && tmp.getPlayer().equals(player)) {
                return tmp;
            }
        }
        return null;
    }

    public static FileConfiguration loadFile(JavaPlugin plugin, String fileName) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.saveResource(fileName, true);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        FileConfiguration config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
        return config;
    }

    public static boolean updateSign(Sign sign, SignData data) {
        sign.setLine(0, "[IXP]");
        if (data.type == 0) {
            sign.setLine(1, "SEND");
            sign.setLine(2, data.server);
        } else
            sign.setLine(1, "RECEIVE");
        sign.setLine(3, data.fee + "");
        sign.update();
        return true;
    }

    public static Player GetPlayer(CommandSender sender){
        if(sender instanceof Player){
            Player player = (Player) sender;
            return player;
        }
        else{
            return null;
        }
    }

    public static String GetRandomString(int length){
        StringBuffer sb = new StringBuffer();
        for (int count=0;count<length;count++){
            sb.append(GetRandomChar());
        }
        return sb.toString();
    }

    private static char GetRandomChar(){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rad = new Random();

        return str.charAt(rad.nextInt(62));

    }

    public static void SendHelpMsg(CommandSender sender){
        if (sender.hasPermission("IXP.admin")) {
            sender.sendMessage("/ixp sign create send [server-id] -- Register a send out (outbound) "
                    + "IXP sign that send items to the specific server");
            sender.sendMessage("/ixp sign create receive -- Register a receive IXP sign that "
                    + "receives items in current server\n");
            sender.sendMessage("/ixp sign remove --Unregister the facing IXP sign\n");
            sender.sendMessage("/ixp inv acquire [player] -- Acquire all items of a "
                    + "specific player (sender)\n");
            sender.sendMessage("/ixp inv clear [player] -- Clear all items of a "
                    + "specific player (sender)\n");
        }
        sender.sendMessage("/ixp spass [password] -- 输入发送密码\n");
        sender.sendMessage("/ixp rpass [password] -- 输入接收密码\n");
        return;
    }

}
