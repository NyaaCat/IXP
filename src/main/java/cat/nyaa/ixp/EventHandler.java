package cat.nyaa.ixp;

import org.bukkit.block.Sign;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class EventHandler implements Listener {
    private IXPPlugin plugin;

    EventHandler(IXPPlugin plugin){
        this.plugin = plugin;
    }

    @org.bukkit.event.EventHandler
    public void onClickSign(PlayerInteractEvent event){
        Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Sign sign = (Sign) event.getClickedBlock();
            if (isIXPSign(sign)){
                //todo
            }
        }
    }

    private boolean isIXPSign(Sign sign) {
        return "[ixp]".equalsIgnoreCase(sign.getLine(0));
    }
}
