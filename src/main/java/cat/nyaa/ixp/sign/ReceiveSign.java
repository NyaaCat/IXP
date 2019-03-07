package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.ixp.eco.EcoManager;
import cat.nyaa.ixp.net.client.TransactionManager;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class ReceiveSign extends BaseSign {

    ReceiveSign(Sign sign, int timeout) {
        super(sign, timeout);
    }

    @Override
    public String getType() {
        return "RECEIVE";
    }

    @Override
    public void onSingleClick(Player player) {
        player.sendMessage(I18n.format("info.receive_single_hint"));
    }

    @Override
    public void onDoubleClick(Player player) {
//        player.sendMessage(mReceiveHint);
        takeItem(player);
    }

    private void takeItem(Player player) {
        EcoManager eco = EcoManager.getInstance();
        IXPPlugin plugin = IXPPlugin.getInstance();
        double receiveFee = plugin.getReceiveFee();
        if (eco.hasEnoughMoney(player, receiveFee)){
            if (TransactionManager.getInstance().takeByPlayer(player)) {
                eco.withdrawPlayer(player, receiveFee);
            }
        }else {
            player.sendMessage(I18n.format("info.no_enough_money"));
        }
    }

    private void takeItem(Player player, String password) {
        EcoManager eco = EcoManager.getInstance();
        IXPPlugin plugin = IXPPlugin.getInstance();
        double receiveFee = plugin.getReceiveFee();
        if (eco.hasEnoughMoney(player, receiveFee)){
            if (TransactionManager.getInstance().takeByPassword(player,password)) {
                eco.withdrawPlayer(player, receiveFee);
            }
        }else {
            player.sendMessage(I18n.format("info.no_enough_money"));
        }
    }

    @Override
    protected void iOnPassword(Player player, String correct) {
        takeItem(player, correct);
    }

}
