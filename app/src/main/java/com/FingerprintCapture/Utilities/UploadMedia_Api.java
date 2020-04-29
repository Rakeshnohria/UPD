/*
package com.FingerprintCapture.Utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.FingerprintCapture.Services.AllUrl;
import com.FingerprintCapture.Services.RestClient;
import com.FingerprintCapture.Services.UserApi;
import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


*/
/**
 * Created by Manjot on 4/6/2018.
 *//*


public class UploadMedia_Api  implements ProgressRequestBody.UploadCallbacks {
    Context context;
    byte[] byteArray;
    String token;
    RequestBody imagefile1;
   // Dialog dialog1;
   ProgressDialog  pd;
   String imgPath;
    private File imageFile;

    public void uploadMedia(FragmentActivity activity, byte[] byteArray, String token,String imgPath) {
        this.context = activity;
        this.byteArray = byteArray;
        this.token = token;
        this.imgPath = imgPath;

        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);
        pd.setMax(100);
        pd.setProgress(0);
        pd.setTitle("Please wait...");

      //  total = files_uploaded;

       // pd.setMessage("Uploading "+total +" / "+ arrayListImg.size());
        pd.setMessage("Uploading "+1 +" / "+1);
        pd.show();



        MultipartBody.Part image1 = null;

        MultipartBody.Part filePart=null;
       try {
           File file = new File(imgPath);
           ProgressRequestBody fileBody = new ProgressRequestBody(file, this,streamFileType(file));
            filePart =
                   MultipartBody.Part.createFormData("image", file.getName(), fileBody);
       }catch (Exception e){

       }
    }


    @Override
    public void onProgressUpdate(int percentage) {
        Log.e("printNeww",percentage+" Percentage");
        pd.setProgress(percentage);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        pd.setProgress(100);
    }


    public String  streamFileType(File file)
    {

        String exsistingFileName  = file.getName();
        if (exsistingFileName.endsWith(".jpg"))
        {
            return "image/jpg";
        }
        if(exsistingFileName.endsWith(".JPG")){

            return "image/jpg";
        }
        if (exsistingFileName.endsWith(".png")) {

            return "image/png";

        }
        if (exsistingFileName.endsWith(".PNG")) {

            return "image/png";
        }
        if (exsistingFileName.endsWith(".gif")) {

            return "image/gif";

        }
        if (exsistingFileName.endsWith(".GIF")) {

            return "image/gif";

        }
        if (exsistingFileName.endsWith(".jpeg")) {

            return "image/jpeg";

        }
        if (exsistingFileName.endsWith(".JPEG")) {

            return "image/jpeg";

        }
        if(exsistingFileName.endsWith(".mp3")){

            return "audio/mp3";

        }
        if(exsistingFileName.endsWith(".MP3")){

            return "audio/mp3";

        }
        if (exsistingFileName.endsWith(".mp4")) {

            return "video/mp4";

        }
        if (exsistingFileName.endsWith(".avi"))
        {
            return "video/.avi";

        }
        if (exsistingFileName.endsWith(".ogg")) {

            return "video/ogg";

        }
        if (exsistingFileName.endsWith(".3gp")) {

            return "video/3gp";
        }

        return "";
    }

    public void uploadMedia(Context context, String mCustomerId, String name, String dob, String agentId, String id, String firstName, String email, String mBitmapImage, Uri imageUri, Uri thumbNailUri) {

        this.context = context;
        this.byteArray = byteArray;
        this.token = token;
        this.imgPath = imgPath;

        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);
        pd.setMax(100);
        pd.setProgress(0);
        pd.setTitle("Please wait...");

        pd.show();



        MultipartBody.Part image1 = null;

        MultipartBody.Part filePart=null;
        try {
            File file = new File(imageUri.getPath().toString());
            ProgressRequestBody fileBody = new ProgressRequestBody(file, this,streamFileType(file));
            filePart =
                    MultipartBody.Part.createFormData("image", file.getName(), fileBody);
        }catch (Exception e){

        }





        Retrofit retrofit = RestClient.build(AllUrl.baseUrl);
        UserApi userApi = retrofit.create(UserApi.class);
        Call<JsonObject> call = userApi.uploadData(createPartFromString(mCustomerId),
                createPartFromString(name),
                createPartFromString(dob),
                createPartFromString(agentId),
                createPartFromString(id),
                createPartFromString(firstName),
                createPartFromString(email),
                createPartFromString(mBitmapImage),
                filePart,
                */
/*prepareFilePart("image", imageUri),*//*

                prepareFilePart("thumb_image", thumbNailUri)

        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {
                if (response.body().get("status").toString().equals("200")) {
                    */
/*final UploadImageResponse uploadImageResponse = new Gson().fromJson(response.body(), UploadImageResponse.class);
                    CustomerData customerData = uploadImageResponse.getCustomerData().get(0);
                    String fingerPrintId ;

                    //send customer id for old customers not having upd id

                    fingerPrintId=customerData.getUpdId()==null?customerData.getId():customerData.getUpdId();
                    if ( !mIsImage ) {
                        if (progressLayout != null) {
                            progressLayout.setVisibility(View.GONE);
                        }
                        BioServerEnrollmentEventHandler bioServerEnrollmentEventHandler = new BioServerEnrollmentEventHandler(fingerPrintId, mBitmapImage,AddImageActivity.this);
                        bioServerEnrollmentEventHandler.doEnrollment(AddImageActivity.this,uploadImageResponse);
                    }
                    else {

                        success(uploadImageResponse);
                    }*//*

                } else {
                    */
/*fail();*//*

                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                */
/*fail();*//*


            }
        });
    }

    @NonNull
    private RequestBody createPartFromString(String string) {
        return RequestBody.create(MultipartBody.FORM, string);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath().toString());
        imageFile = file;
        System.out.println("getfile " + file.toString() + " " + file.getName());
        RequestBody requestBody = RequestBody.create(MediaType.parse(fileUri.getPath().toString()), file);
        return MultipartBody.Part.createFormData(partName, fileUri.getPath(), requestBody);
    }
}
*/
