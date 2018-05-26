package kr.ac.koreatech.chat.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class User implements Serializable {
    public static String ref = "users";
    public static User currentUser;

    @Exclude
    public String uid;

    public String name;
    public String email;
    public String playerId;
    public Boolean isOnline;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.isOnline = false;
    }

    public User(String uid, String name, String email, Boolean isOnline) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.isOnline = isOnline;
    }

    public void setEmail(String email) {
        this.email = email;
        update();
    }

    public void setName(String name) {
        this.name = name;
        update();
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
        update();
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
        update();
    }

    public void update() {
        if (uid != null && name != null) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(User.ref).child(uid);
            myRef.setValue(this);
        }
    }

    public void clearData() {
        this.playerId = null;
        this.isOnline = false;
        update();
    }
}