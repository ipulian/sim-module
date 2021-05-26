package com.ipusoft.sim.component;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.ipusoft.context.utils.ScreenUtils;
import com.ipusoft.sim.R;
import com.ipusoft.sim.datastore.SimDataRepo;
import com.ipusoft.sim.utils.DateTimeUtils;
import com.ipusoft.sim.view.HowToOpenRecordingActivity;

/**
 * author : GWFan
 * time   : 5/20/21 10:44 AM
 * desc   :
 */

public class CheckRecordingFileDialog extends DialogFragment {
    private static final String TAG = "CheckRecordingFileD";
    protected static FragmentActivity mActivity;

    protected View view;

    @Override
    public void onStart() {
        super.onStart();
        Dialog mDialog = getDialog();
        if (mDialog != null) {
            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.gravity = Gravity.CENTER;
                attributes.width = 17 * ScreenUtils.getAppScreenWidth() / 24;
                attributes.height = ScreenUtils.getAppScreenHeight() / 3;
                window.setAttributes(attributes);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            view = inflater.inflate(R.layout.dialog_check_recording_file, container);
            setCancelable(false);
            initView();
        }
        return view;
    }

    public static CheckRecordingFileDialog getInstance(FragmentActivity activity) {
        CheckRecordingFileDialog.mActivity = activity;
        Bundle args = new Bundle();
        CheckRecordingFileDialog fragment = new CheckRecordingFileDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void show() {
        long lastTimestamp = SimDataRepo.getLastShowCheckRecordingPermissionDialog();
        if (!DateTimeUtils.isToday(lastTimestamp)) {
            SimDataRepo.setLastShowCheckRecordingPermissionDialog(System.currentTimeMillis());
            show(getClass().getSimpleName());
        }
    }

    private void show(String tag) {
        mActivity.runOnUiThread(() -> {
            FragmentManager fm = mActivity.getSupportFragmentManager();
            Fragment prev = fm.findFragmentByTag(tag);
            if (prev != null) {
                fm.beginTransaction().remove(prev);
            }
            CheckRecordingFileDialog.super.show(fm, tag);
        });
    }

    protected void initView() {
        TextView tvMsg = view.findViewById(R.id.tv_msg);
        TextView tvMsg2 = view.findViewById(R.id.tv_msg2);
        String tip = "我们无法检测到通话录音文件，请检查通话录音权限是否打开";
        String msg = "如何打开录音权限？";
        tvMsg.setText(tip);
        tvMsg2.setText(msg);

        tvMsg2.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, HowToOpenRecordingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        });
        view.findViewById(R.id.ll_agree).setOnClickListener(v -> dismiss());
    }
}

