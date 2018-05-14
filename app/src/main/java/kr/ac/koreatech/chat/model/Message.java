package kr.ac.koreatech.chat.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

public class Message {
    public static String ref = "messages";

    @Exclude
    public String id;

    public String text;
    public String name;

    public Message() {
    }

    public Message(String name, String text) {
        this.name = name;
        this.text = text;
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

    public void update() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Message.ref);
        myRef.push().setValue(this);
    }
}