package pulltorefresh.android.heaven7.com.pulltorefesh.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.adapter.util.ViewHelper2;
import com.heaven7.android.pullrefresh.LoadingFooterView;
import com.heaven7.android.pullrefresh.PullToRefreshLayout;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;
import com.heaven7.java.study.processor.PrintMe;
import com.heaven7.java.study.processor.Proxy_heaven7;
import com.heaven7.java.study.processor.ShouldProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import pulltorefresh.android.heaven7.com.pulltorefesh.R;

/**
 * Created by heaven7 on 2017/5/17 0017.
 */
@Proxy_heaven7
@PrintMe
public class PullToRefreshTest2Activity extends BaseActivity{

    private static final String TAG = "PullToRefreshTestActivity";
    private static final int STATE_ERROR = 1;
    private static final int STATE_EMPTY = 2;

    @BindView(R.id.pull_refresh)
    PullToRefreshLayout mPullView;

    private QuickRecycleViewAdapter<TestBean> mAdapter;
    private View mEmptyView;
    private View mErrorView;

    @Override
    public int getLayoutId() {
        return R.layout.ac_test_pullrefesh;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
        mEmptyView = getLayoutInflater().inflate(R.layout.include_empty_data, mPullView.getContentOverlapView(), true);
        mErrorView = getLayoutInflater().inflate(R.layout.include_net_error, mPullView.getWholeOverlapView(), true);
        mErrorView.findViewById(R.id.tv_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPullView.getWholeOverlapView().setVisibility(View.GONE);
                mPullView.getSwipeRefreshLayout().setRefreshing(true);
                loadData();
            }
        });

        mPullView.setLayoutManager(new LinearLayoutManager(context));
        mPullView.setCallback(new PullToRefreshLayout.Callback() {
            @Override
            public void onRefresh(PullToRefreshLayout layout) {
                Logger.i(TAG,"onRefresh","");
                if(layout.getState() == STATE_EMPTY){
                    mPullView.getContentOverlapView().setVisibility(View.GONE);
                }
                loadData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout layout) {
                Logger.i(TAG,"onLoadMore","");
                loadMoreData(layout);
            }

            @Override
            public void onClickFooter(PullToRefreshLayout layout, View footer, int state) {
                Logger.i(TAG,"onClickFooter","state = " + state);
            }
        });
        mPullView.setAdapter(mAdapter = new QuickRecycleViewAdapter<TestBean>(
                R.layout.item_test_pull_refresh, getTestList(0)
        ) {
            @Override
            protected void onBindData(Context context, int position, TestBean item, int itemLayoutId, ViewHelper2 helper) {
                helper.setText(R.id.tv1, item.text1)
                     .setText(R.id.tv2, item.text2);
            }
        });
        mPullView.setStatePerformDelegate(new PullToRefreshLayout.StatePerformDelegate() {
            @Override
            public void performState(PullToRefreshLayout layout,int preState,int state) {
                 switch (state){
                     case STATE_EMPTY:
                         layout.getSwipeRefreshLayout().setRefreshing(false);
                         layout.getWholeOverlapView().setVisibility(View.GONE);
                         layout.getContentOverlapView().setVisibility(View.VISIBLE);
                         break;
                     case STATE_ERROR:
                         layout.getSwipeRefreshLayout().setRefreshing(false);
                         layout.getWholeOverlapView().setVisibility(View.VISIBLE);
                         layout.getContentOverlapView().setVisibility(View.GONE);
                         break;
                 }
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

    @ShouldProxy
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

    @ShouldProxy
    private void loadData() {
        //mock error. empty. normal
        int result = (new Random().nextInt(8) + 1) % 3;
        switch (result){
            case 0: { // normal
                MainWorker.postDelay(2000, new Runnable() {
                    @Override
                    public void run() {
                        Random r = new Random();
                        mAdapter.getAdapterManager().replaceAllItems(getTestList(r.nextInt(10) + 20));
                        mPullView.setLoadingComplete();
                        getToaster().show("refresh done");
                    }
                });
            }break;

            case 1: {
                MainWorker.postDelay(1000, new Runnable() {
                    @Override
                    public void run() {
                        mPullView.setState(STATE_EMPTY);
                        getToaster().show("Empty");
                    }
                });
            }break;

            case 2: {
                MainWorker.postDelay(1000,new Runnable() {
                    @Override
                    public void run() {
                        mPullView.setState(STATE_ERROR);
                        getToaster().show("error");
                    }
                });
            }break;

            default:
                System.err.println("wrong number = " + result);
        }
    }

    static class TestBean extends BaseSelector{

        String text1;
        String text2;

        public TestBean(String text, int pos ) {
            this.text1 = text +"___pos_" + pos + "___1";
            this.text2 = text +"___pos_" + pos + "___2";
        }
    }
}
