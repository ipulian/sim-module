package com.ipusoft.sim.component;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.ipusoft.context.utils.SizeUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sim.R;
import com.ipusoft.sim.iface.OnCancelClickListener;
import com.ipusoft.sim.iface.OnConfirmClickListener;


/**
 * author : GWFan
 * time   : 2020/5/19 14:35
 * desc   :
 */

public class IAlertDialog extends DialogFragment implements View.OnClickListener {

    protected static FragmentActivity mActivity;

    protected View view;

    private String title = "提示", msg, confirmText;

    private OnConfirmClickListener confirmClickListener;

    private OnCancelClickListener cancelClickListener;

    private boolean showCancelBtn = true;

    private IAlertDialog() {
    }

    public static IAlertDialog getInstance(FragmentActivity activity) {
        mActivity = activity;
        Bundle args = new Bundle();
        IAlertDialog fragment = new IAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public IAlertDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public IAlertDialog setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public IAlertDialog setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.confirmClickListener = listener;
        return this;
    }

    public IAlertDialog setOnCancelClickListener(OnCancelClickListener listener) {
        this.cancelClickListener = listener;
        return this;
    }

    public IAlertDialog setShowCancelBtn(boolean showCancelBtn) {
        this.showCancelBtn = showCancelBtn;
        return this;
    }

    public IAlertDialog setConfirmText(String text) {
        this.confirmText = text;
        return this;
    }

    public void show() {
        show(getClass().getSimpleName());
    }

    public void show(String tag) {
        mActivity.runOnUiThread(() -> {
            FragmentManager fm = mActivity.getSupportFragmentManager();
            Fragment prev = fm.findFragmentByTag(tag);
            if (prev != null) {
                fm.beginTransaction().remove(prev);
            }
            show(fm, tag);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog mDialog = getDialog();
        if (mDialog != null) {
            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.gravity = Gravity.CENTER;
                attributes.width = SizeUtils.dp2px(275);
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
            view = inflater.inflate(R.layout.sim_layout_alert_dialog, container);
            initView();
        }
        return view;
    }

    protected void initView() {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvMsg = view.findViewById(R.id.tv_msg);
        LinearLayout llMsg = view.findViewById(R.id.ll_msg);
        LinearLayout llContent = view.findViewById(R.id.ll_content);
        tvTitle.setText(title);

        tvMsg.setText(msg);
        if (StringUtils.isEmpty(msg)) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llContent.getLayoutParams();
            layoutParams.height = SizeUtils.dp2px(76);
            llContent.setLayoutParams(layoutParams);
        } else {
            llMsg.setVisibility(View.VISIBLE);
        }

        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        if (StringUtils.isNotEmpty(confirmText)) {
            tvConfirm.setText(confirmText);
        }

        if (showCancelBtn) {
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setOnClickListener(v -> dismiss());
        } else {
            tvCancel.setVisibility(View.GONE);
        }

        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_confirm) {
            dismiss();
            if (confirmClickListener != null) {
                confirmClickListener.onConfirm();
            }
        } else if (v.getId() == R.id.tv_cancel) {
            dismiss();
            if (cancelClickListener != null) {
                cancelClickListener.onCancel();
            }
        }
    }
}
