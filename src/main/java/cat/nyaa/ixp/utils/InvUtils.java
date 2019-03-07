package cat.nyaa.ixp.utils;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.nyaacore.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class InvUtils {
    private static final Map<Player, Inventory> tempInvMap;

    static {
        tempInvMap = new HashMap<>();
    }

    public static boolean playerHasSpace(Player player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();
        Inventory enderChest = player.getEnderChest();
        return InventoryUtils.hasEnoughSpace(inventory, itemStack, itemStack.getAmount())
                && InventoryUtils.hasEnoughSpace(enderChest, itemStack, itemStack.getAmount());
    }

    public static boolean addItemToPlayer(Player player, ItemStack itemStack) {
        try {
            PlayerInventory inventory = player.getInventory();
            Inventory enderChest = player.getEnderChest();
            int amount = itemStack.getAmount();
            if (InventoryUtils.hasEnoughSpace(inventory, itemStack, amount)) {
                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack is = inventory.getItem(i);
                    if (is == null) {
                        player.sendMessage(I18n.format("inv.success_inv"));
                        inventory.setItem(i, itemStack);
                        return true;
                    }
                }
                InventoryUtils.addItem(inventory, itemStack);
                player.sendMessage(I18n.format("inv.success_inv"));
                return true;
            } else if (InventoryUtils.hasEnoughSpace(enderChest, itemStack, amount)) {
                InventoryUtils.addItem(enderChest, itemStack);
                player.sendMessage(I18n.format("inv.success_ender"));
                return true;
            } else {
                Inventory tempInv = iGettempinv(player);
                if (InventoryUtils.hasEnoughSpace(tempInv, itemStack)) {
                    InventoryUtils.addItem(tempInv, itemStack);
                    player.sendMessage(I18n.format("inv.success_temp"));
                    player.openInventory(tempInv);
                    return true;
                } else {
                    player.sendMessage(I18n.format("error.no_space"));
                    return false;
                }
            }
        } catch (Exception e) {
            IXPPlugin.plugin.getLogger().log(Level.SEVERE, I18n.format("error.internal"), e);
            player.sendMessage(I18n.format("error.internal"));
            return false;
        }
    }

    public static Inventory getTempInv(Player player) {
        return iGettempinv(player);
    }

    private static Inventory iGettempinv(Player player) {
        Inventory itemStacks = tempInvMap.get(player);
        if (itemStacks == null) {
            itemStacks = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, I18n.format("chest.temp_title"));
            tempInvMap.put(player, itemStacks);
        }
        return itemStacks;
    }
}
