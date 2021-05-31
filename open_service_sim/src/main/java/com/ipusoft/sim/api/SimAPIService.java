package com.ipusoft.sim.api;

import com.ipusoft.context.http.HttpConstant;
import com.ipusoft.sim.bean.UploadResponse;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
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
 */

public interface SimAPIService {
    /**
     * 录音文件上传
     *
     * @param body
     * @return
     */
    @POST("/app/recorder/uploadCall")
    Observable<UploadResponse> uploadFile(@Body MultipartBody body);

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
