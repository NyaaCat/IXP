package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public abstract class BaseSign {
    protected Sign sign;
    private IXPPlugin plugin;
    protected int tickSinceClick = 0;
    protected int timeout;
    private HashSet<UUID> clickQueue;

    BaseSign(IXPPlugin plugin, Sign sign, int timeout) {
        this.plugin = plugin;
        this.timeout = timeout;
        this.sign = sign;
        clickQueue = new HashSet<>();
    }

    public static BaseSign create(IXPPlugin plugin, Sign sign, int timeout) {
        String type = sign.getLine(1);
        type = type.toUpperCase();
        BaseSign result;
        switch (type) {
            case "SEND":
                result = new SendSign(plugin, sign, timeout);
                break;
            case "RECEIVE":
                result = new ReceiveSign(plugin, sign, timeout);
                break;
            default:
                throw new RuntimeException();
        }
        return result;
    }

    public abstract String getType();

    public abstract void onSingleClick(Player player);

    public abstract void onDoubleClick(Player player, PlayerInteractEvent event);

    public abstract void onPassword(Player player, boolean correct);

    public void onPlayerRightClick(Player player, PlayerInteractEvent event) {
        UUID uuid = player.getUniqueId();
        if (!clickQueue.contains(uuid)) {
            clickQueue.add(uuid);
            onSingleClick(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    onTimeExceeded(player);
                }
            }.runTaskLater(plugin, 20*timeout);
        } else {
            clickQueue.remove(uuid);
            onDoubleClick(player, event);
        }
    }

    private void onTimeExceeded(Player player) {
        if (clickQueue.contains(player.getUniqueId())) {
            player.sendMessage(I18n.format("sign.timeout"));
            clickQueue.remove(player.getUniqueId());
        }
    }

}
