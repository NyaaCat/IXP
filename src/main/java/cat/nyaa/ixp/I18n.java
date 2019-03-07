package cat.nyaa.ixp;

import cat.nyaa.nyaacore.LanguageRepository;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class I18n extends LanguageRepository {
    private final JavaPlugin plugin;
    private static I18n instance;
    private final String lang;

    public I18n(JavaPlugin plugin, String lang) {
        this.plugin = plugin;
        this.lang = lang;
        instance = this;
        load();
    }

    public static String format(String s, Object... paras) {
        return instance.getFormatted(s,paras);
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
