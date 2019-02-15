package cat.nyaa.ixp;

import cat.nyaa.ixp.conf.Configuration;
import cat.nyaa.ixp.sign.SignManager;
import cat.nyaa.nyaacore.CommandReceiver;
import cat.nyaa.nyaacore.ILocalizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class CommandHandler extends CommandReceiver {
    private IXPPlugin plugin;
    private double receiveFee;
    private double sendFee;

    public CommandHandler(IXPPlugin plugin, ILocalizer i18n) {
        super(plugin, i18n);
        this.plugin = plugin;
    }

    @Override
    public String getHelpPrefix() {
        return "";
    }

    public void loadFee(Configuration configuration){
        receiveFee = configuration.fee.get("receive");
        sendFee = configuration.fee.get("send");
    }

    @SubCommand("sign")
    public void signCommand(CommandSender sender, Arguments arguments) {
        if (isAdmin(sender)) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                //note: player.getTargetBlock will fail when other signs is in the way to the actual target
                Block targetBlock = player.getTargetBlock(null, 5);
                if (!targetBlock.getBlockData().getMaterial().equals(Material.WALL_SIGN)) {
                    sender.sendMessage("error.not_player");
                } else {
                    BlockState state = targetBlock.getState();
                    if (state instanceof Sign) {
                        String subCommand = arguments.nextString();
                        if ("CREATE".equals(subCommand.toUpperCase())) {
                            String string = arguments.nextString();
                            String serverId = arguments.nextString();
                            boolean hasFeeOverride = arguments.length() == 5;
                            if (!isValidId(serverId)) {
                                sender.sendMessage(I18n.format("error.create_not_valid_server"));
                            } else {
                                SignManager instance = SignManager.getInstance();
                                if (instance.hasSignAt(targetBlock.getLocation())) {
                                    sender.sendMessage(I18n.format("error.create_already_registered"));
                                    return;
                                }else {
                                    double fee;
                                    switch (string.toUpperCase()) {
                                        case "SEND":
                                            fee = hasFeeOverride ? arguments.nextDouble() : sendFee;
                                            instance.createSendSign(targetBlock.getLocation(), (Sign) state, player, serverId, fee);
                                            break;
                                        case "RECEIVE":
                                            fee = hasFeeOverride ? arguments.nextDouble() : receiveFee;
                                            instance.createReceiveSign(targetBlock.getLocation(), (Sign) state, player, serverId, fee);
                                            break;
                                        default:
                                            String unexpected = I18n.format("error.unexpected");
                                            Bukkit.getLogger().log(Level.WARNING, unexpected);
                                            sender.sendMessage(unexpected);
                                    }
                                }
                            }
                        } else if ("REMOVE".equals(subCommand.toUpperCase())) {
                            SignManager instance = SignManager.getInstance();
                            if (instance.hasSignAt(targetBlock.getLocation())){
                                instance.removeSign(targetBlock.getLocation(), ((Sign) state), player);
                            }else {
                                sender.sendMessage(I18n.format("error.remove_not_created"));
                            }
                        }
                    }
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
