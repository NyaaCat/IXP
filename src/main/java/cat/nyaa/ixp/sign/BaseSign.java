package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.IXPPlugin;

/**
 * @author ReinWD
 * created @ 2019/2/4
 */
public abstract class BaseSign {
    private IXPPlugin plugin;
    int tickSinceClick = 0;
    int timeout;

    BaseSign(IXPPlugin plugin){
        this(plugin,200);
    }

    BaseSign(IXPPlugin plugin, int timeout){
        this.plugin = plugin;
        this.timeout = timeout;
    }

    //todo
}
