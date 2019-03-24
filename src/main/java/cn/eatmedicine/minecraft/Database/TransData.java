package cn.eatmedicine.minecraft.Database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "TransData")
public class TransData {
    @Column(name = "OriginServer")
    public String OriginServer;
    @Column(name = "SenderName")
    public String SenderName;
    @Column(name = "ItemData")
    public String ItemData;
    @Column(name = "Password")
    public String Password;
    @Column(name = "TimeStamp")
    public long TimeStamp;
    @Column(name = "IsTaken")
    public boolean IsTaken;

    public static void SetTransData(TransData data, String originServer, String senderName, String itemData, String password
            , long timeStamp, boolean isTaken) {
        data.OriginServer = originServer;
        data.SenderName = senderName.toLowerCase();
        data.ItemData = itemData;
        data.Password = password;
        data.TimeStamp = timeStamp;
        data.IsTaken = isTaken;
    }
}
