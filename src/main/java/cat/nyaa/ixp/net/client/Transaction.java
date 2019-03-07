package cat.nyaa.ixp.net.client;

import javax.persistence.*;
import java.util.Map;

@Entity
@Access(AccessType.FIELD)
@Table(name = "trans")
public class Transaction  {
    @Column(name = "transid")
    private String transId;
    @Column(name = "origin")
    private String origin;
    @Column(name = "sender")
    private String sender;
    @Column(name = "item", columnDefinition = "MEDIUMTEXT")
    private String item;
    @Column(name = "password", columnDefinition = "TEXT")
    private String password;
    @Column(name = "time_stamp")
    private Long timeStamp;

    @Column(name = "retrieved")
    private Boolean retrieved = false;


    public Transaction(){}

    Transaction(Map<String, String> payload, String uri){
        transId = uri.substring(uri.lastIndexOf("/"));
        origin = payload.get("origin");
        sender = payload.get("sender");
        item = payload.get("item");
        password = payload.get("password");
        timeStamp = Long.valueOf(payload.get("timestamp"));
    }

    Transaction(String origin, String sender, String item, String password, String timeStamp) {
        this(origin, sender, item, password, timeStamp, null);
    }

    Transaction(String origin, String sender, String item, String password, String timeStamp, String transId) {
        this.origin = origin;
        this.sender = sender;
        this.item = item;
        this.password = password;
        this.timeStamp = Long.valueOf(timeStamp);
        this.transId = transId == null ? TransactionManager.getTransId() : transId;
    }

    public String getTransId() {
        return transId;
    }
    public String getOrigin() {
        return origin;
    }
    public String getSender() {
        return sender;
    }
    public String getItem() {
        return item;
    }
    public String getPassword() {
        return password;
    }
    public Long getTimeStamp() {
        return timeStamp;
    }

    public void retrieve() {
        retrieved = true;
    }
}
