package kr.ac.koreatech.chat.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class User implements Serializable {
    public static String ref = "users";
    public static User currentUser;

    private String uid;
    private String name;
    private String email;
    private String playerId;
    private Boolean isOnline;

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

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPlayerId() {
        return playerId;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
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