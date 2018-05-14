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

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
        update();
    }
    public void setName(String name) {
        this.name = name;
        update();
    }

    private void update() {
        if (uid != null) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(User.ref).child(uid);
            myRef.setValue(this);
        }
    }
}