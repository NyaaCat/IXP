package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Level;

public class SignManager{
    private static SignManager INSTANCE;
    Map<String, String[]> signMap;

    static {
        INSTANCE = new SignManager();
    }

    private IXPPlugin plugin;

    private SignManager(){
    }

    public SignManager setPlugin(IXPPlugin plugin){
        this.plugin = plugin;
        return this;
    }

    public synchronized static SignManager getInstance() {
        if (INSTANCE == null){
            INSTANCE = new SignManager();
        }
        return INSTANCE;
    }

    public void createSendSign(Location location, Sign sign, Player player, String server, double fee){
        sign.setLine(0,"[IXP]");
        sign.setLine(1,"SEND");
        sign.setLine(2, server);
        sign.setLine(3, String.format("%.2f", fee));
        signMap.put(location.toVector().toString(), sign.getLines());
        String loc = String.format("{world:%s, x:%d, y:%d, z:%d }", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        Bukkit.getLogger().log(Level.INFO, String.format(I18n.format("sign.create_send"), player.getName(),loc));
    }

    public void createReceiveSign(Location location, Sign sign, Player player, String server, double fee){
        sign.setLine(0,"[IXP]");
        sign.setLine(1,"RECEIVE");
        sign.setLine(2, server);
        sign.setLine(3, String.format("%.2f", fee));
        signMap.put(location.toVector().toString(), sign.getLines());
        String loc = String.format("{world:%s, x:%d, y:%d, z:%d }", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        Bukkit.getLogger().log(Level.INFO, String.format(I18n.format("sign.create_receive"), player.getName(),loc));
    }

    public void removeSign(Location location, Sign sign, Player player){
        signMap.remove(location.toString());
    }

    public void load(){
    }

    public void save(){

    }

    public boolean hasSignAt(Location location) {
        return signMap.containsKey(location.toVector().toString());
    }
}
