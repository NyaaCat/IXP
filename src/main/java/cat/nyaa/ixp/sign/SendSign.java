package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.ixp.eco.EcoManager;
import cat.nyaa.ixp.net.client.Sender;
import cat.nyaa.ixp.utils.InvUtils;
import cat.nyaa.nyaacore.http.client.HttpClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SendSign extends BaseSign {

    SendSign(Sign sign, int timeout) {
        super(sign, timeout);
    }

    @Override
    public String getType() {
        return "SEND";
    }

    @Override
    public void onSingleClick(Player player) {
        player.sendMessage(I18n.format("info.send_single"));
    }

    @Override
    public void onDoubleClick(Player player) {
        sendItem(player, "");
//        player.sendMessage(mSendHint);
    }

    private void sendItem(Player player, String password) {
        PlayerInventory inventory = player.getInventory();
        ItemStack itemInMainHand = inventory.getItemInMainHand();
        if (itemInMainHand.getType().equals(Material.AIR)) {
            player.sendMessage(I18n.format("error.send_no_item"));
            return;
        }
        HttpClient.HttpCallback callback = (ctx, response, throwable) -> {
            HttpResponseStatus status = response.status();
            if (status.equals(HttpResponseStatus.CREATED)) {
                SendSign.this.doOnSuccess(player, itemInMainHand);
            } else if (status.equals(HttpResponseStatus.BAD_REQUEST) ||
                    status.equals(HttpResponseStatus.UNAUTHORIZED) ||
                    status.equals(HttpResponseStatus.INTERNAL_SERVER_ERROR) ||
                    status.equals(HttpResponseStatus.SERVICE_UNAVAILABLE)) {
                onError(player, itemInMainHand, status);
            }
        };
        EcoManager eco = EcoManager.getInstance();
        double sendFee = IXPPlugin.plugin.getSendFee();
        if (eco.hasEnoughMoney(player, sendFee)) {
            PlayerInventory inventory1 = player.getInventory();
            int heldItemSlot = inventory1.getHeldItemSlot();
            inventory1.setItem(heldItemSlot, new ItemStack(Material.AIR));
//            InventoryUtils.removeItem(player,itemInMainHand, itemInMainHand.getAmount());
            Sender.getInstance().sendItemTo(itemInMainHand, sign.getLine(2), player,password , callback);
            eco.withdrawPlayer(player, sendFee);
        }else {
            player.sendMessage(I18n.format("info.no_enough_money"));
        }
    }

    private void onError(Player player, ItemStack itemInMainHand, HttpResponseStatus status) {
        double sendFee = IXPPlugin.plugin.getSendFee();
        String message = getMessage(status);
        player.sendMessage(message);
        InvUtils.addItemToPlayer(player, itemInMainHand);
        EcoManager.getInstance().payback(player, sendFee);
    }

    private String getMessage(HttpResponseStatus status) {
        if (status.equals(HttpResponseStatus.BAD_REQUEST)) {
            return I18n.format("error.http_bad_request");
        } else if (status.equals(HttpResponseStatus.UNAUTHORIZED)) {
            return I18n.format("error.http_unauthorized");
        } else if (status.equals(HttpResponseStatus.INTERNAL_SERVER_ERROR)) {
            return I18n.format("error.http_internal_error");
        } else if (status.equals(HttpResponseStatus.SERVICE_UNAVAILABLE)) {
            return I18n.format("error.http_service_unavailable");
        }
        return I18n.format("error.unexpected");
    }

    private void doOnSuccess(Player player, ItemStack item) {
        player.sendMessage(I18n.format("info.send_success"));
    }

    @Override
    protected void iOnPassword(Player player, String password) {
        sendItem(player, password);
    }
}
