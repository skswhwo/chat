package kr.ac.koreatech.chat.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public static String ref = "messages";

    @Exclude
    public String id;

    public String text;
    public String name;
    public long time;

    public Message() {
    }

    public Message(String name, String text) {
        this.name = name;
        this.text = text;
        Date date = new Date();
        this.time = date.getTime();
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        if (time > 0) {
            Date date = new Date(time);
            SimpleDateFormat formatter = new SimpleDateFormat("MM월 dd일 hh시 mm분");
            return formatter.format(date);
        }
        return null;
    }

    public String getNameAndTime() {
        String res = getName();
        String time = getTime();
        if (time != null) {
            res = res.concat(" (" + time + ")");
        }
        return res;
    }

    public void update() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Message.ref);
        myRef.push().setValue(this);
    }
}