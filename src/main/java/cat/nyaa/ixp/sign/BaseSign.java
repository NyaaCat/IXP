package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.IXPPlugin;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public abstract class BaseSign {
    private Sign sign;
    private IXPPlugin plugin;
    protected int tickSinceClick = 0;
    protected int timeout;

    BaseSign(IXPPlugin plugin, Sign sign, int timeout){
        this.plugin = plugin;
        this.timeout = timeout;
        this.sign = sign;
    }

    //todo

    public static BaseSign create(IXPPlugin plugin, Sign sign, int timeout){
        String type = sign.getLine(1);
        type = type.toUpperCase();
        BaseSign result;
        switch (type){
            case "SEND":
                result = new SendSign(plugin,sign,timeout);
                break;
            case "RECEIVE":
                result = new ReceiveSign(plugin, sign, timeout);
                break;
            default:
                throw new RuntimeException();
        }
        return result;
    }

    public abstract String getType();

    public void onPlayerRightClick(Player player){
        UUID uuid = player.getUniqueId();
    }

    private void onTimeExceeded(Player player){
        player.sendMessage("操作超时");
    }

    private void onTimerReset(Player player){
        player.sendMessage("操作超时");
    }


}
