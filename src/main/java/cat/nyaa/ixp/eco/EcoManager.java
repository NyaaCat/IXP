package cat.nyaa.ixp.eco;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

public class EcoManager {
    private final Economy eco;
    private static EcoManager INSTANCE;

    private EcoManager(Economy eco){
        this.eco =eco;
    }

    public static EcoManager getInstance(){
        return INSTANCE;
    }

    public static EcoManager init(Economy eco){
        INSTANCE = new EcoManager(eco);
        return INSTANCE;
    }

    public boolean hasEnoughMoney(OfflinePlayer player, double fee){
        return eco.has(player, fee);
    }

    public void withdrawPlayer(OfflinePlayer player, double fee){
        EconomyResponse economyResponse = eco.withdrawPlayer(player, fee);
    }

    public void payback(OfflinePlayer player, double fee){
        eco.depositPlayer(player, fee);
    }
}
