package com.ipusoft.sim;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.ipusoft.context.BaseActivity;
import com.ipusoft.sim.databinding.ActivitySimModuleMainBinding;

public class SimMainActivity extends BaseActivity {
    ActivitySimModuleMainBinding binding;

    @Override
    protected void initViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sim_module_main);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void bindLiveData() {

    }

    @Override
    protected void initRequest() {

    }

    public void queryCallLog(View view) {
        //SysRecordManager.getInstance().queryRecordingFile()
    }

    public void showWindow(View view) {
//        Intent intent = new Intent(this, DialWindowService.class);
//        intent.setAction(DialWindowService.ACTION_SHOW);
//        startService(intent);
    }
}