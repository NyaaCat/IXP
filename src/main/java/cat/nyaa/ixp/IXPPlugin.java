package cat.nyaa.ixp;

import cat.nyaa.ixp.conf.Configuration;
import cat.nyaa.ixp.eco.EcoManager;
import cat.nyaa.ixp.net.client.Receiver;
import cat.nyaa.ixp.net.client.Sender;
import cat.nyaa.ixp.net.client.TransactionManager;
import cat.nyaa.ixp.net.server.Server;
import cat.nyaa.ixp.sign.SignManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.logging.Level;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class IXPPlugin extends JavaPlugin {
    public static IXPPlugin plugin;
    private SignManager signManager;
    private Sender sender;
    private Receiver receiver;
    private Server server;
    private I18n i18n;
    Configuration config;


    public static IXPPlugin getInstance() {
        return plugin;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.init();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        plugin = this;
        PluginCommand ixp = getCommand("ixp");
        CommandHandler commander = new CommandHandler(this, i18n);
        ixp.setExecutor(commander);
        commander.loadFee(config);
        getServer().getPluginManager().registerEvents(new Events(this),this);
        signManager = SignManager.getInstance();
        signManager.setPlugin(this).load();
        EcoManager.init((getServer().getServicesManager().getRegistration(Economy.class).getProvider()));
        try {
            Receiver.initialize(this, config.http.get("port"));
            receiver = Receiver.getInstance();
            sender = Sender.getInstance();
            server = Server.getInstance();
        } catch (CertificateException | InterruptedException | SSLException e) {
            getLogger().log(Level.WARNING,I18n.format("error.server_config") ,e);
        }
//        HttpClient.init(2);
    }

    private void init() {
        config = new Configuration(this);
        getLogger().log(Level.INFO, "loading server config");
        config.load();
        i18n = new I18n(this,config.lang);
        i18n.load();
        TransactionManager.getInstance();
    }

    public Configuration getConf(){
        return this.config;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.save();
        signManager.shutdown();
        sender.shutdown();
        receiver.shutdown();
        server.shutdown();
        plugin = null;

    }

    private void save() {
        signManager.save();
    }

    public int getSignTimeout() {
        return (int) config.misc.get("password-timeout");
    }

    public double getSendFee() {
        return config.fee.get("send").doubleValue();
    }

    public double getReceiveFee(){
        return config.fee.get("receive").doubleValue();
    }

    public String getPsk() {
        return config.psk;
    }

    public String getPsk(String server){
        return config.serverIds.get(server).psk;
    }

    public int getMaxSlot() {
        Object o = config.misc.get("slot-limit");
        return o == null? 16: (int) o;
    }
}
