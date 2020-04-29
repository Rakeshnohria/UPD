package com.FingerprintCapture.BioServerWebServices.BioServerRestApiClientConfig;




import com.FingerprintCapture.BioServerWebServices.Constants.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BioServerApiClientConfig {
    /**
     * Retrofit Api Implementation
     **/
    public Retrofit getRetrofit() {
        return initializeRetrofitService(Constants.BioServerRestClientConfig.sBIOSERVER_BASE_URL);
    }

    /**
     * Initialize Retrofit Api Service
     *
     * @param pBaseUrl - Url for Api
     *                 <p>
     *                 Build Connection or handshaking with url and Model
     **/
    private Retrofit initializeRetrofitService(String pBaseUrl) {
        return new Retrofit.Builder()
                .baseUrl(pBaseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
