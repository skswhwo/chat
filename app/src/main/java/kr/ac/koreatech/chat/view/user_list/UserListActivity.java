package kr.ac.koreatech.chat.view.user_list;

import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import kr.ac.koreatech.chat.R;
import kr.ac.koreatech.chat.model.User;
import kr.ac.koreatech.chat.view.BaseActivity;

public class UserListActivity extends BaseActivity {
    private ListView mUserListView;
    private UserListAdapter mAdapter;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mUserListView = (ListView) findViewById(R.id.userListView);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAdapter = new UserListAdapter(this, mFirebaseDatabaseReference.child(User.ref));
        mUserListView.setAdapter(mAdapter);
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
}
