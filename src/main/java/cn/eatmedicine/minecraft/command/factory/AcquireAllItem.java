package cn.eatmedicine.minecraft.command.factory;

import cat.nyaa.nyaacore.utils.ItemStackUtils;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.Database.TransData;
import cn.eatmedicine.minecraft.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.xml.crypto.Data;
import java.util.List;

public class AcquireAllItem implements IHandleCommand{
    public Main plugin;
    public Player player;
    public String AcquirePlayerName;

    public AcquireAllItem(Main plugin, Player player, String acquirePlayerName) {
        this.plugin = plugin;
        this.player = player;
        AcquirePlayerName = acquirePlayerName;
    }


    @Override
    public boolean handleCommand() {
        Player acquirePlayer = plugin.getServer().getPlayer(AcquirePlayerName);
        if(acquirePlayer==null){
             player.sendMessage("Could not find this Player");
             return false;
        }
        String Uuid = acquirePlayer.getUniqueId().toString();
        Database db = new Database(plugin);
        List<TransData> list = db.SelectTransDataByUuid(Uuid);
        if(list.size()==0){
            player.sendMessage("There are no items to be acquired");
            return false;
        }
        for(TransData tdata : list){
            Inventory inventory = player.getInventory();
            if(inventory.firstEmpty()==-1){
                player.sendMessage("Your inventory already full£¬please clear your inventory and try again");
                break;
            }
            ItemStack item = ItemStackUtils.itemFromBase64(tdata.ItemData);
            db.deleteTransData(tdata.SenderUuid,tdata.TimeStamp);
            inventory.addItem(item);
        }
        return true;
    }
}
