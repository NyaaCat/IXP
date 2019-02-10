package cn.eatmedicine.minecraft.http;

import cat.nyaa.nyaacore.http.client.HttpClient;
import cat.nyaa.nyaacore.http.server.Responder;
import cat.nyaa.nyaacore.http.server.ResponseHead;
import cat.nyaa.nyaacore.utils.ItemStackUtils;
import cat.nyaa.nyaacore.utils.PlayerUtils;
import cn.eatmedicine.minecraft.Database.Database;
import cn.eatmedicine.minecraft.Database.TransData;
import cn.eatmedicine.minecraft.Main;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IXPResponder implements Responder {
    public Main plugin;
    public IXPResponder(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public Object receive(HttpRequest req, ResponseHead response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String[] parm = req.uri().split("/");
        plugin.getLogger().info("parm.length:"+parm.length+"|"+parm[0]+"|");
        if(parm.length!=5){
            response.status(400);
            jsonObject.put("status","Bad Request");
            return jsonObject.toJSONString();
        }
        if(req.headers().get("x-ixp-psk").equals(plugin.cm.localInfo.getPsk())==false){
            plugin.getLogger().info(req.headers().get("x-ixp-psk")+"|"+plugin.cm.localInfo.getPsk());
            response.status(401);
            jsonObject.put("status","Authentication Failure");
            return jsonObject.toJSONString();
        }
        try{
            FullHttpRequest request = (FullHttpRequest)req;
            byte[] data = new byte[request.content().readableBytes()];
            request.content().readBytes(data);
            String strContent = new String(data, "UTF-8");
            plugin.getLogger().info(strContent);
            Gson gson = new Gson();
            TransData tData = gson.fromJson(strContent, TransData.class);
            if(tData.Password.equals("CannotUseThisPassword")){
                tData.Password = null;
            }
            plugin.getLogger().info(tData.ItemData);
            Database db = new Database(plugin);
            plugin.getLogger().info("Add DB?:"+db.addTransData(tData));
            response.status(201);
            jsonObject.put("status","Ok");
        }catch (Exception ex){
            jsonObject.put("status","Internal plugin error");
            response.status(500);
        }
        String json = jsonObject.toJSONString();
        return json;
    }


}
