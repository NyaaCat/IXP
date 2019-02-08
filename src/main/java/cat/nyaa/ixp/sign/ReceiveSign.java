package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class ReceiveSign extends BaseSign {

    private String mSingleClickHint = I18n.format("sign.receive_single_hint");
    private String mSuccessHint = I18n.format("sign.receive_success");
    private String mWrongPassHint = I18n.format("sign.receive_wrong_pass");

    ReceiveSign(IXPPlugin plugin, Sign sign, int timeout) {
        super(plugin, sign, timeout);
    }

    @Override
    public String getType() {
        return "RECEIVE";
    }

    @Override
    public void onSingleClick(Player player) {
        player.sendMessage(mSingleClickHint);
    }

    @Override
    public void onDoubleClick(Player player, PlayerInteractEvent event) {
        player.sendMessage(mSuccessHint);
    }

    @Override
    public void onPassword(Player player, boolean correct) {
        if (correct){
            player.sendMessage(mSuccessHint);
        }{
            player.sendMessage(mWrongPassHint);
        }
    }

}
