package kr.ac.koreatech.chat.view.chat_room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import kr.ac.koreatech.chat.model.Message;

public class ChatRoomAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Message> data;

    private DatabaseReference messageRef;
    private ChildEventListener childEventListener;

    public ChatRoomAdapter(Context context, DatabaseReference ref) {
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messageRef = ref;
        this.data = new ArrayList<Message>();
    }

    //ListView에 표시될 아이템의 개수
    @Override
    public int getCount() {
        return data.size();
    }

    //position에 해당하는 아이템을 객체형태로 반환
    @Override
    public Message getItem(int position) {
        return data.get(position);
    }

    //position에 해당하는 아이템의 Id를 반환
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Data를 ListView의 아이템 뷰로 만들어주는 메소드
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatRoomItem view = (ChatRoomItem) convertView;
        if(view == null) {
            view = ChatRoomItem_.build(context);
        }

        Message message = getItem(position);
        view.bind(message);

        return view;
    }

    //database listener 연결 여부 결정 (Activity에서 view의 상태에 따라 결정함)
    public void startListening() {
        messageRef.addChildEventListener(childEventListener());
    }
    public void stopListening() {
        messageRef.removeEventListener(childEventListener());
    }

    //Firebase Database listener (data의 추가, 변경을 감지)
    private ChildEventListener childEventListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    /*
                        @TODO message 모델 추가 및 UI 반영
                     */
                    Message message = dataSnapshot.getValue(Message.class);
                    message.setId(dataSnapshot.getKey());
                    if (data.contains(message) == false) {
                        data.add(message);
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    /*
                        @TODO 이전메세지 교체
                     */
                    Message newMessage = dataSnapshot.getValue(Message.class);
                    newMessage.setId(dataSnapshot.getKey());
                    for (Message message : data) {
                        if (message.getId().equals(newMessage.getId())) {
                            data.set(data.indexOf(message), newMessage);
                            notifyDataSetChanged();
                            break;
                        }
                    }
                }
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