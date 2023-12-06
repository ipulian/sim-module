package com.ipusoft.sim.api;

import com.ipusoft.http.HttpConstant;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * author : GWFan
 * time   : 5/14/21 9:58 AM
 * desc   :
 * @author gwfan
 */

public interface SimAPIService {

    /**
     * SIM外呼风控
     *
     * @param requestBody
     * @return
     */
    @POST("/call/simCallPhone")
    @Headers({HttpConstant.CONTENT_TYPE})
    Call<ResponseBody> simCallPhone(@Body RequestBody requestBody);
}
