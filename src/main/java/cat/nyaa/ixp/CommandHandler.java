package cat.nyaa.ixp;

import cat.nyaa.ixp.conf.Configuration;
import cat.nyaa.ixp.net.client.Transaction;
import cat.nyaa.ixp.net.client.TransactionManager;
import cat.nyaa.ixp.sign.SignManager;
import cat.nyaa.ixp.utils.InvUtils;
import cat.nyaa.nyaacore.CommandReceiver;
import cat.nyaa.nyaacore.ILocalizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
@SuppressWarnings("package")
public class CommandHandler extends CommandReceiver {
    private final IXPPlugin plugin;
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
        receiveFee = configuration.fee.get("receive").doubleValue();
        sendFee = configuration.fee.get("send").doubleValue();
    }

    @SubCommand("sign")
    public void signCommand(CommandSender sender, Arguments arguments) {
        if (isAdmin(sender)) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                //note: player.getTargetBlock will fail when other signs is in the way to the actual target
                Block targetBlock = player.getTargetBlock(null, 5);
                Material material = targetBlock.getBlockData().getMaterial();
                if (!material.equals(Material.WALL_SIGN)&&!material.equals(Material.SIGN)) {
                    sender.sendMessage(I18n.format("error.not_sign"));
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
                                if (SignManager.hasSignAt(targetBlock.getLocation())) {
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
                            if (SignManager.hasSignAt(targetBlock.getLocation())){
                                instance.removeSign(targetBlock.getLocation(), ((Sign) state), player);
                                player.sendMessage(I18n.format("info.remove_success"));
                            }else {
                                sender.sendMessage(I18n.format("error.remove_not_created"));
                            }
                        }
                    }else {
                        sender.sendMessage(I18n.format("error.not_sign"));
                    }
                }
            }else {
                sender.sendMessage(I18n.format("error.not_player"));
            }
        }else {
            sender.sendMessage(I18n.format("permission.admin"));
        }
    }

    private boolean isValidId(String serverId) {
        return plugin.config.serverIds.containsKey(serverId);
    }

    @SubCommand("temp")
    public void tempInvCommand(CommandSender sender, Arguments arguments){
        if (!(sender instanceof Player)){
            sender.sendMessage("error.not_player");
            return;
        }
        Player player = (Player) sender;
        Inventory tempInv = InvUtils.getTempInv(player);
        player.openInventory(tempInv);
    }

    @SubCommand("inv")
    public void invCommand(CommandSender sender, Arguments arguments) {

    }

    @SubCommand("pass")
    public void passCommand(CommandSender sender, Arguments arguments) {
        if (!(sender instanceof Player)){
            sender.sendMessage(I18n.format("error.not_player"));
            return;
        }
        Player player = (Player) sender;
        String passwd = arguments.nextString();
        SignManager.getInstance().onPassword(player, passwd);
    }

    @SubCommand("list")
    public void getTransactionList(CommandSender sender, Arguments arguments){
        if (!isAdmin(sender)){
            sender.sendMessage(I18n.format("permission.admin"));
        }
        String action = arguments.nextString();
        switch (action) {
            case "unfinished":
                TransactionManager tm = TransactionManager.getInstance();
                List<Transaction> transactionList = tm.queryUnfinished(sender);
                if (transactionList.size()==0) {
                    sender.sendMessage(I18n.format("info.no_transaction"));
                    return;
                }else {
                    transactionList.forEach(transaction -> this.sendTransaction(sender, transaction));
                }
                break;
            case "finished":
            case "all":
            default:
                //todo support full query
                sender.sendMessage("error.unsupported");
                break;
        }
    }

    private void sendTransaction(CommandSender sender, Transaction transaction) {
        Player player = sender.getServer().getPlayer(UUID.fromString(transaction.getSender()));
        sender.sendMessage(I18n.format("info.transaction",transaction.getTimeStamp() ,transaction.getOrigin(), player.getName()));
    }

    private boolean isAdmin(CommandSender sender) {
        return sender.hasPermission("ixp.admin");
    }
}
