package cn.eatmedicine.minecraft.config;

public class serverIds {
    private String name;
    private String ip;
    private int port;
    private String psk;
    private boolean isEnable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port2) {
        this.port = port2;
    }

    public String getPsk() {
        return psk;
    }

    public void setPsk(String psk) {
        this.psk = psk;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    //=================================================
    public serverIds(String name, String ip, int port, String psk, boolean isEnable) {
        setName(name);
        setIp(ip);
        setPort(port);
        setPsk(psk);
        setEnable(isEnable);
    }

    public boolean isRight() {
        if (name.isEmpty() || ip.isEmpty() || psk.isEmpty())
            return false;
        if (port < 0 || port > 65535)
            return false;
        return true;
    }


}
