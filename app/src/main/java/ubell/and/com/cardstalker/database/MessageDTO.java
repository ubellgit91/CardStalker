package ubell.and.com.cardstalker.database;

public class MessageDTO {

    private int _id;
    private String address;
    private String message;
    private String time;
    private String lat;
    private String lon;

    public MessageDTO(int _id, String address, String message, String time, String lat, String lon) {
        this._id = _id;
        this.address = address;
        this.message = message;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
