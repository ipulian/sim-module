package com.ipusoft.sim.view;

import androidx.databinding.DataBindingUtil;

import com.ipusoft.context.BaseActivity;
import com.ipusoft.sim.R;
import com.ipusoft.sim.databinding.ActivityHowToOpenRecordingBinding;

/**
 * author : GWFan
 * time   : 5/7/21 11:38 AM
 * desc   :
 */

public class HowToOpenRecordingActivity extends BaseActivity {
    private ActivityHowToOpenRecordingBinding binding;

    @Override
    protected void initViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_how_to_open_recording);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initUI() {
        binding.ivBack.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void bindLiveData() {

    }

    @Override
    protected void initRequest() {

    }


}
