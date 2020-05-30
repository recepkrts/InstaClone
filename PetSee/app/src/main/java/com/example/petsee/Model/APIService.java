package com.example.petsee.Model;

import com.example.petsee.Bildirim.MyResponse;
import com.example.petsee.Bildirim.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
        {
            "Content-Type:application/json",
            "Authorization:key=AAAApmx3Clw:APA91bGa04II0LXmAzrDeA9JSTNCGBw0qemZ2YpR8qAhTS-9xsLLCZKtoaz3NP6Oy1qSEsd_YXYjM0umj6UfkiDEN8O3g-NsKXBdZ36QyVL19HwYLRm1gMNz4wWpsSlRzI3BgWY-ZV0L"
        }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
