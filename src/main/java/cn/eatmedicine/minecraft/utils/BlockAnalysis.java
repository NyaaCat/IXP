package cn.eatmedicine.minecraft.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

import cn.eatmedicine.minecraft.IXPData.IXPData;
import cn.eatmedicine.minecraft.IXPData.IXPType;


public class BlockAnalysis {

    private static final String IXPFirstLine = "[IXP]";
    private static final String IXPSecondLineSend = "SEND";
    private static final String IXPSecondLineReceive = "RECEIVE";


    public static boolean isSign(Block block) {
        return block != null && block.getType().name().endsWith("_SIGN");
    }

    //从block获取一个Sign牌子的对象，如果block不属于Sign则返回null
    public static Sign GetSign(Block block) {
        BlockState bs = block.getState();
        //如果block属于Sign
        if (bs instanceof Sign) {
            Sign sign = (Sign) bs;
            return sign;
        }
        return null;
    }

    //判断是否是符合IXP格式的牌子
    public static IXPData GetIXP(Sign sign, String fromServer, JavaPlugin plugin) {
        String line1 = sign.getLine(0);
        String line2 = sign.getLine(1);
        String line3 = sign.getLine(2);
        String line4 = sign.getLine(3);

        //前两行不匹配
        if (!line1.equals(IXPFirstLine)) {
            return null;
        }
        IXPType type = IXPType.getIXPType(line2);
        if (type == IXPType.UNDEFINED)
            return null;

        //如果是发送种类且第三行为空 则错误
        if (type == IXPType.SEND && line3.equals(""))
            return null;

        //第四行是否匹配为数字
        double fee;
        try {
            fee = Double.parseDouble(line4);
        } catch (NumberFormatException e) {
            return null;
        }
        IXPData data = new IXPData(type, fee, fromServer, line3);
        //这里还缺少一个判断服务器名字是否正确
        //

        return data;
    }

    //判断是否是符合IXP格式的牌子
    public static IXPData GetIXP(String[] lines, String fromServer, JavaPlugin plugin) {
        if (lines.length < 3)
            return null;
        String line1 = lines[0];
        String line2 = lines[1];
        String line3 = lines[2];
        String line4 = lines[3];

        //前两行不匹配
        if (!line1.equals(IXPFirstLine)) {
            return null;
        }
        IXPType type = IXPType.getIXPType(line2);
        if (type == IXPType.UNDEFINED)
            return null;

        //如果是发送种类且第三行为空 则错误
        if (type == IXPType.SEND && line3.equals(""))
            return null;

        //第四行是否匹配为数字
        int fee;
        try {
            fee = Integer.parseInt(line4);
        } catch (NumberFormatException e) {
            return null;
        }
        IXPData data;
        if(type==IXPType.SEND){
             data = new IXPData(type, fee, fromServer, line3);
        }
        else{
            data = new IXPData(type, fee, fromServer, null);
        }


        return data;
    }
}
