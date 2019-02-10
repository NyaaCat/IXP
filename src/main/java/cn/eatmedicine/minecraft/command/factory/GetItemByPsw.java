package cn.eatmedicine.minecraft.command.factory;


import cat.nyaa.nyaacore.utils.ItemStackUtils;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.Database.TransData;
import cn.eatmedicine.minecraft.Main;
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
        if(list.size()==0){
            player.sendMessage("There are no items to be acquired");
            return true;
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
