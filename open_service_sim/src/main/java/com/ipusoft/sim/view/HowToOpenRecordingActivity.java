package com.ipusoft.sim.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ipusoft.sim.R;
import com.ipusoft.sim.databinding.SimActivityHowToOpenRecordingBinding;

/**
 * author : GWFan
 * time   : 5/7/21 11:38 AM
 * desc   : 如何打开录音功能
 */

public class HowToOpenRecordingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimActivityHowToOpenRecordingBinding binding = DataBindingUtil
                .setContentView(this, R.layout.sim_activity_how_to_open_recording);
        binding.ivBack.setOnClickListener(v -> finish());
    }
}
