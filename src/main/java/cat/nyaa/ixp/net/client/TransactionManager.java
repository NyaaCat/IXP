package cat.nyaa.ixp.net.client;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.ixp.utils.InvUtils;
import cat.nyaa.nyaacore.database.DatabaseUtils;
import cat.nyaa.nyaacore.database.relational.Query;
import cat.nyaa.nyaacore.database.relational.RelationalDB;
import cat.nyaa.nyaacore.utils.ItemStackUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class TransactionManager {
    private static TransactionManager INSTANCE;
    private final String retrievedStr = "retrieved";
    private final RelationalDB relationalDB;

    static void submit(Transaction transaction) {
        if (INSTANCE == null) {
            getInstance();
        }
        INSTANCE.iSubmit(transaction);
    }

    private void iSubmit(Transaction transaction) {
        try (Query<Transaction> query = relationalDB.query(Transaction.class)) {
            query.insert(transaction);
        } catch (Exception e) {
            IXPPlugin.plugin.getLogger().log(Level.SEVERE, I18n.format("error.db_itm_insert_fail", transaction.getItem()), e);
        }
    }

    public static TransactionManager getInstance() {
        if (INSTANCE == null) {
            synchronized (TransactionManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TransactionManager();
                }
            }
        }
        return INSTANCE;
    }

    private TransactionManager() {
        relationalDB = DatabaseUtils.get(RelationalDB.class);
    }


    static String getTransId() {
        return UUID.randomUUID().toString();
    }

    public boolean takeByPassword(Player player, String passwd) {
        try (Query<Transaction> query = relationalDB.query(Transaction.class).whereEq(retrievedStr, Boolean.FALSE)) {
            query.whereEq("password", passwd);
            List<Transaction> select = query.select();
            if (select.size() > 0) {
                AtomicBoolean ret = new AtomicBoolean(true);
                select.forEach(transaction -> {
                    if (ret.get()) {
                        if (InvUtils.addItemToPlayer(player, ItemStackUtils.itemFromBase64(transaction.getItem()))) {
                            transaction.retrieve();
                            query.update(transaction, retrievedStr);
                            player.sendMessage(I18n.format("info.receive_success"));
                        } else {
                            ret.set(false);
                        }
                    }
                });
                return ret.get();
            } else {
                player.sendMessage(I18n.format("info.no_item"));
                return false;
            }
        } catch (Exception e) {
            onError(player, e);
        }
        return false;
    }

    public boolean takeByPlayer(Player player) {
        try (Query<Transaction> query = relationalDB.query(Transaction.class).whereEq("sender", player.getUniqueId().toString())) {
            query.whereEq(retrievedStr, Boolean.FALSE);
            query.whereEq("password", "");
            List<Transaction> select = query.select();
            if (select.isEmpty()) {
                player.sendMessage(I18n.format("info.receive_no_item"));
                return false;
            } else {
                select.forEach(transaction -> {
                    ItemStack itemStack = ItemStackUtils.itemFromBase64(transaction.getItem());
                    InvUtils.addItemToPlayer(player, itemStack);
                    transaction.retrieve();
                    query.update(transaction, retrievedStr);
                });
                player.sendMessage(I18n.format("info.receive_success"));
                return true;
            }
        } catch (Exception e) {
            onError(player, e);
        }
        return false;
    }

    private void onError(Player player, Exception e) {
        player.sendMessage(I18n.format("error.internal"));
        IXPPlugin.plugin.getLogger().log(Level.SEVERE, "error.internal", e);
    }

    public List<Transaction> queryUnfinished(CommandSender sender) {
        try (Query<Transaction> query = relationalDB.query(Transaction.class).whereEq(retrievedStr, Boolean.FALSE)) {
            return query.select();
        } catch (Exception e) {
            sender.sendMessage("error.internal");
            IXPPlugin.plugin.getLogger().log(Level.SEVERE, I18n.format("error.internal"), e);
            return new ArrayList<>();
        }
    }

    boolean playerHasEnoughSlot(String sender) {
        IXPPlugin plugin = IXPPlugin.plugin;
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(sender));
        if (player == null) return false;
        try (Query<Transaction> query = relationalDB.query(Transaction.class).whereEq(retrievedStr, Boolean.FALSE)) {
            int size = query.select().size();
            return size < plugin.getMaxSlot();
        } catch (Exception e) {
            return false;
        }
    }
}
