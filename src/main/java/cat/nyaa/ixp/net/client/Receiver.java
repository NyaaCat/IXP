package cat.nyaa.ixp.net.client;

import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.ixp.net.server.HttpRequestListener;
import cat.nyaa.ixp.net.server.Server;
import cat.nyaa.nyaacore.http.server.Responder;
import cat.nyaa.nyaacore.utils.ItemStackUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.net.ssl.SSLException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Receiver implements HttpRequestListener {
    private static Receiver INSTANCE;
    private final IXPPlugin plugin;
    private Responder responder;

    public static void initialize(IXPPlugin plugin, int httpPort) throws CertificateException, InterruptedException, SSLException {
        initialize(plugin, httpPort, 0);
    }

    public static void initialize(IXPPlugin plugin, int httpPort, int httpsPort) throws CertificateException, InterruptedException, SSLException {
        INSTANCE = new Receiver(plugin);
        Server.init(httpPort, httpsPort).registerReceiver(INSTANCE);
    }

    private Receiver(IXPPlugin plugin) {
        this.plugin = plugin;
    }

    private static boolean checkIP(String target) {
        String[] split = target.split("\\.");
        if (split.length != 4) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            if (!checkNum(split[i], 255)) {
                return false;
            }
        }
        String[] portSplit = split[3].split(":");
        if (portSplit.length != 2) {
            return false;
        }
        if (!checkNum(portSplit[0], 255)) {
            return false;
        } else {
            return checkNum(portSplit[1], 65535);
        }
    }

    private static boolean checkNum(String num, int bound) {
        try {
            int check = Integer.parseInt(num);
            if (check < 0 || check > bound) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static Receiver getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponseStatus onReceiveHttp(FullHttpRequest msg) {
        HttpResponseStatus status;
        try {
            if (!msg.method().equals(HttpMethod.PUT)) {
                status = HttpResponseStatus.BAD_REQUEST;
                return status;
            }
            String uri = msg.uri();
            String psk = msg.headers().get("psk");
            String charset = msg.headers().get("charset");
            String content = null;
            if (!isValidPsk(psk)) {
                status = HttpResponseStatus.UNAUTHORIZED;
                return status;
            }
            if (charset != null) {
                content = msg.content().toString(Charset.forName(charset));
            } else {
                content = msg.content().toString(Charset.defaultCharset());
            }
            if (content==null){
                status = HttpResponseStatus.BAD_REQUEST;
                return status;
            }
            String[] split = content.split("\n");

            Map<String, String> payload = new HashMap<>();
            for (String s :
                    split) {
                String[] split1 = s.split(": ");
                String k = split1[0];
                String v;
                if (split1.length == 1) {
                    v = "";
                } else {
                    v = split1[1];
                }
                payload.put(k, v);
            }
            if (!checkPayload(payload)) {
                status = HttpResponseStatus.BAD_REQUEST;
                return status;
            }
            if (TransactionManager.getInstance().playerHasEnoughSlot(payload.get("sender"))) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Transaction transaction = new Transaction(payload, msg.uri());
                    onReceive(transaction);
                });
                status = HttpResponseStatus.CREATED;
            } else status = HttpResponseStatus.SERVICE_UNAVAILABLE;
        } catch (Exception e) {
            status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }
        return status;
    }

    private final String[] payloadChecker = {
            "origin",
            "sender",
            "item",
            "password",
            "timestamp"
    };

    private boolean checkPayload(Map<String, String> payload) {
        boolean ret = true;
        try {
            for (String s :
                    payloadChecker) {
                ret = ret && payload.containsKey(s);
            }
            if (ret) {
                IXPPlugin plugin = IXPPlugin.plugin;

                String origin = payload.get("origin");
                if (origin == null) ret = false;

                String sender = payload.get("sender");
                Player player = plugin.getServer().getPlayer(UUID.fromString(sender));
                if (player == null) ret = false;

                String item = payload.get("item");
                ItemStack itemStack = ItemStackUtils.itemFromBase64(item);
                if (itemStack == null) ret = false;

                if (payload.get("password") == null) ret = false;

                Long.valueOf(payload.get("timestamp"));
            }
        } catch (Exception e) {
            return false;
        }
        return ret;
    }

    private boolean isValidPsk(String psk) {
        String psk1 = IXPPlugin.plugin.getPsk();
        return psk1.equals(psk);
    }

    private void onReceive(Transaction transaction) {
        TransactionManager.submit(transaction);
    }

    public void shutdown() {
        INSTANCE = null;
    }
}
