package kr.ac.koreatech.chat.view.user_list;

import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import kr.ac.koreatech.chat.R;
import kr.ac.koreatech.chat.model.User;
import kr.ac.koreatech.chat.view.BaseActivity;

@EActivity(R.layout.activity_user_list)
public class UserListActivity extends BaseActivity {

    @ViewById
    ListView userListView;

    private UserListAdapter adapter;

    @AfterViews
    void initUserList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        adapter = new UserListAdapter(this, ref.child(User.ref));
        userListView.setAdapter(adapter);
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
}
