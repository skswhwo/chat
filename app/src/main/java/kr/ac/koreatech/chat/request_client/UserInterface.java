package kr.ac.koreatech.chat.request_client;

import android.net.Uri;

import java.util.List;

import kr.ac.koreatech.chat.model.User;

public interface UserInterface {
    // URL Const Vals
    String SCHEME = "https";
    String AUTHORITY = "koreatech-chat-app.firebaseio.com";
    String PATH = "/users.json";
    // URI
    Uri uri = new Uri.Builder()
            .scheme(SCHEME)
            .authority(AUTHORITY)
            .path(PATH)
            .build();

    void getUsers(RequestCallback callback);

    interface RequestCallback {
        // 생성시의 Callback
        void success(List<User> users);

        // 실패시의 Callback
        void error(Throwable throwable);
    }
}
