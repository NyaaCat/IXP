package cat.nyaa.ixp;

import cat.nyaa.ixp.sign.BaseSign;
import cat.nyaa.ixp.sign.SignManager;
import cat.nyaa.ixp.utils.InvUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
@SuppressWarnings("package")
public class Events implements Listener {
    private final IXPPlugin plugin;
    private final Map<Location, BaseSign> signMap;

    Events(IXPPlugin plugin) {
        this.plugin = plugin;
        signMap = new HashMap<>();
    }

    @EventHandler
    public void onClickSign(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()) {
            return;
        }
        Action action = event.getAction();
        Material material = event.getClickedBlock() == null ? Material.AIR : event.getClickedBlock().getBlockData().getMaterial();

        boolean isRightClickSign = action.equals(Action.RIGHT_CLICK_BLOCK)
                && (material.equals(Material.WALL_SIGN)
                || material.equals(Material.SIGN));
        if (isRightClickSign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            if (isIXPSign(sign)) {
                Player player = event.getPlayer();
                BaseSign baseSign = signMap.get(sign.getLocation());
                if (baseSign == null) {
                    baseSign = BaseSign.create(plugin, sign, IXPPlugin.plugin.getSignTimeout());
                    if (baseSign == null) {
                        player.sendMessage(I18n.format("error.broken_sign"));
                        return;
                    }
                    signMap.put(sign.getLocation(), baseSign);
                }
                baseSign.onPlayerRightClick(player, event);
            }
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent event) {
        Material material = event.getBlock().getBlockData().getMaterial();
        if (material.equals(Material.WALL_SIGN) || material.equals(Material.SIGN)) {
            if (SignManager.hasSignAt(event.getBlock().getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
        BlockFace[] faces = BlockFace.values();
        for (BlockFace f :
                faces) {
            Block relative = event.getBlock().getRelative(f);
            if (SignManager.hasSignAt(relative.getLocation())) {
                Material material1 = relative.getBlockData().getMaterial();
                if (material1.equals(Material.WALL_SIGN)) {
                    WallSign blockData = (WallSign) relative.getBlockData();
                    if (blockData.getFacing().equals(f)) {
                        event.setCancelled(true);
                    }
                } else if (material1.equals(Material.SIGN)) {
                    if (f.equals(BlockFace.UP)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPutToTempChest(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player) {
            Player player = (Player) whoClicked;
            switch (action) {
                case PLACE_ALL:
                case PLACE_SOME:
                case PLACE_ONE:
                case SWAP_WITH_CURSOR:
                case DROP_ALL_SLOT:
                case DROP_ONE_SLOT:
                case HOTBAR_SWAP:
                case HOTBAR_MOVE_AND_READD:
                    Inventory clickedInventory = event.getClickedInventory();
                    if (clickedInventory.equals(InvUtils.getTempInv(player))) {
                        event.setCancelled(true);
                    }
                    break;
                case MOVE_TO_OTHER_INVENTORY:
                    Inventory topInventory = event.getInventory();
                    clickedInventory = event.getClickedInventory();
                    if (!clickedInventory.equals(topInventory)) {
                        if (topInventory.equals(InvUtils.getTempInv(player))) {
                            event.setCancelled(true);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isIXPSign(Sign sign) {
//        return "[ixp]".equalsIgnoreCase(sign.getLine(0)) &&
//                ("SEND".equalsIgnoreCase(sign.getLine(1)) || "RECEIVE".equalsIgnoreCase(sign.getLine(1)));
        return SignManager.hasSignAt(sign.getBlock().getLocation());
    }
}
