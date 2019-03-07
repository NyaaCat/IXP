package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public abstract class BaseSign {
    final Sign sign;
    protected int tickSinceClick = 0;
    final int timeout;

    BaseSign(Sign sign, int timeout) {
        this.timeout = timeout;
        this.sign = sign;
    }

    public static BaseSign create(IXPPlugin plugin, Sign sign, int timeout) {
        String type = sign.getLine(1);
        type = type.toUpperCase();
        BaseSign result = null;
        switch (type) {
            case "SEND":
                result = new SendSign(sign, timeout);
                break;
            case "RECEIVE":
                result = new ReceiveSign(sign, timeout);
                break;
            default:
                SignManager.tryRepair(sign);
        }
        return result;
    }

    public abstract String getType();

    public abstract void onSingleClick(Player player);

    public abstract void onDoubleClick(Player player);

    protected abstract void iOnPassword(Player player, String correct);

    public void onPlayerRightClick(Player player, PlayerInteractEvent event) {
        SignManager.onInteract(this, player);
//        UUID uuid = player.getUniqueId();
//        if (!clickQueue.contains(uuid)) {
//            clickQueue.add(uuid);
//            onSingleClick(player);
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    onTimeExceeded(player);
//                }
//            }.runTaskLater(plugin, 20*timeout);
//        } else {
//            removeFromQueue(uuid);
//            onDoubleClick(player);
//        }
    }

    void onPassword(Player player, String password){
        iOnPassword(player,password);
    }

    void onTimeExceeded(Player player) {
            player.sendMessage(I18n.format("info.timeout"));
    }

    void onAbort(Player player){
        player.sendMessage("info.abort");
    }
}
