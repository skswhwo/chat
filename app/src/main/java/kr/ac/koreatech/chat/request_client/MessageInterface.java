package kr.ac.koreatech.chat.request_client;

import java.util.List;

import kr.ac.koreatech.chat.model.Message;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface MessageInterface {

    @GET("messages")
    Call<List<Message>> getMessages();

    @FormUrlEncoded
    @POST("messages")
    Call<Message> postMessage(@Field("name") String name, @Field("text") String text);

    /*
        #TODO
        Message 조회

        Request:
        (GET) https://koreatech-chat-app.firebaseio.com/messages/:id

        Response:
        Message model

    */
}
