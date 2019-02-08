package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.nyaacore.configuration.ISerializable;
import cat.nyaa.nyaacore.utils.ConcurrentUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Level;

public class SignManager{
    private static SignManager INSTANCE;
    Map<String, Sign> signMap;

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

    public void createSendSign(Location location, Sign sign, Player player){

    }

    public void createReceiveSign(Location location, Sign sign, Player player){
        Bukkit.getLogger().log(Level.INFO, String.format(I18n.format("sign.create_send"), ));
    }

    public void removeSign(Location location, Sign sign, Player player){

    }

    public void load(){
    }

    public void save(){

    }
}
