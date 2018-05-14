package kr.ac.koreatech.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
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

import kr.ac.koreatech.chat.model.User;

public class SignInActivity extends BaseActivity implements View.OnClickListener
{
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mStatusTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mStatusTextView = findViewById(R.id.status);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);

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
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
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
                            mStatusTextView.setText("Failed to create account");
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
                            mStatusTextView.setText("Authentication failed");
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void setCurrentUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            final String uid = firebaseUser.getUid();
            final String email = firebaseUser.getEmail();

            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(User.ref);
            myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User.currentUser = dataSnapshot.getValue(User.class);
                    if (User.currentUser == null) {
                        User.currentUser = new User(uid);
                        User.currentUser.setEmail(email);
                    }

                    User.currentUser.uid = uid;
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        if (User.currentUser != null) {
            if (User.currentUser.name == null) {
                alertDialog();
            } else {
                goToMainActivity();
            }
        } else {
            mStatusTextView.setText("");
            hideProgressDialog();
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
        startActivity(new Intent(this, ChatRoomActivity.class));
    }
}