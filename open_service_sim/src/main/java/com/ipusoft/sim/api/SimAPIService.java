package com.ipusoft.sim.api;

import com.ipusoft.sim.bean.SimRiskControl;
import com.ipusoft.sim.bean.UploadResponse;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    String JSON_TYPE = "Content-type:application/json;charset=UTF-8";

    /**
     * 录音文件上传
     *
     * @param body
     * @return
     */
    @POST("/app/recorder/uploadCall")
    Call<UploadResponse> uploadFile(@Body MultipartBody body);

    /**
     * SIM外呼风控
     *
     * @param requestBody
     * @return
     */
    @POST("/call/simCallPhone")
    @Headers({JSON_TYPE})
    Observable<SimRiskControl> simCallPhone(@Body RequestBody requestBody);
}
