package com.example.blooddonationkotli.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAlPAN8hs:APA91bFy7Gk95fjuQM_IZw7fX4HhvGWVCCpMLx4P0aH0Ryj6jBuQgwrxwgIXXS4mdwmFhHheNwBg36BjIifTEx6nurdoL1caLkJeJb-7nxFghAxpdeK-dzGVK_NnEej3VA4hGbGxZq2y"
            }
    )
    @POST("fcm/send")
    Call<REsponce> sendNotification(@Body Sender body);
}
