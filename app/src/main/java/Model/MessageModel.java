package Model;

public class MessageModel {
    String uid,message;
    long timestamp;

    public MessageModel(String uid, String message, long timestamp) {
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
    }
    //empty constructor for firebase
    public MessageModel() {
    }

    public MessageModel(String uid, String message) {
        this.uid = uid;
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
