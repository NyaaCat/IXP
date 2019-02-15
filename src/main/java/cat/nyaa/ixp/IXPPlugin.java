package cat.nyaa.ixp;

import cat.nyaa.ixp.conf.Configuration;
import cat.nyaa.ixp.sign.SignManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class IXPPlugin extends JavaPlugin {
    I18n i18n;
    Configuration config;

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        config = new Configuration(this);
        config.load();
        i18n = new I18n(this,config.lang);
        i18n.load();
        PluginCommand ixp = getCommand("ixp");
        CommandHandler commander = new CommandHandler(this, i18n);
        commander.loadFee(config);
        ixp.setExecutor(commander);
        getServer().getPluginManager().registerEvents(new Events(this),this);
        SignManager.getInstance().setPlugin(this).load();
//        HttpClient.init(2);
    }

    public Configuration getConf(){
        return this.config;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
