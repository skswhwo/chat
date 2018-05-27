package kr.ac.koreatech.chat.view.chat_room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.ac.koreatech.chat.view.BaseActivity;
import kr.ac.koreatech.chat.R;
import kr.ac.koreatech.chat.model.Message;
import kr.ac.koreatech.chat.model.User;
import kr.ac.koreatech.chat.view.user_list.UserListActivity_;

public class ChatRoomActivity  extends BaseActivity {
    private static final int REQUEST_IMAGE = 1;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    ListView messageListView;
    EditText messageEditText;
    Button sendButton;
    ImageView addMessageImageView;

    private ChatRoomAdapter adapter;
    private DatabaseReference mFirebaseDatabaseReference;

    /*
    TODO: onCreate 함수 제거 (Annotation으로 대체)
    http://androidannotations.org/
    
    1. Layout

    2. View

    3. Click

    4. TextWatcher

    5. Initializer

    6. AndroidManifest 파일 수정

    7. startActivity가 사용되는 곳에서 class이름 + "_" 로 호출

     */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        messageListView = (ListView) findViewById(R.id.messageListView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendButton = (Button) findViewById(R.id.sendButton);
        addMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);

        addMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(User.currentUser.getName(), messageEditText.getText().toString(),null);
                message.update();
                sendPush(message.getText());
                messageEditText.setText("");
            }
        });

        messageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sendButton.setEnabled((messageEditText.getText().toString().trim().length() > 0));
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        adapter = new ChatRoomAdapter(this, mFirebaseDatabaseReference.child(Message.ref));
        messageListView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        adapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                sign_out();
                finish();
                return true;
            case R.id.user_list_menu:
                startActivity(new Intent(this, UserListActivity_.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();

                    Message message = new Message(User.currentUser.getName(), null, LOADING_IMAGE_URL);
                    message.update(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String key = databaseReference.getKey();
                                StorageReference storageReference =
                                        FirebaseStorage.getInstance()
                                                .getReference(firebaseUser.getUid())
                                                .child(key)
                                                .child(uri.getLastPathSegment());

                                putImageInStorage(storageReference, uri, key);
                            } else {
                                //@TODO error handling ("Unable to write message to database.")
                            }
                        }
                    });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatRoomActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Message message = new Message(User.currentUser.getName(), null, task.getResult().getDownloadUrl()
                                    .toString());
                            mFirebaseDatabaseReference.child(Message.ref).child(key)
                                    .setValue(message);
                        } else {
                            //@TODO error handling ("Image upload task was not successful.")
                        }
                    }
                });
    }

    private void sendPush(final String text) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(User.ref);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList playerIds= new ArrayList<String>();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    User user = childDataSnapshot.getValue(User.class);
                    if (user.getPlayerId() != null) {
                        playerIds.add("'" + user.getPlayerId() + "'");
                    }
                }

                String playerIdsStr = TextUtils.join(",", playerIds);

                try {
                    JSONObject payload = new JSONObject("{" +
                            "'contents': {'en':'" + text + "'}, " +
                            "'include_player_ids': [" + playerIdsStr + "]" +
                            "}");
                    OneSignal.postNotification(payload, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}