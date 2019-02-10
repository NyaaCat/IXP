package cn.eatmedicine.minecraft.IXPData;


public class IXPData {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(fee);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((fromServer == null) ? 0 : fromServer.hashCode());
        result = prime * result + ((toServer == null) ? 0 : toServer.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IXPData other = (IXPData) obj;
        if (Double.doubleToLongBits(fee) != Double.doubleToLongBits(other.fee))
            return false;
        if (fromServer == null) {
            if (other.fromServer != null)
                return false;
        } else if (!fromServer.equals(other.fromServer))
            return false;
        if (toServer == null) {
            if (other.toServer != null)
                return false;
        } else if (!toServer.equals(other.toServer))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    private IXPType type;
    private double fee;
    private String fromServer;
    private String toServer;

    public IXPType getType() {
        return type;
    }

    public void setType(IXPType type) {
        this.type = type;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getFromServer() {
        return fromServer;
    }

    public void setFromServer(String fromServer) {
        this.fromServer = fromServer;
    }

    public String getToServer() {
        return toServer;
    }

    public void setToServer(String toServer) {
        this.toServer = toServer;
    }

    public IXPData(IXPType type, double fee, String fromServer, String toServer) {
        this.type = type;
        this.fee = fee;
        this.fromServer = fromServer;
        this.toServer = toServer;
    }

}
