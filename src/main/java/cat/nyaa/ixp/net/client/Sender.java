package cat.nyaa.ixp.net.client;

import cat.nyaa.ixp.I18n;
import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.ixp.conf.Configuration;
import cat.nyaa.ixp.conf.ServerId;
import cat.nyaa.nyaacore.http.client.HttpClient;
import cat.nyaa.nyaacore.utils.ItemStackUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.Charset;

public class Sender {
    private static Sender INSTANCE;

    private Sender() {
    }

    public static Sender getInstance() {
        synchronized (Sender.class) {
            if (INSTANCE == null) {
                synchronized (Sender.class) {
                    INSTANCE = new Sender();
                }
            }
        }
        return INSTANCE;
    }

    public void sendItemTo(ItemStack itemStack, String server, Player player, String password, HttpClient.HttpCallback callback) {
        Configuration conf = IXPPlugin.getInstance().getConf();
        String item = ItemStackUtils.itemToBase64(itemStack);
        Transaction transaction = new Transaction(conf.id, player.getUniqueId().toString(), item, password, String.valueOf(System.currentTimeMillis()));
        ServerId serverId = IXPPlugin.getInstance().getConf().serverIds.get(server);
        DefaultFullHttpRequest fullHttpRequest = this.makeRequest(server, transaction);
        player.sendMessage(I18n.format("info.item_sent"));
        HttpClient.connect("http://" + serverId.address, fullHttpRequest, callback);
        //                        ServerId id = conf.serverIds.get(server);
//                ByteBuf byteBuf = new EmptyByteBuf(new UnpooledByteBufAllocator(true));
//                String req =
//                        byteBuf.write
//                HttpClient.connect(id.address, new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, String.format("/ix/v1/%s/%s", server, "trans-id"), ));
    }

    private DefaultFullHttpRequest makeRequest(String server, Transaction transaction) {
        String uri = String.format("/ix/v1/%s/%s", server, transaction.getTransId());
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, uri);
        ByteBuf content = request.content();
        writeString(content, String.format("origin: %s\n", transaction.getOrigin()));
        writeString(content, String.format("sender: %s\n", transaction.getSender()));
        writeString(content, String.format("item: %s\n", transaction.getItem()));
        writeString(content, String.format("password: %s\n", transaction.getPassword()));
        writeString(content, String.format("timestamp: %s\n", transaction.getTimeStamp()));
        HttpHeaders headers = request.headers();
        headers.set("content-length", content.readableBytes());
        headers.set("psk", IXPPlugin.getInstance().getPsk());
        return request;
    }

    private void writeString(ByteBuf content, String format) {
        content.writeCharSequence(format, Charset.defaultCharset());
    }

    public void shutdown() {
        INSTANCE = null;
    }
}
