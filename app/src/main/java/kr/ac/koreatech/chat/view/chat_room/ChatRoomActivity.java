package kr.ac.koreatech.chat.view.chat_room;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private ListView mMessageListView;
    private EditText mMessageEditText;
    private Button mSendButton;

    private ChatRoomAdapter mAdapter;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);

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
                Message message = new Message(User.currentUser.name, mMessageEditText.getText().toString());
                message.update();
                sendPush(message.text);
                mMessageEditText.setText("");
            }
        });

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAdapter = new ChatRoomAdapter(this, mFirebaseDatabaseReference.child(Message.ref));
        mMessageListView.setAdapter(mAdapter);
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

    private void sendPush(final String text) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(User.ref);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList playerIds= new ArrayList<String>();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    User user = childDataSnapshot.getValue(User.class);
                    if (user.playerId != null) {
                        playerIds.add("'" + user.playerId + "'");
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