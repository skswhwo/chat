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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.ac.koreatech.chat.view.BaseActivity;
import kr.ac.koreatech.chat.R;
import kr.ac.koreatech.chat.view.user_list.UserListActivity;
import kr.ac.koreatech.chat.model.Message;
import kr.ac.koreatech.chat.model.User;

public class ChatRoomActivity  extends BaseActivity {
    private static final int REQUEST_IMAGE = 1;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    private ListView mMessageListView;
    private EditText mMessageEditText;
    private Button mSendButton;
    private ImageView mAddMessageImageView;

    private ChatRoomAdapter mAdapter;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);

        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSendButton.setEnabled((charSequence.toString().trim().length() > 0));
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(User.currentUser.getName(), mMessageEditText.getText().toString(),null);
                message.update();
                sendPush(message.getText());
                mMessageEditText.setText("");
            }
        });

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAdapter = new ChatRoomAdapter(this, mFirebaseDatabaseReference.child(Message.ref));
        mMessageListView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.startListening();
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
                startActivity(new Intent(this, UserListActivity.class));
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

                    //Loading 이미지를 삽입 후, message의 key를 가져옴
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

                                //이미지 파일을 storage에 저장
                                //저장 경로: user_id/message_key/original_file_name
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
         /*
              1. Firebase Storage에 저장 후, CompleteListener 연결

              2. 기존 메세지의 LOADING_IMAGE_URL을 실제 업로드된 이미지의 url로 교체
         */
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
        /*
              @TODO 사용자 리스트 조회 후, Push 전송
              1. 사용자 목록에 해당하는 database reference 접근

              2. Database에서 user list 가져오기 (한번만 접근) (addListenerForSingleValueEvent)
              https://firebase.google.com/docs/database/android/read-and-write?authuser=0

              3. OneSignal로 Push 전송 (text 전송)
              https://documentation.onesignal.com/docs/android-native-sdk#section--postnotification-
         */
    }

    private String getPlayerIds(ArrayList<User> users) {
        ArrayList<String> playerIds = new ArrayList<String>();
        for (User user : users) {
            if (user.getPlayerId() != null) {
                playerIds.add("'" + user.getPlayerId() + "'");
            }
        }
        return TextUtils.join(",", playerIds);  //'player1','player2','player3',...
    }

}