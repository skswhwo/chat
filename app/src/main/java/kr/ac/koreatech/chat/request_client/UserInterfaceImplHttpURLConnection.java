package kr.ac.koreatech.chat.request_client;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import kr.ac.koreatech.chat.model.User;

public class UserInterfaceImplHttpURLConnection implements UserInterface {
    @SuppressLint("StaticFieldLeak")
    @Override
    public void getUsers(final RequestCallback callback) {
        new AsyncTask<Void, Void, List<User>>() {
            // 처리 전에 호출되는 메소드
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            // 처리를 하는 메소드
            @Override
            protected List<User> doInBackground(Void... params) {
                final HttpURLConnection urlConnection;
                try {
                    URL url = new URL(uri.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                } catch (MalformedURLException e) {
                    return null;
                } catch (IOException e) {
                    return null;
                }
                final String buffer;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    buffer = reader.readLine();
                } catch (IOException e) {
                    return null;
                } finally {
                    urlConnection.disconnect();
                }
                if (TextUtils.isEmpty(buffer)) {
                    return null;
                }

                Type type = new TypeToken<Map<String, User>>(){}.getType();
                Map<String, User> map = new Gson().fromJson(buffer, type);
                return new ArrayList(map.values());
            }

            // 처리가 모두 끝나면 불리는 메소드
            @Override
            protected void onPostExecute(List<User> response) {
                super.onPostExecute(response);
                // 통신 실패로 처리
                if (response == null) {
                    callback.error(new IOException("HttpURLConnection request error"));
                } else {
                    // 통신 결과를 표시
                    callback.success(response);
                }
            }
        }.execute();
    }
}
