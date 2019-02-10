package cn.eatmedicine.minecraft.command;

import cn.eatmedicine.minecraft.command.factory.*;
import cn.eatmedicine.minecraft.task.InputPswTask;
import cn.eatmedicine.minecraft.utils.Tools;
import org.bukkit.command.CommandSender;

import cn.eatmedicine.minecraft.Main;
import org.bukkit.entity.Player;

import java.util.List;

public class commandFactory {
    public Main plugin;
    public CommandSender sender;
    public List<InputPswTask> waitInputPswList;
    public commandFactory(Main plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
        this.waitInputPswList = plugin.waitInputPswList;
    }

    public IHandleCommand GetExector(String[] args) {
        if (args[0].toLowerCase().equals("sign")) {
            //sign create/remove
            if (args.length < 2)
                return null;

            if (args[1].toLowerCase().equals("create")) {
                //create send/receive
                if (args.length < 3)
                    return null;

                if (args[2].toLowerCase().equals("send")) {
                    //send [server-ids]
                    if (args.length < 4)
                        return null;
                    //创建
                    return new CreateSendSign(plugin, sender, args[3]);
                }
                if (args[2].toLowerCase().equals("receive")) {
                    //创建
                    return new CreateReceiveSign(plugin, sender);
                }
            }
            if (args[1].toLowerCase().equals("remove")) {
                //创建
                return new RemoveSign(plugin, sender);
            }
        }
        if(args[0].toLowerCase().equals("send")){
            if(args.length<3)
                return null;
            if(args[1].toLowerCase().equals("pass")){
                return new InputPsw(plugin,sender,args[2]);
            }
        }
        if(args[0].toLowerCase().equals("receive")){
            if(args.length<3)
                return null;
            if(args[1].toLowerCase().equals("pass")){
                Player player = Tools.GetPlayer(sender);
                if(player==null){
                    sender.sendMessage("Only Player allow use this command");
                    return null;
                }
                return new GetItemByPsw(plugin,args[2],player);
            }
        }
        if(args[0].toLowerCase().equals("inv")){
            if(args.length<3)
                return null;
            if(args[1].toLowerCase().equals("acquire")){
                Player player = Tools.GetPlayer(sender);
                if(player==null){
                    sender.sendMessage("Only Player allow use this command");
                    return null;
                }
                return new AcquireAllItem(plugin,player,args[2]);
            }
            if(args[1].toLowerCase().equals("clear")){
                return new ClearAllItem(plugin,sender,args[2]);
            }
        }

        return null;
    }
}
