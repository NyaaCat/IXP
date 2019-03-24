package cn.eatmedicine.minecraft.command.factory;

import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Enzo Cotter on 2019/2/10.
 */
public class ClearAllItem implements IHandleCommand{
    public Main plugin;
    public CommandSender player;
    public String ClearPlayerName;

    public ClearAllItem(Main plugin, CommandSender player, String clearPlayerName) {
        this.plugin = plugin;
        this.player = player;
        ClearPlayerName = clearPlayerName;
    }

    @Override
    public boolean handleCommand() {
        Database db = new Database(plugin);
        Player acquirePlayer = plugin.getServer().getPlayer(ClearPlayerName);
        if(acquirePlayer==null){
            player.sendMessage(plugin.lang.format("message.command.not_find_player"));
            return false;
        }
        String Uuid = acquirePlayer.getUniqueId().toString();
        int count = db.deleteAllTransData(Uuid);
        player.sendMessage(plugin.lang.format("message.command.success_delete_item",count));
        return false;
    }
}
