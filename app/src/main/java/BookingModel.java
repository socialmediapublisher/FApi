import java.util.Date;

public class BookingModel {
    String id;
    String booker_user_id;
    String room;
    Date date;
    String start_time;
    String end_time;
    String reason;
    String lacks_tags;

    public BookingModel(String id, String booker_user_id, String room, Date date, String start_time, String end_time, String reason, String lacks_tags) {
        this.id = id;
        this.booker_user_id = booker_user_id;
        this.room = room;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.reason = reason;
        this.lacks_tags = lacks_tags;
    }

    public String getId() {
        return id;
    }

    public String getBooker_user_id() {
        return booker_user_id;
    }

    public String getRoom() {
        return room;
    }

    public Date getDate() {
        return date;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getReason() {
        return reason;
    }

    public String getLacks_tags() {
        return lacks_tags;
    }
}
