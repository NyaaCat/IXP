package cn.eatmedicine.minecraft.command;

import cat.nyaa.nyaacore.utils.ItemStackUtils;
import cn.eatmedicine.minecraft.task.InputPswTask;
import cn.eatmedicine.minecraft.utils.Tools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import cat.nyaa.nyaacore.database.DatabaseUtils;
import cn.eatmedicine.minecraft.Main;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.command.factory.IHandleCommand;

import java.util.List;

public class IXPCommandExecutor implements CommandExecutor {
    private final Main plugin;
    public List<InputPswTask> waitInputPswList;
    public IXPCommandExecutor(Main plugin) {
        this.plugin = plugin;
        this.waitInputPswList = plugin.waitInputPswList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("ixp")) {
            return false;
        }
        if (args.length == 0) {
            Tools.SendHelpMsg(sender,plugin);
            return true;
        }
        Player player = Tools.GetPlayer(sender);
        //Check Permission
        if(CommandUtils.CheckCommandPermission(sender,args)==false){
            sender.sendMessage(plugin.lang.format("message.command.not_permission"));
            return false;
        }
        //Uses the factory design method to generate commands
        commandFactory cf = new commandFactory(plugin, sender);
        IHandleCommand executor = cf.GetExecutor(args);
        if (executor == null) {
            Tools.SendHelpMsg(sender,plugin);
            return false;
        }
        executor.handleCommand();
        return true;
    }
}
