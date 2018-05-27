package kr.ac.koreatech.chat.view.user_list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.koreatech.chat.model.User;

public class UserListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> data;

    private DatabaseReference messageRef;
    private ValueEventListener valueEventListener;

    public UserListAdapter(Context context, DatabaseReference ref) {
        this.context = context;
        this.messageRef = ref;
        this.data = new ArrayList<User>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public User getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserListItem view = (UserListItem) convertView;
        if(view == null) {
            view = UserListItem_.build(context);
        }

        User user = getItem(position);
        view.bind(user);

        return view;
    }

    public void startListening() {
        messageRef.addValueEventListener(valueEventListener());
    }

    public void stopListening() {
        messageRef.removeEventListener(valueEventListener());
    }

    private ValueEventListener valueEventListener() {
        if (valueEventListener == null) {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    data = new ArrayList<User>();
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        User user = childDataSnapshot.getValue(User.class);
                        user.setUid(childDataSnapshot.getKey());
                        data.add(user);
                    }
                    notifyDataSetChanged();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };
        }
        return valueEventListener;
    }
}
