package com.youtube.fizantofuzz.YouTube;

import com.youtube.fizantofuzz.Notifications.MyResponse;
import com.youtube.fizantofuzz.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAfnksOlI:APA91bGSIHkEV4qD59ygK_kRS1ikRVJW2Y24qCceada50Fu9WKdJwCeTQ6ZJFrkZGvW3wVrUHR8uxaQ1cucD7FHTJlKuQ3CZso9qHddSbxMFqT1_mzqEX8F7EVJ_D5ntHdotTeM1XFF4"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
