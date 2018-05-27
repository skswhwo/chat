package kr.ac.koreatech.chat.model;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Message {
    public static String ref = "messages";

    private String id;
    private String text;
    private String name;
    private String imageUrl;
    private long time;

    public Message(String name, String text, String imageUrl) {
        this.name = name;
        this.text = text;
        this.imageUrl = imageUrl;
        Date date = new Date();
        this.time = date.getTime();
    }

    public void update() {
        update(null);
    }

    public void update(DatabaseReference.CompletionListener listener) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Message.ref);
        myRef.push().setValue(this, listener);
    }
}