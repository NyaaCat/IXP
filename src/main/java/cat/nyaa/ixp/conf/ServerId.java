package cat.nyaa.ixp.conf;

import cat.nyaa.nyaacore.configuration.ISerializable;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class ServerId implements ISerializable {
    @Serializable
    public String address;
    @Serializable
    public String psk;
    @Serializable
    public boolean enabled;
}
