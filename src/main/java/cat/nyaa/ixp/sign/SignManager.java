package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Level;

public class SignManager {
    private static SignManager INSTANCE;
    private Map<String, List<String>> signMap;
    private final Map<UUID, BaseSign> clickQueue;
    private final Map<UUID, BukkitTask> cancelTask;
//    private KeyValueDB<String, String[]> signDB;

    static {
        INSTANCE = new SignManager();
    }

    private static IXPPlugin plugin;

    private SignManager() {
        signMap = new HashMap<>();
        clickQueue = new HashMap<>();
        cancelTask = new HashMap<>();
    }

    public void onPassword(Player player, String passwd){
        BaseSign sign = clickQueue.get(player.getUniqueId());
        if (sign == null){
            player.sendMessage(I18n.format("error.no_sign_click"));
        }else {
            sign.onPassword(player, passwd);
            clickQueue.remove(player.getUniqueId());
        }
    }

    static void onInteract(BaseSign sign, Player player) {
        Map<UUID, BaseSign> clickQueue = INSTANCE.clickQueue;
        BaseSign queueSign = clickQueue.get(player.getUniqueId());
        if (queueSign == null) {
            sign.onSingleClick(player);
            addCancelTask(sign, player);
        } else if (queueSign.equals(sign)) {
            sign.onDoubleClick(player);
            clickQueue.remove(player.getUniqueId());
        } else {
            queueSign.onAbort(player);
            sign.onSingleClick(player);
            addCancelTask(sign, player);
        }
    }

    private static void addCancelTask(BaseSign sign, Player player) {
        INSTANCE.clickQueue.put(player.getUniqueId(), sign);
        BukkitTask previousCancelTask = INSTANCE.cancelTask.get(player.getUniqueId());
        if (previousCancelTask!=null) {
            previousCancelTask.cancel();
            INSTANCE.cancelTask.remove(player.getUniqueId());
        }
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                onTimeExceeded(sign, player);
            }
        }.runTaskLater(plugin, 20 * sign.timeout);
        INSTANCE.cancelTask.put(player.getUniqueId(), bukkitTask);
    }

    private static void onTimeExceeded(BaseSign sign, Player player) {
        synchronized (INSTANCE.clickQueue) {
            Map<UUID, BaseSign> clickQueue = INSTANCE.clickQueue;
            BaseSign baseSign = clickQueue.get(player.getUniqueId());
            if (baseSign != null && baseSign.equals(sign)) {
                clickQueue.remove(player.getUniqueId());
                sign.onTimeExceeded(player);
            }
        }
    }

    public static void tryRepair(Sign sign) {
        Location location = sign.getLocation();
        if (hasSignAt(location)) {
            List<String> strings = INSTANCE.signMap.get(getLocationKey(location));
            for (int i = 0; i < strings.size(); i++) {
                sign.setLine(i, strings.get(i));
            }
            sign.update();
        }
    }

    public SignManager setPlugin(IXPPlugin plugin) {
        SignManager.plugin = plugin;
        return this;
    }

    public synchronized static SignManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SignManager();
        }
        return INSTANCE;
    }

    public void createSendSign(Location location, Sign sign, Player player, String server, double fee) {
        String loc = createSign(location, sign, server, fee, "SEND");
        Bukkit.getLogger().log(Level.INFO, I18n.format("log.create_send", player.getName(), loc));
        player.sendMessage(I18n.format("info.create_send"));
        save();
    }

    public void createReceiveSign(Location location, Sign sign, Player player, String server, double fee) {
        String loc = createSign(location, sign, server, fee, "RECEIVE");
        Bukkit.getLogger().log(Level.INFO, I18n.format("log.create_receive", player.getName(), loc));
        player.sendMessage(I18n.format("info.create_receive"));
        save();
    }

    private String createSign(Location location, Sign sign, String server, double fee, String type) {
        sign.setLine(0, "[IXP]");
        sign.setLine(1, type);
        sign.setLine(2, server);
        sign.setLine(3, String.format("%.2f", fee));
        signMap.put(getLocationKey(location), Arrays.asList(sign.getLines()));
        sign.update();
        return String.format("{world:%s, x:%d, y:%d, z:%d }", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void removeSign(Location location, Sign sign, Player player) {
        signMap.remove(getLocationKey(location));
    }

    private static String getLocationKey(Location location) {
        Vector vector = location.toVector();
        return String.format("world:%s,%d,%d,%d", location.getWorld().getName(), vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public void load() {
        SignInfo signs = new SignInfo();
        signs.load();
        signMap = signs.getMap();
    }

    public void save() {
        SignInfo configuration = new SignInfo();
        configuration.setMap(signMap);
        configuration.save();
    }

    public static boolean hasSignAt(Location location) {
        return INSTANCE.signMap.containsKey(getLocationKey(location));
    }

    public void shutdown() {
        save();
        INSTANCE = null;
    }
}
