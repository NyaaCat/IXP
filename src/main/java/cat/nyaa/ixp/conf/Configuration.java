package cat.nyaa.ixp.conf;

import cat.nyaa.nyaacore.configuration.PluginConfigure;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class Configuration extends PluginConfigure {
    JavaPlugin plugin;

    @Serializable
    public String lang = null;
    @Serializable
    public String id;
    @Serializable
    public String psk;
    @Serializable
    public Map<String, String> http;
    @Serializable(name = "server-ids")
    public Map<String, ServerId> serverIds;
    @Serializable
    public Map<String, Double> fee;
    @Serializable
    public Map<String, String> misc;


    public Configuration(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected JavaPlugin getPlugin() {
        return plugin;
    }
}