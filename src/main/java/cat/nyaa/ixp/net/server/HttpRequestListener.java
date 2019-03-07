package cat.nyaa.ixp.net.server;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public interface HttpRequestListener {
    HttpResponseStatus onReceiveHttp(FullHttpRequest msg);
}
