package kr.ac.koreatech.chat.view.user_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.koreatech.chat.R;
import kr.ac.koreatech.chat.model.User;

public class UserListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<User> data;

    private DatabaseReference messageRef;
    private ValueEventListener valueEventListener;

    public UserListAdapter(Context context, DatabaseReference ref) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messageRef = ref;
        this.data = new ArrayList<User>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_user, parent,false);
        }

        User user = data.get(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        TextView emailTextView = (TextView) convertView.findViewById(R.id.subTitleTextView);
        TextView optionTextView = (TextView) convertView.findViewById(R.id.optionTextView);

        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        optionTextView.setText(user.getIsOnline()? "Online":"");

        return convertView;
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
                    /*
                    @TODO ListView에 적용할 User data 정제

                    1. dataSnapshot을 User model의 list로 변환

                    2. ListView에 데이터의 변경을 알림

                    */
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
