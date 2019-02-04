package cat.nyaa.ixp;

import cat.nyaa.nyaacore.CommandReceiver;
import cat.nyaa.nyaacore.ILocalizer;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class CommandHandler extends CommandReceiver {

    public CommandHandler(JavaPlugin plugin, ILocalizer i18n) {
        super(plugin, i18n);
    }

    @Override
    public String getHelpPrefix() {
        return "";
    }
}
