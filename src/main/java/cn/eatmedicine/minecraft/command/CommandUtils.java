package cn.eatmedicine.minecraft.command;

import org.bukkit.command.CommandSender;

/**
 * Created by Enzo Cotter on 2019/2/10.
 */
public class CommandUtils {
    public static boolean CheckCommandPermission(CommandSender sender,String[] args){
        //ixp.user
        if(sender.hasPermission("ixp.user")==false)
            return false;
        if(args.length>0){
            if(args[0].equals("sign")||args[0].equals("inv")){
                if(sender.hasPermission("ixp.admin")==false)
                    return false;
            }
        }
        return true;
    }
}
