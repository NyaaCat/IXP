package cat.nyaa.ixp.net.server;

import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.nyaacore.utils.ConcurrentUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

public class Server {
    private static Server INSTANCE;
    private int httpsPort;
    private int httpPort;
    private ServerHandler handler;

    private Server(int httpPort, int httpsPort) {
        this.httpPort = httpPort;
        this.httpsPort = httpsPort;
    }

    public static Server init(int httpPort, int httpsPort) {
        INSTANCE = new Server(httpPort, httpsPort);
        INSTANCE.handler = new ServerHandler();
        ConcurrentUtils.runAsyncTask(IXPPlugin.plugin, null, o -> INSTANCE.boot());
        boolean inited = true;
        return INSTANCE;
    }

    public static Server getInstance() {
        return INSTANCE;
    }

    private void boot() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelInitializer<SocketChannel> childHandler = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast("decoder", new HttpRequestDecoder())
                        .addLast("encoder", new HttpResponseEncoder())
                        .addLast("aggregator", new HttpObjectAggregator(65535))
                        .addLast("handler", handler);
            }
        };
        bootstrap.group(eventLoopGroup, workers)
                .channel(NioServerSocketChannel.class)
                .childHandler(childHandler)
                .bind(httpPort);
    }

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
    private final EventLoopGroup workers = new NioEventLoopGroup(8);

    public void registerReceiver(HttpRequestListener instance) {
        handler.setListener(instance);
    }

    public void shutdown() {
        eventLoopGroup.shutdownGracefully();
        workers.shutdownGracefully();
        INSTANCE = null;
    }
}
