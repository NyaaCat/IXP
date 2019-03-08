package cat.nyaa.ixp.sign;

import cat.nyaa.ixp.IXPPlugin;
import cat.nyaa.nyaacore.configuration.FileConfigure;
import cat.nyaa.nyaacore.configuration.ISerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SignInfo extends FileConfigure {
    @Serializable
    Map<String, IList> info;

    @Override
    protected String getFileName() {
        return "sign.yml";
    }

    @Override
    protected JavaPlugin getPlugin() {
        return IXPPlugin.plugin;
    }

    Map<String, List<String>> getMap() {
        if (info == null){
            return new HashMap<>();
        }
        HashMap<String, List<String>>result = new HashMap<>(info.size());
        info.forEach((s, iList) -> result.put(s, iList.list));
        return result;
    }

    void setMap(Map<String, List<String>> signMap) {
        this.info = new HashMap<>();
        signMap.forEach((s, strings) -> info.put(s, new IList(strings)));
    }

    public static class IList implements ISerializable{
        @Serializable
        List<String> list;

        public IList(){}
        IList(List<String> list){this.list = list;}
    }
}
