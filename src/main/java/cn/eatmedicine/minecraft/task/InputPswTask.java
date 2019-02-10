package cn.eatmedicine.minecraft.task;

import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.command.factory.InputPsw;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class InputPswTask extends BukkitRunnable {
    public IXPData data;
    public String PlayerName;
    public List<InputPswTask> list;

    private Main plugin;
    public InputPswTask(IXPData data, String PlayerName, List<InputPswTask> list, Main plugin){
        this.data = data;
        this.PlayerName = PlayerName;
        this.list = list;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        list.remove(this);
    }
}
