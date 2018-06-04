package kr.ac.koreatech.chat.request_client;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.ac.koreatech.chat.model.User;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class UserInterfaceImplRetrofit2 implements UserInterface {
    private final UserService service;

    public UserInterfaceImplRetrofit2() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).build().toString())
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(UserService.class);
    }

    @Override
    public void getUsers(final RequestCallback callback) {
        service.getUser().enqueue(new Callback<Map<String, User>>() {
            @Override
            public void onResponse(Call<Map<String, User>> call, Response<Map<String, User>> response) {
                final List<User> users = new ArrayList(response.body().values());
                callback.success(users);
            }

            @Override
            public void onFailure(Call<Map<String, User>> call, Throwable error) {
                callback.error(error);
            }
        });
    }

    private interface UserService {
        @GET(PATH) //users.json
        Call<Map<String, User>> getUser();
    }

}
