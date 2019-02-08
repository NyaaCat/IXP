package cat.nyaa.ixp;

import cat.nyaa.ixp.sign.SignManager;
import cat.nyaa.nyaacore.CommandReceiver;
import cat.nyaa.nyaacore.ILocalizer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class CommandHandler extends CommandReceiver {
    private IXPPlugin plugin;

    public CommandHandler(IXPPlugin plugin, ILocalizer i18n) {
        super(plugin, i18n);
        this.plugin = plugin;
    }

    @Override
    public String getHelpPrefix() {
        return "";
    }

    @SubCommand("sign")
    public void signCommand(CommandSender sender, Arguments arguments) {
        if (isAdmin(sender)) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                //note: player.getTargetBlock will fail when other signs is in the way to the actual target
                Block targetBlock = player.getTargetBlock(null, 5);
                if (targetBlock.getBlockData().getMaterial().equals(Material.WALL_SIGN)) {
                    BlockState state = targetBlock.getState();
                    if (state instanceof Sign) {
                        String subCommand = arguments.nextString();
                        if ("CREATE".equals(subCommand.toUpperCase())) {
                            String string = arguments.nextString();
                            switch (string.toUpperCase()) {
                                case "SEND":
                                    String serverId = arguments.nextString();
                                    if (isValidId(serverId)){
                                        SignManager instance = SignManager.getInstance();
                                        instance.createSendSign(targetBlock.getLocation(), (Sign) state, player);
                                    }
                                    break;
                                case "RECEIVE":

                                    break;
                                default:

                            }
                        } else if ("REMOVE".equals(subCommand.toUpperCase())) {

                        }
                    }
                } else {
                    sender.sendMessage("error.not_player");
                }
            } else {
                sender.sendMessage(I18n.format("permission.admin"));
            }
        }
    }

    private boolean isValidId(String serverId) {
        return plugin.config.serverIds.containsKey(serverId);
    }

    @SubCommand("inv")
    public void invCommand(CommandSender sender, Arguments arguments) {

    }

    @SubCommand("pass")
    public void passCommand(CommandSender sender, Arguments arguments) {
        String passwd = arguments.nextString();
        //todo check passwd
    }

    private boolean isAdmin(CommandSender sender) {
        return sender.hasPermission("ixp.admin");
    }
}
