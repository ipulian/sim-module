package com.ipusoft.sim.debug;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.ipusoft.context.BaseActivity;
import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.bean.SysRecording;
import com.ipusoft.context.utils.FileUtilsKt;
import com.ipusoft.context.utils.SizeUtils;
import com.ipusoft.sim.R;
import com.ipusoft.sim.bean.UploadProgress;
import com.ipusoft.sim.databinding.SimActivitySimModuleMainBinding;
import com.ipusoft.sim.manager.UploadManager;
import com.ipusoft.sim.view.HowToOpenRecordingActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SimMainActivity extends BaseActivity {
    private SimActivitySimModuleMainBinding binding;

    @Override
    protected void initViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.sim_activity_sim_module_main);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initUI() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1024);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
        }
    }

    long l = 0;

    @Override
    protected void bindLiveData() {
        Map<String, Integer> stringIntegerMap = new HashMap<>();
        LiveDataBus.get().with("uploadProgress", UploadProgress.class).observe(this, uploadProgress -> {
            long c = System.currentTimeMillis();
            //if (c - l > 2000) {
            String phoneName = uploadProgress.getRecording().getPhoneName();
            if (uploadProgress.getProgress() == 100) {
                stringIntegerMap.remove(uploadProgress.getRecording().getPhoneName());
            } else {
                stringIntegerMap.put(phoneName, uploadProgress.getProgress());
            }
            //stringIntegerMap.put(phoneName, uploadProgress.getProgress());
            StringBuilder stringBuilder = new StringBuilder();
            Log.d("TAG", "bindLiveData: ------->" + stringIntegerMap.size());
            for (Map.Entry<String, Integer> entry : stringIntegerMap.entrySet()) {
                stringBuilder.append(entry.getKey() + "---->" + entry.getValue() + "\n");
//                if (entry.getValue() == 100){
//
//                }
            }
            binding.tv.setText(stringBuilder.toString());
//                for (int i = 0; i < 10; i++) {
//                    TextView textView = (TextView) binding.llRoot.getChildAt(i);
//                    String tag = (String) textView.getTag(i);
//                    if (tag.equals(phoneName)) {
//                        textView.setText(phoneName + "---------->" + uploadProgress.getProgress());
////                    binding.llRoot.removeViewAt(i);
////                    binding.llRoot.addView(textView);
//                        break;
//                    }
//                }
            l = c;
            // }
        });
    }

    @Override
    protected void initRequest() {

    }

    public void queryCallLog(View view) {
        //SysRecordManager.getInstance().queryRecordingFile()
    }

    public void help(View view) {
        startActivity(new Intent(this, HowToOpenRecordingActivity.class));
    }

    /**
     * if (StringUtils.isNotEmpty(absolutePath)) {
     * uploadFileRequestBody = new UploadFileRequestBody(recording, fileUploadObserver);
     * builder.addFormDataPart("record", recording.getFileName(), uploadFileRequestBody);
     * builder.addFormDataPart("fileMD5", recording.getFileMD5());
     * }
     * int callType = recording.getCallType();
     * builder.addFormDataPart("callId", recording.getCallId() + "");
     * builder.addFormDataPart("name", StringUtils.null2Empty(recording.getPhoneName()));
     * builder.addFormDataPart("phone", recording.getPhoneNumber());
     * builder.addFormDataPart("startTime", DateTimeUtils.millis2String(recording.getCallTime()));
     * builder.addFormDataPart("duration", recording.getDuration() + "");
     * builder.addFormDataPart("callResult", recording.getCallResult() + "");
     * builder.addFormDataPart("callType", callType == 1 ? "2" : callType == 2 ? "1" : callType + "");
     * builder.addFormDataPart("token", IpuSoftSDK.getToken());
     *
     * @param view
     */
    String path = "/storage/emulated/0/Mine/樊高威(18317893005)_20210527140801.mp3";

    public void uploadTest(View view) {
        //binding.llRoot.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(50));
        for (int i = 0; i < 10; i++) {
//            LinearLayout linearLayout = new LinearLayout(this);

            TextView textView = new TextView(this);
            textView.setTag("zzz" + i);
            textView.setText(i + "------->");
            textView.setLayoutParams(layoutParams);
            //ViewParent parent = textView.getParent();
            //linearLayout.addView(textView);
            //  binding.llRoot.addView(textView);

            SysRecording sysRecording = new SysRecording();
            sysRecording.setAbsolutePath(path);
            sysRecording.setFileName("樊高威(18317893005)_20210527140801.mp3");
            sysRecording.setFileMD5(FileUtilsKt.getFileMD5ToString(new File(path)));
            sysRecording.setCallId(System.currentTimeMillis());
            sysRecording.setPhoneName("zzz" + i);
            sysRecording.setPhoneNumber("18317893005");
            sysRecording.setCallTime(System.currentTimeMillis());
            sysRecording.setDuration(30);
            sysRecording.setCallResult(0);
            sysRecording.setCallType(1);

            UploadManager.getInstance().addRecording2Task(sysRecording);
            //new UploadRecordingWorker(sysRecording).executeUploadHttpTask(sysRecording);
        }
    }
}