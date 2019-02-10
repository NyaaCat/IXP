package cn.eatmedicine.minecraft.Database;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import cat.nyaa.nyaacore.database.DatabaseUtils;
import cat.nyaa.nyaacore.database.relational.Query;
import cat.nyaa.nyaacore.database.relational.RelationalDB;

public class Database implements Cloneable {
    public final RelationalDB database;
    private final JavaPlugin plugin;

    public Database(JavaPlugin plugin) {
        this.plugin = plugin;
        database = DatabaseUtils.get(RelationalDB.class);
    }

    public boolean addSign(int x, int y, int z, String world, String server, int type, double fee, int isEnable) {
        SignData s = new SignData();
        try (Query<SignData> query = database.queryTransactional(SignData.class)) {
            int id = 0;
            List<SignData> list = query.select();
            if (list.size() != 0) {
                for (SignData sign : list) {
                    if (sign.id >= id) {
                        id = sign.id + 1;
                    }
                }
            }

            if (isEnable < 0)
                isEnable = 0;
            if (isEnable > 0)
                isEnable = 1;
            SignData.SetSignData(s, id, x, y, z, world, server, type, fee, isEnable);
            query.insert(s);
            query.commit();
        }
        return true;
    }

    public SignData selectSign(int x, int y, int z, String world) {
        try (Query<SignData> query = database.queryTransactional(SignData.class).
                whereEq("x", x).
                whereEq("y", y).
                whereEq("z", z).
                whereEq("world", world)) {
            if (query.count() == 0)
                return null;
            SignData s = query.selectUniqueForUpdate();
            return s;
        }
    }

    public List<SignData> selectAllSign() {
        try (Query<SignData> query = database.queryTransactional(SignData.class)) {
            return query.select();
        }
    }

    public boolean deleteSign(int x, int y, int z, String world) {
        try (Query<SignData> query = database.queryTransactional(SignData.class).
                whereEq("x", x).
                whereEq("y", y).
                whereEq("z", z).
                whereEq("world", world)) {
            if (query.count() == 0)
                return false;
            query.delete();
            query.commit();
        }
        return true;
    }

    public boolean setSignEnable(int x, int y, int z, String world, int isEnable) {
        if (isEnable < 0)
            isEnable = 0;
        if (isEnable > 0)
            isEnable = 1;
        try (Query<SignData> query = database.queryTransactional(SignData.class).
                whereEq("x", x).
                whereEq("y", y).
                whereEq("z", z).
                whereEq("world", world)) {
            SignData s = query.selectUniqueUnchecked();
            if (s == null)
                return false;
            s.isEnable = isEnable;
            query.update(s, "isEnable");
            query.commit();
        }
        return true;
    }

    public boolean addTransData(TransData data){
        try (Query<TransData> query = database.queryTransactional(TransData.class)){
            query.insert(data);
            query.commit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteTransData(String senderUuid,long timeStamp){
        try (Query<TransData> query = database.queryTransactional(TransData.class)
                .whereEq("SenderUuid",senderUuid)
                .whereEq("TimeStamp",timeStamp)){
            if(query.count()==0)
                return false;
            query.delete();
            query.commit();
        }
        catch (Exception ex){
            return false;
        }
        return true;
    }

    public boolean SetTakenTransData(String senderUuid,long timeStamp,boolean isTaken){
        try (Query<TransData> query = database.queryTransactional(TransData.class)
                .whereEq("SenderUuid",senderUuid)
                .whereEq("TimeStamp",timeStamp)){
            if(query.count()==0)
                return false;
            TransData data = query.selectUniqueUnchecked();
            data.IsTaken = isTaken;
            query.update(data,"IsTaken");
            query.commit();
        }
        catch (Exception ex){
            return false;
        }
        return true;
    }

    public List<TransData> SelectTransDataByUuid(String senderUuid){
        List<TransData> list = new ArrayList<>();
        try (Query<TransData> query = database.queryTransactional(TransData.class)
                .whereEq("SenderUuid",senderUuid)){
            if(query.count()==0)
                return list;
            list = query.select();
        }
        catch (Exception ex){
            return list;
        }
        return list;
    }
    public List<TransData> SelectTransDataByPsw(String psw){
        List<TransData> list = new ArrayList<>();
        try (Query<TransData> query = database.queryTransactional(TransData.class)
                .whereEq("Password",psw)){
            if(query.count()==0)
                return list;
            list = query.select();
        }
        catch (Exception ex){
            return list;
        }
        return list;
    }

    public int deleteAllTransData(String playerUuid){
        int count;
        try (Query<TransData> query = database.queryTransactional(TransData.class)
                .whereEq("SenderUuid",playerUuid)){
            count=query.count();
            if(query.count()==0)
                return 0;
            query.delete();
            query.commit();
        }
        catch (Exception ex){
            return 0;
        }
        return count;
    }
}
