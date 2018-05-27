package kr.ac.koreatech.chat.view.user_list;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import kr.ac.koreatech.chat.R;
import kr.ac.koreatech.chat.model.User;

@EViewGroup(R.layout.item_user)
public class UserListItem extends LinearLayout {

    public UserListItem(Context context) {
        super(context);
    }

    @ViewById
    TextView titleTextView;

    @ViewById
    TextView subTitleTextView;

    @ViewById
    TextView optionTextView;

    public void bind(User user) {
        titleTextView.setText(user.getName());
        subTitleTextView.setText(user.getEmail());
        optionTextView.setText(user.getIsOnline()? "Online":"");
    }
}
