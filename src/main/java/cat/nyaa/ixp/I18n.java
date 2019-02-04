package cat.nyaa.ixp;

import cat.nyaa.nyaacore.LanguageRepository;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class I18n extends LanguageRepository {
    JavaPlugin plugin;
    String lang = null;

    public I18n(JavaPlugin plugin, String lang) {
        this.plugin = plugin;
        this.lang = lang;
        load();
    }

    @Override
    protected JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    protected String getLanguage() {
        return lang;
    }
}
