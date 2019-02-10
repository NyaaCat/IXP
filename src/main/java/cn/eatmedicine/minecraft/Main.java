package cn.eatmedicine.minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cat.nyaa.nyaacore.http.server.TinyHttpServer;
import cn.eatmedicine.minecraft.http.IXPResponder;
import cn.eatmedicine.minecraft.task.InputPswTask;
import org.bukkit.plugin.java.JavaPlugin;

import cn.eatmedicine.minecraft.command.IXPCommandExecutor;
import cn.eatmedicine.minecraft.config.ConfigManager;
import cn.eatmedicine.minecraft.listener.PlayerInteractEventListener;
import cn.eatmedicine.minecraft.listener.SignChangeEventListener;
import cn.eatmedicine.minecraft.listener.SignProtectListener;
import cn.eatmedicine.minecraft.sign.SignManager;

public class Main extends JavaPlugin {
    File file = new File(getDataFolder(), "config.yml");
    public SignManager sm;
    public ConfigManager cm;
    public TinyHttpServer tinyHttpServer;
    public List<InputPswTask> waitInputPswList;

    @Override
    public void onEnable() {
        getLogger().info("IXP插件正在加载");
        loadConfig();
        sm = new SignManager(this);
        cm = new ConfigManager(this);
        waitInputPswList = new ArrayList<>();
        getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(this), this);
        getServer().getPluginManager().registerEvents(new SignChangeEventListener(this), this);
        getServer().getPluginManager().registerEvents(new SignProtectListener(this), this);
        this.getCommand("ixp").setExecutor(new IXPCommandExecutor(this));
        try{
            int port = cm.localInfo.getPort();
            tinyHttpServer = new TinyHttpServer(new IXPResponder(this),port,port);
        }
        catch (Exception ex){
            this.getLogger().info("Server has some wrong");
        }
        //test
    }

    public boolean loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!(file.exists())) {
            saveDefaultConfig();
        }
        reloadConfig();
        return true;
    }


}
