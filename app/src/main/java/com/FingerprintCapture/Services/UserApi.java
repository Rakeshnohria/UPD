package com.FingerprintCapture.Services;

import com.FingerprintCapture.models.request.UserRequest;
import com.FingerprintCapture.models.response.UserResponse;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by chicmic on 8/14/17.
 */

public interface UserApi {


    @FormUrlEncoded
    @POST(AllUrl.loginUrl)
    public Call<JsonObject> login(@FieldMap Map<String, String> parameters);

    @Multipart
    @POST(AllUrl.uploadUrl)
    public Call<JsonObject> uploadData(@Part("customer_id") RequestBody customer_id,
                                       @Part("name") RequestBody name,
                                       @Part("date") RequestBody date,
                                       @Part("agent_id") RequestBody agent_id,
                                       @Part("id") RequestBody id,
                                       @Part("first_name") RequestBody first_name,
                                       @Part("email") RequestBody email,
                                       @Part("fingerprint") RequestBody fingerprint,
                                       @Part MultipartBody.Part image,
                                       @Part MultipartBody.Part thumbNail
    );

    @POST(AllUrl.customerApi)
    Call<UserResponse> doUserRequest(@Body UserRequest userRequest);

    @FormUrlEncoded
    @POST(AllUrl.deceasedVerify)
    Call<JsonObject>updateVerified(@Field("deceased_id") String deceasedId);

}
