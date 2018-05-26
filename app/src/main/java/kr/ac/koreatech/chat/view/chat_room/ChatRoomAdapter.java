package kr.ac.koreatech.chat.view.chat_room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;

import kr.ac.koreatech.chat.R;
import kr.ac.koreatech.chat.model.Message;

public class ChatRoomAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Message> data;

    private DatabaseReference messageRef;
    private ChildEventListener childEventListener;

    public ChatRoomAdapter(Context context, DatabaseReference ref) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messageRef = ref;
        this.data = new ArrayList<Message>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_message, parent,false);
        }

        Message message = data.get(position);

        TextView messageTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        TextView writerTextView = (TextView) convertView.findViewById(R.id.subTitleTextView);

        messageTextView.setText(message.getText());
        writerTextView.setText(message.getNameAndTime());

        return convertView;
    }

    public void startListening() {
        messageRef.addChildEventListener(childEventListener());
    }

    public void stopListening() {
        messageRef.removeEventListener(childEventListener());
    }

    private ChildEventListener childEventListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    /*
                        @TODO message 모델 추가 및 UI 반영
                     */
                    Message message = dataSnapshot.getValue(Message.class);
                    data.add(message);
                    notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
        }

        return childEventListener;
    }
}