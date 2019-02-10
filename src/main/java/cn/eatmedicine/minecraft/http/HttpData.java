package cn.eatmedicine.minecraft.http;

import cn.eatmedicine.minecraft.Database.TransData;

public class HttpData {
    public TransData transData;
    public String transId;
    public String IP;
    public int port;

    public HttpData(TransData transData, String transId, String IP, int port) {
        this.transData = transData;
        this.transId = transId;
        this.IP = IP;
        this.port = port;
    }
}
