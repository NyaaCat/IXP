package cn.eatmedicine.minecraft.command.factory;


import cat.nyaa.nyaacore.utils.ItemStackUtils;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.Database.TransData;
import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.Main;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GetItemByPsw implements IHandleCommand{
    public Main plugin;
    public String Psw;
    public Player player;

    public GetItemByPsw(Main plugin, String psw, Player player) {
        this.plugin = plugin;
        Psw = psw;
        this.player = player;
    }

    @Override
    public boolean handleCommand() {
        Database db = new Database(plugin);
        List<TransData> list = db.SelectTransDataByPsw(Psw);
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(player.getUniqueId());
        //Use the command to get the item using the default cost in config.yml
        double defaultReceiveFee = plugin.cm.config.getDouble("fee.receive");
        if(list.size()==0){
            player.sendMessage("There are no items to be acquired");
            return true;
        }
        for(TransData tdata : list){
            //Check if the player account has money to get the next item
            if(plugin.economy.has(offlinePlayer,defaultReceiveFee)==false){
                player.sendMessage("You don't have enough money to get the next item");
                return false;
            }
            Inventory inventory = player.getInventory();
            if(inventory.firstEmpty()==-1){
                player.sendMessage("Your inventory already full£¬please clear your inventory and try again");
                break;
            }
            ItemStack item = ItemStackUtils.itemFromBase64(tdata.ItemData);
            db.deleteTransData(tdata.SenderUuid,tdata.TimeStamp);
            inventory.addItem(item);
            //Take money
            plugin.economy.withdrawPlayer(offlinePlayer,defaultReceiveFee);
        }
        return true;
    }
}
