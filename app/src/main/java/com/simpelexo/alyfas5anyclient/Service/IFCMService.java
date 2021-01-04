package com.simpelexo.alyfas5anyclient.Service;



import com.simpelexo.alyfas5anyclient.Model.FCMResponse;
import com.simpelexo.alyfas5anyclient.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:Key=AAAAviLgtK0:APA91bGAut51hxRqxHBmP0hQnvi_P7ZlgnGIWlVF9GW0ccwqWYDkgBxTvKvVY-rDrqaXRWUSrBhDWKWTAo5bjdVdCv2hx8JhvawSlkrB34rNzJQ7G_hFLpuz4cztillZXxWu3XFy5aLj"

    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
