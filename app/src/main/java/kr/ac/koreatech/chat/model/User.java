package kr.ac.koreatech.chat.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class User implements Serializable {
    public static String ref = "users";

    public static User currentUser;

    private String uid;
    private String name;
    private String email;
    private String playerId;
    private Boolean isOnline = false;

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