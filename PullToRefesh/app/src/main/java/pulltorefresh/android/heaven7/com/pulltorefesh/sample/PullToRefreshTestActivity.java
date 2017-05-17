package pulltorefresh.android.heaven7.com.pulltorefesh.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.android.pullrefresh.LoadingFooterView;
import com.heaven7.android.pullrefresh.PullToRefreshLayout;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;
import com.heaven7.core.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import pulltorefresh.android.heaven7.com.pulltorefesh.R;

/**
 * Created by heaven7 on 2017/5/17 0017.
 */

public class PullToRefreshTestActivity extends BaseActivity{

    private static final String TAG = "PullToRefreshTestActivity";

    @BindView(R.id.pull_refresh)
    PullToRefreshLayout mPullView;

    private QuickRecycleViewAdapter<TestBean> mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.ac_test_pullrefesh;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
        mPullView.setLayoutManager(new LinearLayoutManager(context));
        mPullView.setCallback(new PullToRefreshLayout.Callback() {
            @Override
            public void onRefresh(PullToRefreshLayout layout) {
                Logger.i(TAG,"onRefresh","");
                loadData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout layout) {
                Logger.i(TAG,"onLoadMore","");
                loadMoreData(layout);
            }

            @Override
            public void onClickFooter(PullToRefreshLayout layout, LoadingFooterView footer, int state) {
                Logger.i(TAG,"onClickFooter","state = " + state);
            }
        });
        mPullView.setAdapter(mAdapter = new QuickRecycleViewAdapter<TestBean>(
                R.layout.item_test_pull_refresh, getTestList(0)
        ) {
            @Override
            protected void onBindData(Context context, int position, TestBean item, int itemLayoutId, ViewHelper helper) {
                helper.setText(R.id.tv1, item.text1)
                     .setText(R.id.tv2, item.text2);
            }
        });
    }

    private List<TestBean> getTestList(int count ) {
        if( count == 0){
            count = 20;
        }
        List<TestBean> list = new ArrayList<>();
        for(int i = 0 ;  i < count ; i++){
            list.add(new TestBean("PullRefreshView--->heaven7--->", i));
        }
        return list;
    }

    private void loadMoreData(PullToRefreshLayout layout){
        layout.getFooterView().setState(LoadingFooterView.STATE_LOADING);
        MainWorker.postDelay(2000, new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                mAdapter.getAdapterManager().addItems(getTestList(r.nextInt(10) + 2));
                mPullView.setLoadingComplete();
                getToaster().show("load more done");
            }
        });
    }

    private void loadData() {
        MainWorker.postDelay(2000, new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                mAdapter.getAdapterManager().replaceAllItems(getTestList(r.nextInt(10) + 20));
                mPullView.setLoadingComplete();
                getToaster().show("refresh done");
            }
        });
    }

    static class TestBean extends BaseSelector{

        String text1;
        String text2;

        public TestBean(String text, int pos ) {
            this.text1 = text +"___pos_" + pos + "___1";
            this.text2 = text +"___pos_" + pos + "___2";
        }
    }

    public static class LoadingViewPerformer implements PullToRefreshLayout.PlaceHolderViewPerformer{

        @Override
        public void performPlaceHolderView(PullToRefreshLayout layout, LinearLayout phv, int flag) {
            if(phv.getChildCount() == 0){
                LayoutInflater.from(layout.getContext()).inflate(R.layout.view_pb, phv, true);
            }
        }
    }
}
