package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.IXPPlugin;
import org.bukkit.block.Sign;

public class ReceiveSign extends BaseSign {

    ReceiveSign(IXPPlugin plugin, Sign sign, int timeout) {
        super(plugin, sign, timeout);
    }

    @Override
    public String getType() {
        return "RECEIVE";
    }
}
