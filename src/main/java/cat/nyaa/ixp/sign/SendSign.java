package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SendSign extends BaseSign{
    private String mSingleClickHint = I18n.format("sign.send_single_hint");

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
    public void onDoubleClick(Player player) {

    }

    @Override
    public void onPassword(Player player, boolean correct) {

    }
}
