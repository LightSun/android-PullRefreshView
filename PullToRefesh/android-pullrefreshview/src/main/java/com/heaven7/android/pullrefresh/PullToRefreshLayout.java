package com.heaven7.android.pullrefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.heaven7.adapter.AbstractLoadMoreScrollListener;
import com.heaven7.adapter.ISelectable;
import com.heaven7.adapter.QuickRecycleViewAdapter;


/**
 * the pull to refresh view.
 * Created by heaven7 on 2017/5/17 0017.
 */

public class PullToRefreshLayout extends FrameLayout {

    private SwipeRefreshLayout mRefreshlayout;
    private RecyclerView mRv;
    /** a place holder view that can show loading, error,tips */
    private LinearLayout mPlaceHolderView;

    private LoadMoreScrollListenerImpl mLoadMoreListenerImpl;
    private LoadingFooterView mFooterView;

    private Callback mCallback;
    private PlaceHolderViewPerformer mHolderPerformer;

    /**
     * the callback of {@linkplain PullToRefreshLayout}
     */
    public static abstract class Callback{

        /**
         * called on refresh
         * @param layout the PullToRefreshLayout
         */
        public void onRefresh(PullToRefreshLayout layout) {

        }
        /**
         * called on load more
         * @param layout the PullToRefreshLayout
         */
        public void onLoadMore(PullToRefreshLayout layout) {

        }

        /**
         * called on click footer
         * @param layout the PullToRefreshLayout
         */
        public void onClickFooter(PullToRefreshLayout layout, LoadingFooterView footer, int state){

        }
    }

    /**
     * the place holder performer.
     */
    public interface PlaceHolderViewPerformer{
        /**
         * called on perform the place holder view/
         * @param layout the layout
         * @param placeHolderView the place holder view.
         * @param flag the code which comes from {@linkplain PullToRefreshLayout#showPlaceHolderView(int)}
         */
        void performPlaceHolderView(PullToRefreshLayout layout, LinearLayout  placeHolderView, int flag);
    }


    public PullToRefreshLayout(@NonNull Context context) {
       this(context, null);
    }

    public PullToRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PullToRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public PullToRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                               @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.widget_refresh_view, this);
        mRefreshlayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRv = (RecyclerView) findViewById(R.id.rv);
        mPlaceHolderView = (LinearLayout) findViewById(R.id.vg_loading);

        //mRefreshlayout.setColorSchemeColors(context.getResources().getColor(R.color.bg_color_036ddd));
        mRv.setLayoutManager(new LinearLayoutManager(context));
        mLoadMoreListenerImpl = new LoadMoreScrollListenerImpl();
        mRefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCallback.onRefresh(PullToRefreshLayout.this);
            }
        });
        mRv.addOnScrollListener(mLoadMoreListenerImpl);
        //footer
        mFooterView = new LoadingFooterView(getContext());
        mFooterView.setOnClickListener(new OnClickFooterListenerImpl());
    }

    /**
     * set the place holder view performer
     * @param performer the performer.
     */
    public void setPlaceHolderViewPerformer(PlaceHolderViewPerformer performer) {
        this.mHolderPerformer = performer;
    }

    /**
     * set state performer. {@linkplain LoadingFooterView.StatePerformer}
     * @param performer the target state performer.
     */
    public void setStatePerformer(LoadingFooterView.StatePerformer performer){
        mFooterView.setStatePerformer(performer);
    }

    /**
     * set the layout manager of {@linkplain RecyclerView}
     * @param lm the layout manager.
     */
    public void setLayoutManager(RecyclerView.LayoutManager lm){
        getRecyclerView().setLayoutManager(lm);
    }

    /** set adapter .it will auto add loading footer .*/
    public <T extends ISelectable> void setAdapter(QuickRecycleViewAdapter<T> adapter){
        final RecyclerView.Adapter preAdapter = getRecyclerView().getAdapter();
        if(preAdapter != null && preAdapter instanceof QuickRecycleViewAdapter){
            ((QuickRecycleViewAdapter) preAdapter).removeFooterView(mFooterView);
        }
        adapter.addFooterView(mFooterView);
        getRecyclerView().setAdapter(adapter);
    }

    /**
     * you should call this when load data done. or else the method
     * {@linkplain Callback#onLoadMore(PullToRefreshLayout)} wll not called any more.
     */
    public void setLoadingComplete(){
        mLoadMoreListenerImpl.setLoadingComplete();
        getSwipeRefreshLayout().setRefreshing(false);
    }

    /**
     * get the footer view
     * @return the footer view.
     */
    public LoadingFooterView getFooterView(){
        return mFooterView;
    }

    /**
     * set the callback of action.
     * @param mCallback the callback
     */
    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public RecyclerView getRecyclerView(){
        return mRv;
    }
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mRefreshlayout;
    }
    /**
     * get the place holder view . you can use it show loading, error or tips.
     * @return the place holder view.
     */
    public LinearLayout getPlaceHolderView(){
        return mPlaceHolderView;
    }

    /**
     * show the content of {@linkplain RecyclerView}/{@linkplain SwipeRefreshLayout}
     */
    public void showContentView(){
        mPlaceHolderView.setVisibility(View.GONE);
        mRefreshlayout.setVisibility(View.VISIBLE);
    }

    /**
     * show the place holder view .  it can show loading, error or tips.
     */
    public void showPlaceHolderView(int flag){
        mPlaceHolderView.setVisibility(View.VISIBLE);
        mRefreshlayout.setVisibility(View.GONE);
        mHolderPerformer.performPlaceHolderView(this, mPlaceHolderView, flag);
    }

    private class LoadMoreScrollListenerImpl extends AbstractLoadMoreScrollListener{
        @Override
        protected void onLoadMore(RecyclerView rv) {
             mCallback.onLoadMore(PullToRefreshLayout.this);
        }
    }

    private class OnClickFooterListenerImpl implements OnClickListener{
        @Override
        public void onClick(View v) {
            LoadingFooterView view = (LoadingFooterView) v;
            mCallback.onClickFooter(PullToRefreshLayout.this, view, view.getState());
        }
    }

}
