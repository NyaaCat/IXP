package cat.nyaa.ixp.conf;

import cat.nyaa.nyaacore.configuration.ISerializable;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public class Fee implements ISerializable {
    @Serializable
    double send;
    @Serializable
    double receive;
}
