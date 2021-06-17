package com.ipusoft.sim.module;

import com.ipusoft.context.utils.ArrayUtils;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.http.interceptors.base.BaseSimHttpInterceptor;
import com.ipusoft.http.manager.RetrofitManager;
import com.ipusoft.sim.api.SimAPIService;
import com.ipusoft.sim.iface.OnSimCallPhoneResultListener;
import com.ipusoft.sim.module.base.BaseSimService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author : GWFan
 * time   : 5/27/21 8:52 AM
 * desc   :
 */

public class SimService extends BaseSimService {
    private static final String TAG = "SimService";

    private static class SimServiceHolder {
        private final static SimService INSTANCE = new SimService();
    }

    public static SimService getInstance() {
        BaseSimService.initInterceptors();
        return SimServiceHolder.INSTANCE;
    }

    @Override
    public <T> void simCallPhone(Map<String, Object> params, Class<T> clazz,
                                 @NonNull OnSimCallPhoneResultListener<T> listener) {
        /*
         * 执行拦截器
         */
        List<BaseSimHttpInterceptor> interceptors = getInterceptors();
        if (ArrayUtils.isNotEmpty(interceptors)) {
            for (BaseSimHttpInterceptor interceptor : interceptors) {
                interceptor.interceptor(params);
            }
        }
        /*
         * 执行网络请求
         */
        RetrofitManager.getInstance()
                .getRetrofit().create(SimAPIService.class)
                .simCallPhone(RetrofitManager.getInstance()
                        .getRequestBody(params))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call,
                                           @NotNull Response<ResponseBody> response) {
                        try {
                            ResponseBody body = response.body();
                            if (body != null) {
                                String string = body.string();
                                if (StringUtils.isNotEmpty(string)) {
                                    T t = GsonUtils.fromJson(string, clazz);
                                    listener.onSucceed(t);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            listener.onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        listener.onFailure(t);
                    }
                });
    }
}
