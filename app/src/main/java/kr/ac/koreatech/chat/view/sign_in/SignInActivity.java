package kr.ac.koreatech.chat.view.sign_in;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import kr.ac.koreatech.chat.R;
import kr.ac.koreatech.chat.model.User;
import kr.ac.koreatech.chat.view.BaseActivity;
import kr.ac.koreatech.chat.view.chat_room.ChatRoomActivity;

@EActivity(R.layout.activity_sign_in)
public class SignInActivity extends BaseActivity
{
    private FirebaseAuth mAuth;

    @ViewById
    EditText fieldEmail;

    @ViewById
    EditText fieldPassword;

    @ViewById
    TextView statusTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        showProgressDialog();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        setCurrentUser(currentUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Click
    public void emailSignInButton() {
        signIn(fieldEmail.getText().toString(), fieldPassword.getText().toString());
    }

    @Click
    public void emailCreateAccountButton() {
        createAccount(fieldEmail.getText().toString(), fieldPassword.getText().toString());
    }

    private void createAccount(String email, String password) {
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            setCurrentUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Failed to create account",
                                    Toast.LENGTH_SHORT).show();
                            setCurrentUser(null);
                            statusTextView.setText("Failed to create account");
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signIn(String email, String password) {
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            setCurrentUser(user);
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            setCurrentUser(null);
                            statusTextView.setText("Authentication failed");
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void setCurrentUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            final String uid = firebaseUser.getUid();
            final String email = firebaseUser.getEmail();

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get User object and use the values to update the UI
                    User user = dataSnapshot.getValue(User.class);

                    if (user == null) {
                        user = new User(uid, null, email, null, true);
                    }

                    user.setUid(uid);
                    User.currentUser = user;
                    updateUI();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };

            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(User.ref);
            myRef.child(uid).addListenerForSingleValueEvent(listener);

        } else {
            updateUI();
        }
    }

    private void updateUI() {
        hideProgressDialog();

        if (User.currentUser != null) {
            if (User.currentUser.getName() == null) {
                alertDialog();
            } else {
                goToMainActivity();
            }
        } else {
            statusTextView.setText("");
        }
    }

    private void alertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);

        alertDialog.setTitle("Input your name");       // 제목 설정

// EditText 삽입하기
        final EditText et = new EditText(SignInActivity.this);
        alertDialog.setView(et);

// 확인 버튼 설정
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Text 값 받기
                User.currentUser.setName(et.getText().toString());

                //닫기
                dialog.dismiss();

                // Event
                updateUI();
            }
        });
        alertDialog.show();
    }

    private void goToMainActivity() {
        User.currentUser.setIsOnline(true);
        User.currentUser.update();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                if (registrationId != null) {
                    User.currentUser.setPlayerId(userId);
                    User.currentUser.update();
                }
            }
        });

        startActivity(new Intent(this, ChatRoomActivity.class));
    }
}