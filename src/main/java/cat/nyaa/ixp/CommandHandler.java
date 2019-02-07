package cat.nyaa.ixp;

import cat.nyaa.nyaacore.CommandReceiver;
import cat.nyaa.nyaacore.ILocalizer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

    @SubCommand("sign")
    public void signCommand(CommandSender sender, Arguments arguments){
        if (isAdmin(sender)){

        }else {
            sender.sendMessage("只有管理员具有管理权限");
        }
    }

    @SubCommand("inv")
    public void invCommand(CommandSender sender, Arguments arguments){

    }

    @SubCommand("pass")
    public void passCommand(CommandSender sender, Arguments arguments){
        String passwd = arguments.nextString();
        //todo check passwd
    }

    private boolean isAdmin(CommandSender sender){
        return sender.hasPermission("ixp.admin");
    }
}
