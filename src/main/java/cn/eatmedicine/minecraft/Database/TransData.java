package cn.eatmedicine.minecraft.Database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "TransData")
public class TransData {
    @Column(name = "OriginServer")
    public String OriginServer;
    @Column(name = "SenderUuid")
    public String SenderUuid;
    @Column(name = "ItemData")
    public String ItemData;
    @Column(name = "Password")
    public String Password;
    @Column(name = "TimeStamp")
    public long TimeStamp;
    @Column(name = "IsTaken")
    public boolean IsTaken;

    public void SetTransData(String originServer, String senderUuid, String itemData, String password
            , long timeStamp, boolean isTaken) {
        OriginServer = originServer;
        SenderUuid = senderUuid;
        ItemData = itemData;
        Password = password;
        TimeStamp = timeStamp;
        IsTaken = isTaken;
    }
}
