package kr.ac.koreatech.chat.request_client;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.ac.koreatech.chat.model.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserInterfaceImplOkHttp3 implements UserInterface  {
    private Handler handler = new Handler();

    @Override
    public void getUsers(final RequestCallback callback) {
        final Request request = new Request.Builder()
                // URL 생성
                .url(uri.toString())
                .get()
                .build();
        // 클라이언트 개체를 만듬
        final OkHttpClient client = new OkHttpClient();
        // 새로운 요청을 한다
        client.newCall(request).enqueue(new Callback() {
            // 통신이 성공했을 때
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 통신 결과를 로그에 출력한다
                final String responseBody = response.body().string();

                Type type = new TypeToken<Map<String, User>>(){}.getType();
                final Map<String, User> map = new Gson().fromJson(responseBody, type);
                final List<User> users = new ArrayList(map.values());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.success(users);
                    }
                });
            }

            // 통신이 실패했을 때
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.error(e);
                    }
                });
            }
        });
    }
}
