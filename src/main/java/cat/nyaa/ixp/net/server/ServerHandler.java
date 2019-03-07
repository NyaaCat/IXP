package cat.nyaa.ixp.net.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;

@ChannelHandler.Sharable
class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private HttpRequestListener listener = msg -> HttpResponseStatus.valueOf(500);
    private final HttpVersion version = HttpVersion.HTTP_1_1;

    void setListener(HttpRequestListener handler){this.listener = handler;}

    @Override
    @SuppressWarnings("exception")
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        HttpResponseStatus status = listener.onReceiveHttp(msg);
        resopnse(ctx, status);
    }

    private void resopnse(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(version,status);
        response.content().writeCharSequence(status.codeAsText(),Charset.defaultCharset());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().add(HttpHeaderNames.ACCEPT_CHARSET, Charset.defaultCharset());
        ctx.writeAndFlush(response);
    }
}
