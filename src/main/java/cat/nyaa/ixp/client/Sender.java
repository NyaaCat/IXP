package cat.nyaa.ixp.client;

import cat.nyaa.nyaacore.http.client.HttpClient;
import org.bukkit.inventory.ItemStack;

public class Sender {
    private static Sender INSTANCE;

    static {
        INSTANCE = new Sender();
    }

    private Sender(){}

    public static Sender getInstance() {
        return INSTANCE;
    }

    public void SendItemTo(ItemStack itemStack, String Server){

    }
}
