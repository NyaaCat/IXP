package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.ixp.client.Sender;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SendSign extends BaseSign{
    private String mSingleClickHint = I18n.format("sign.send_single_hint");
    private String mSuccessHint = I18n.format("sign.send_success_hint");
    private String mWrongPassHint = I18n.format("sign.send_wrong_pass");

    SendSign(IXPPlugin plugin, Sign sign, int timeout) {
        super(plugin, sign, timeout);
    }

    @Override
    public String getType() {
        return "SEND";
    }

    @Override
    public void onSingleClick(Player player) {
        player.sendMessage(mSingleClickHint);
    }

    @Override
    public void onDoubleClick(Player player, PlayerInteractEvent event) {
        player.sendMessage(mSuccessHint);
        ItemStack item = event.getItem();
        Sender.getInstance().SendItemTo(item, sign.getLine(3));
    }

    @Override
    public void onPassword(Player player, boolean correct) {
        if (correct){
             player.sendMessage(mSuccessHint);
        }else {
            player.sendMessage(mWrongPassHint);
        }
    }
}
