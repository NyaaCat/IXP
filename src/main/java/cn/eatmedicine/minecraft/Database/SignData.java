package cn.eatmedicine.minecraft.Database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "IXPData")
public class SignData {
    @Column(name = "id")
    public int id;
    @Column(name = "x")
    public int x;
    @Column(name = "y")
    public int y;
    @Column(name = "z")
    public int z;
    @Column(name = "world")
    public String world;
    @Column(name = "server")
    public String server;
    //0=send 1 or other=receive
    @Column(name = "type")
    public int type;
    @Column(name = "fee")
    public double fee;
    @Column(name = "isEnable")
    public int isEnable;

    public static void SetSignData(SignData data, int id, int x,
                                   int y, int z, String world, String server, int type, double fee, int isEnable) {
        data.id = id;
        data.x = x;
        data.y = y;
        data.z = z;
        data.world = world;
        data.server = server;
        data.type = type;
        data.fee = fee;
        data.isEnable = isEnable;
    }

}
