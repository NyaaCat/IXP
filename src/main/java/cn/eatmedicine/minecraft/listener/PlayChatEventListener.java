package cn.eatmedicine.minecraft.listener;

import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.task.ClickTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayChatEventListener implements Listener {
    public Main plugin;
    public List<String> list;
    public PlayChatEventListener(Main plugin,List<String> waitInputPswList){
        this.plugin = plugin;
        list = new ArrayList<String>();
    }

}
