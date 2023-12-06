package com.ipusoft.sim.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ipusoft.utils.SysRecordingUtils;
import com.ipusoft.sim.R;
import com.ipusoft.sim.databinding.SimActivityHowToOpenRecordingBinding;

/**
 * author : GWFan
 * time   : 5/7/21 11:38 AM
 * desc   : 如何打开录音功能
 */

public class HowToOpenRecordingActivity extends AppCompatActivity implements View.OnClickListener {
    private SimActivityHowToOpenRecordingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.sim_activity_how_to_open_recording);
        binding.ivBack.setOnClickListener(v -> finish());
        binding.tvToOpen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.tvToOpen.getId()) {
            if (SysRecordingUtils.isHUAWEI()) {
                SysRecordingUtils.startHuaweiRecord();
            } else if (SysRecordingUtils.isMIUI()) {
                SysRecordingUtils.startXiaomiRecord();
            } else if (SysRecordingUtils.isOPPO()) {
                SysRecordingUtils.startOppoRecord();
            } else if (SysRecordingUtils.isVIVO()) {
                SysRecordingUtils.startViVoRecord();
            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
            }
        }
    }
}
