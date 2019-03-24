package cn.eatmedicine.minecraft.utils;

import cn.eatmedicine.minecraft.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

/**
 * Created by Enzo Cotter on 2019/3/24.
 */
public class LangUtils {
    private FileConfiguration Lang = null;
    private File LangFile = null;
    private Main plugin = null;
    private String LangFileName = "en_US";
    public LangUtils(Main plugin,String langFileName){
        this.plugin = plugin;
        this.LangFileName = langFileName;
        reloadLang();
    }

    public String format(String path,Object... arg){
        String str = Lang.getString(path);
        if(str==null)
            return "Cannot Find Language Content："+path;
        return String.format(str,arg);
    }

    public void reloadLang(){
        if (LangFile == null) {
            LangFile = new File(plugin.getDataFolder(), LangFileName+".yml");
        }
        if(!LangFile.exists()){
            saveDefaultConfig();
        }
        Lang = YamlConfiguration.loadConfiguration(LangFile);
/*
        // Look for defaults in the jar
        Reader defConfigStream = null;
        try{
            defConfigStream = new InputStreamReader(plugin.getResource(LangFileName+".yml"), "UTF8");
        }
        catch (Exception ex){
            plugin.getLogger().info("Cannot load default Language file");
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            Lang.setDefaults(defConfig);
        }*/
    }
    public FileConfiguration getLang() {
        if (Lang == null) {
            reloadLang();
        }
        return Lang;
    }

    public void saveDefaultConfig() {
        if (LangFile == null) {
            LangFile = new File(plugin.getDataFolder(), LangFileName+".yml");
        }
        if (!LangFile.exists()) {
            plugin.saveResource(LangFileName+".yml", false);
        }
    }

}
