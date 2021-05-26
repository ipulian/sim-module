package com.ipusoft.sim.module

import com.ipusoft.database.bean.SysRecording
import com.ipusoft.sim.bean.UploadResponse
import com.ipusoft.network.RetrofitManager
import com.ipusoft.sim.api.SimAPIService
import com.ipusoft.sim.base.UploadFileObserve
import com.ipusoft.sim.upload.MultipartBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * author : GWFan
 * time   : 4/9/21 11:25 AM
 * desc   :
 */

class PublicHttpService {
    companion object {
        val TAG = "PublicHttpService"

        /**
         * 文件上传
         */
        fun uploadRecordingFile(sysRecording: SysRecording, uploadFileObserve: UploadFileObserve<UploadResponse>) {
            uploadRecordingFiles(listOf(sysRecording), uploadFileObserve)
        }

        /**
         * 多文件上传
         */
        private fun uploadRecordingFiles(sysRecording: List<SysRecording>, uploadFileObserve: UploadFileObserve<UploadResponse>) {
            RetrofitManager.getInstance().getRetrofit(false).create(SimAPIService::class.java)
                    .uploadFile(MultipartBuilder.filesToMultipartBody(sysRecording, uploadFileObserve))
                    .enqueue(object : Callback<UploadResponse?> {
                        override fun onResponse(call: Call<UploadResponse?>, response: Response<UploadResponse?>) {
                            uploadFileObserve.onUploadSuccess(response.body())
                        }

                        override fun onFailure(call: Call<UploadResponse?>, t: Throwable) {
                            uploadFileObserve.onUploadFail(t)
                        }
                    })
        }

        private fun uploadRecordingFilesAsync(sysRecording: List<SysRecording>, uploadFileObserve: UploadFileObserve<ResponseBody>) {
//            Log.d(TAG, "uploadRecordingFilesAsync: ----------->" + GsonUtils.toJson(sysRecording))
//            val execute = RetrofitManager.getInstance().getRetrofit(false).create(APIService::class.java)
//                    .uploadFile(MultipartBuilder.filesToMultipartBody(sysRecording, uploadFileObserve))
//                    .execute()
//            val body = execute.body()
//            Log.d(TAG, "uploadRecordingFilesAsync: --------->" + GsonUtils.toJson(body))
        }
    }
}