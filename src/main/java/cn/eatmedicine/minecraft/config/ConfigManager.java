package cn.eatmedicine.minecraft.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    public final JavaPlugin plugin;
    public FileConfiguration config;
    private ConfigurationSection serverIds;
    public List<serverIds> server;
    public serverIds localInfo;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        update();
    }

    public boolean update() {
        config = plugin.getConfig();
        localInfo = new serverIds(config.getString("id"),
                "localhost",
                config.getInt("http.port"),
                config.getString("psk"), true);
        server = new ArrayList<serverIds>();
        serverIds = config.getConfigurationSection("serverIds");
        Set<String> setTmp = config.getConfigurationSection("serverIds").getKeys(false);
        String[] serverid = setTmp.toArray(new String[setTmp.size()]);
        for (String str : serverid) {
            String address = config.getString("serverIds." + str + ".address");
            String ip = address.split(":")[0];
            int port = Integer.parseInt(address.split(":")[1]);
            String psk = config.getString("serverIds." + str + ".psk");
            boolean isEnable = config.getBoolean("serverIds." + str + ".enabled");
            plugin.getLogger().info(str + "|" + ip + "|" + port + "|" + psk + "|" + isEnable + "\n");
            serverIds tmp = new serverIds(str, ip, port, psk, isEnable);
            server.add(tmp);
        }
        plugin.getLogger().info("Have"+server.size()+"Server");
        return true;

    }

    public serverIds hasServer(String serverName) {
        for (serverIds s : server) {
            if (s.getName().equals(serverName) == true)
                return s;
        }
        return null;
    }

    public boolean addServer(serverIds server) {
        if (server.isRight() == false)
            return false;
        config = plugin.getConfig();
        config.set("serverIds." + server.getName() + ".address", server.getIp() + ":" + server.getPort());
        config.set("serverIds." + server.getName() + ".psk", server.getPsk());
        config.set("serverIds." + server.getName() + ".enabled", server.isEnable());
        plugin.saveConfig();
        return true;
    }

    public boolean delServer(String serverName) {
        if (hasServer(serverName) == null)
            return false;
        plugin.getConfig().set("serverIds." + serverName + ".enabled", false);
        return true;
    }


}
