package kr.ac.koreatech.chat.request_client;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.ac.koreatech.chat.model.Message;
import kr.ac.koreatech.chat.model.User;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageInterfaceImplRetrofit2 {
    private final MessageInterface service;

    // URL Const Vals
    String TEST_SCHEME = "https";
    String TEST_AUTHORITY = "koreatech-chat-app.firebaseio.com";
    String TEST_BASE_URI = new Uri.Builder().scheme(TEST_SCHEME).authority(TEST_AUTHORITY).build().toString();

    public MessageInterfaceImplRetrofit2() {
        /*
        #TODO
        1. Retrofit 객체 생성
        2. MessageInterface 연결
        3. (GET) messages
        4. (GET) messages/:id
        5. (POST) messages/:id
         */
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TEST_BASE_URI)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MessageInterface.class);
    }

    public void getMessages(final CallbackInterface callback) {
        /*
            #TODO

                Message의 List를 받기

                Request:
                (GET) https://koreatech-chat-app.firebaseio.com/messages

                Response:
                [
                 {
                   name: user-name,
                   text: chat-message
                 }
                 :
                ]
         */

        service.getMessages().enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                callback.success(response.body());
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable error) {
                callback.error(error);
            }
        });
    }

    public void getMessage(String id, final CallbackInterface callback) {

        /*
            #TODO

                Message 조회 하기

                Request:
                (GET) https://koreatech-chat-app.firebaseio.com/messages/:id

                Response:
                {
                  name: user-name,
                  text: chat-message
                }
         */

        service.getMessage(id).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                callback.success(response.body());
            }

            @Override
            public void onFailure(Call<Message> call, Throwable error) {
                callback.error(error);
            }
        });
    }

    public void postMessage(String name, String text, final CallbackInterface callback) {

         /*
            #TODO

                Message 생성

                Request:
                (POST) https://koreatech-chat-app.firebaseio.com/messages

                Response:
                {
                  name: user-name,
                  text: chat-message
                }
         */

        service.postMessage(name, text).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                callback.success(response.body());
            }

            @Override
            public void onFailure(Call<Message> call, Throwable error) {
                callback.error(error);
            }
        });
    }
}
