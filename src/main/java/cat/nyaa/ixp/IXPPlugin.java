package cat.nyaa.ixp;

import cat.nyaa.ixp.conf.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
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
        ixp.setExecutor(new CommandHandler(this,i18n));
        getServer().getPluginManager().registerEvents(new EventHandler(this),this);
    }

    public Configuration getConf(){
        return this.config;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
