package cat.nyaa.ixp;

import cat.nyaa.ixp.sign.BaseSign;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class EventHandler implements Listener {
    private IXPPlugin plugin;
    private Map<Location, BaseSign> signMap;

    EventHandler(IXPPlugin plugin){
        this.plugin = plugin;
        signMap = new HashMap<>();
    }

    @org.bukkit.event.EventHandler
    public void onClickSign(PlayerInteractEvent event){
        Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getBlockData().getMaterial().equals(Material.WALL_SIGN)) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            if (isIXPSign(sign)){
                Player player = event.getPlayer();
                BaseSign baseSign = signMap.get(sign.getLocation());
                if ( baseSign == null){
                    baseSign = BaseSign.create(plugin, sign, 5);
                    signMap.put(sign.getLocation(),baseSign);

                }
                baseSign.onPlayerRightClick(player);
            }
        }
    }

    private boolean isIXPSign(Sign sign) {
        return "[ixp]".equalsIgnoreCase(sign.getLine(0)) &&
                ("SEND".equalsIgnoreCase(sign.getLine(1)) || "RECEIVE".equalsIgnoreCase(sign.getLine(1)));
    }
}
