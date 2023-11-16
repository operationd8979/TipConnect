package Tip.Connect.model.Chat;

public class MessageChat {
    private String from;
    private String to;
    private String body;
    private long timestamp;
    private RecordType type;

    private boolean seen = false;

    public void setFrom(String from){
        this.from = from;
    }
    public void setSeen(Boolean seen){
        this.seen = seen;
    }
    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    public String getFrom(){
        return this.from;
    }

    public String getTo(){
        return this.to;
    }

    public String getBody(){
        return this.body;
    }
    public boolean getSeen(){
        return this.seen;
    }
    public RecordType getType(){
        return this.type;
    }




}
