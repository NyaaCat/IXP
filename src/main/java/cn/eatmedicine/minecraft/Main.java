package cn.eatmedicine.minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cat.nyaa.nyaacore.http.server.TinyHttpServer;
import cat.nyaa.nyaacore.utils.VaultUtils;
import cn.eatmedicine.minecraft.http.IXPResponder;
import cn.eatmedicine.minecraft.task.InputPswTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
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
    public Economy economy = null;

    @Override
    public void onEnable() {
        getLogger().info("IXP插件正在加载");
        loadConfig();
        sm = new SignManager(this);
        cm = new ConfigManager(this);
        waitInputPswList = new ArrayList<>();
        economy = VaultUtils.getVaultEconomy();
        if(initVault()==false){
            this.getLogger().info("Cannot find Vault plugin, Init fail");
            return;
        }


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

    public boolean initVault(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            if ((economy = economyProvider.getProvider()) == null){
                return false;
            }
            else
                return true;
        }
        return false;
    }

}
