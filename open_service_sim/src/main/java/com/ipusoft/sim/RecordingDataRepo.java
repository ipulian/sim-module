package com.ipusoft.sim;

import android.util.Pair;

import com.ipusoft.context.base.IObserver;
import com.ipusoft.context.bean.SysRecording;
import com.ipusoft.localcall.repository.SysRecordingRepo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * @author : GWFan
 * time   : 2023/12/21 21:28
 * desc   :
 */

public class RecordingDataRepo {

    public interface OnRecordCountListener {
        void recordCount(int count);
    }

    public interface OnRecordListListener {
        void recordList(List<SysRecording> list);
    }

    /**
     * 查询总条数，用于分页
     *
     * @param statusList
     * @param listener
     */
    public static void queryRecordCount(List<Integer> statusList, OnRecordCountListener listener) {
        SysRecordingRepo.queryCountByStatusForListPage(statusList, new IObserver<Integer>() {
            @Override
            public void onNext(@NonNull Integer count) {
                super.onNext(count);
                if (listener != null) {
                    listener.recordCount(count);
                }
            }
        });
    }

    /**
     * 查询上传记录
     * 分页从0开始
     *
     * @param statusList
     * @param listener
     */
    public static void queryRecordList(List<Integer> statusList, int page, int pageSize, OnRecordListListener listener) {
        SysRecordingRepo.queryByStatusForListPage(statusList, page, pageSize, new IObserver<List<SysRecording>>() {
            @Override
            public void onNext(@NotNull List<SysRecording> list) {
                super.onNext(list);
                if (listener != null) {
                    listener.recordList(list);
                }
            }
        });
    }
}
