package cn.eatmedicine.minecraft.IXPData;

//数据的类型
public enum IXPType {
    SEND, RECEIVE, UNDEFINED;
    private static final String IXPSend = "SEND";
    private static final String IXPReceive = "RECEIVE";

    public static IXPType getIXPType(String str) {
        if (str.equals(IXPSend))
            return IXPType.SEND;
        else if (str.equals(IXPReceive))
            return IXPType.RECEIVE;
        else
            return IXPType.UNDEFINED;
    }
}
