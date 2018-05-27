package kr.ac.koreatech.chat.view.chat_room;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.koreatech.chat.R;
import kr.ac.koreatech.chat.model.Message;

@EViewGroup(R.layout.item_message)
public class ChatRoomItem extends LinearLayout {

    @ViewById
    TextView titleTextView;

    @ViewById
    TextView subTitleTextView;

    @ViewById
    ImageView messageImageView;

    Context context;

    public ChatRoomItem(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(Message message) {
        titleTextView.setText(message.getText());
        subTitleTextView.setText(getNameAndTime(message));

        if (message.getImageUrl() != null) {
            Glide.with(context )
                    .load(message.getImageUrl())
                    .into(messageImageView);
            messageImageView.setVisibility(ImageView.VISIBLE);
            titleTextView.setVisibility(TextView.GONE);
        } else {
            messageImageView.setVisibility(ImageView.GONE);
            titleTextView.setVisibility(TextView.VISIBLE);
        }
    }

    private String getNameAndTime(Message message) {
        String res = message.getName();
        String time = getTimeString(message);
        if (time != null) {
            res = res.concat(" (" + time + ")");
        }
        return res;
    }

    private String getTimeString(Message message) {
        if (message.getTime()> 0) {
            Date date = new Date(message.getTime());
            SimpleDateFormat formatter = new SimpleDateFormat("MM월 dd일 HH시 mm분");
            return formatter.format(date);
        }
        return null;
    }
}