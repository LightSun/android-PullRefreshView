package com.heaven7.android.pullrefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.heaven7.adapter.AbstractLoadMoreScrollListener;
import com.heaven7.adapter.AdapterManager;


/**
 * the pull to refresh layout.
 * Created by heaven7 on 2017/5/17 0017.
 */

public class PullToRefreshLayout extends FrameLayout {

    private SwipeRefreshLayout mRefreshlayout;
    private RecyclerView mRv;
    /** a place holder view that can show loading, error,tips . which is overlap SwipeRefreshLayout. */
    private LinearLayout mPlaceHolderView;
    /** a view which can overlap content(RecyclerView). */
    private FrameLayout mContentOverlapView;

    private LoadMoreScrollListenerImpl mLoadMoreListenerImpl;

    private Callback mCallback;
    private PlaceHolderViewPerformer mHolderPerformer;

    private FooterDelegate mFooterDelegate = new DefaultFooterDelegate();
    private StatePerformDelegate mStatePerformer;
    private int mState = -1;

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
         * @param footer the footer view
         * @param state the state of footer
         */
        @Deprecated
        public void onClickFooter(PullToRefreshLayout layout, LoadingFooterView footer, int state){
            onClickFooter(layout, (View)footer, state);
        }

        /**
         * called on click footer
         * @param layout the layout
         * @param footer the footer view
         * @param state the state. see {@linkplain FooterDelegate#STATE_LOADING} and etc.
         * @since 1.1.0
         */
        public void onClickFooter(PullToRefreshLayout layout, View footer, int state){

        }
    }

    /**
     * <p>Use {@linkplain StatePerformDelegate} with {@linkplain PullToRefreshLayout#setState(int)} instead.</p>
     * the place holder performer.
     */
    @Deprecated
    public interface PlaceHolderViewPerformer{
        /**
         * called on perform the place holder view/
         * @param layout the layout
         * @param placeHolderView the place holder view.
         * @param flag the code which comes from {@linkplain PullToRefreshLayout#showPlaceHolderView(int)}
         */
        void performPlaceHolderView(PullToRefreshLayout layout, LinearLayout placeHolderView, int flag);
    }

    /**
     * the state perform delegate
     * @since 1.1.2
     */
    public interface StatePerformDelegate{
        /**
         * called on perform state
         * @param layout the layout
         * @param preState the previous state.
         * @param state the current state.
         */
        void performState(PullToRefreshLayout layout, int preState,int state);
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
    /**
     * init the refresh layout
     * @param context the context
     * @param attrs the attrs
     */
    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.lib_ptr_widget_refresh_view, this);
        mRefreshlayout = findViewById(R.id.refresh_layout);
        mRv = findViewById(R.id.rv);
        mPlaceHolderView = findViewById(R.id.vg_out);
        mContentOverlapView = findViewById(R.id.vg_in);

        mContentOverlapView.setVisibility(GONE);
        mPlaceHolderView.setVisibility(GONE);
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
    }

    /**
     * set state perform delegate
     * @param delegate the delegate
     * @since 1.1.2
     */
    public void setStatePerformDelegate(StatePerformDelegate delegate){
        this.mStatePerformer = delegate;
    }
    /**
     * set the footer delegate
     * @param delegate the footer delegate
     * @since 1.1.0
     */
    public void setFooterDelegate(FooterDelegate delegate){
        mFooterDelegate = delegate;
    }

    /**
     * get the footer delegate
     * @return the footer delegate
     * @since 1.1.0
     */
    public FooterDelegate getFooterDelegate(){
        return mFooterDelegate;
    }
    /**
     * set the place holder view performer
     * @param performer the performer.
     */
    @Deprecated
    public void setPlaceHolderViewPerformer(PlaceHolderViewPerformer performer) {
        this.mHolderPerformer = performer;
    }

    /**
     * <p>Use {@linkplain #setStatePerformDelegate(StatePerformDelegate)} instead. </p>
     * set state performer
     * @param performer the target state performer.
     */
    @Deprecated
    public void setStatePerformer(LoadingFooterView.StatePerformer performer){
        mFooterDelegate.prepareFooterView(getRecyclerView());
        View view = mFooterDelegate.getView();
        if(view instanceof LoadingFooterView){
            ((LoadingFooterView)view).setStatePerformer(performer);
        }
    }

    /**
     * set the layout manager of {@linkplain RecyclerView}
     * @param lm the layout manager.
     */
    public void setLayoutManager(RecyclerView.LayoutManager lm){
        getRecyclerView().setLayoutManager(lm);
    }

    /** set adapter .it will auto add loading footer .
     * @param adapter the adapter to bind
     * */
    public void setAdapter(RecyclerView.Adapter adapter){
        mFooterDelegate.prepareFooterView(getRecyclerView());
        View view = mFooterDelegate.getView();
        view.setOnClickListener(new OnClickFooterListenerImpl());

        final RecyclerView.Adapter preAdapter = getRecyclerView().getAdapter();
        if(preAdapter instanceof AdapterManager.IHeaderFooterManager){
            ((AdapterManager.IHeaderFooterManager) preAdapter).removeFooterView(view);
        }
        if(adapter instanceof AdapterManager.IHeaderFooterManager){
            ((AdapterManager.IHeaderFooterManager) adapter).addFooterView(view);
        }
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
    @Deprecated
    public LoadingFooterView getFooterView(){
        mFooterDelegate.prepareFooterView(getRecyclerView());
        View view = mFooterDelegate.getView();
        return view instanceof LoadingFooterView ? (LoadingFooterView) view : null;
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
     * <p>Use {@linkplain #getWholeOverlapView()} instead. </p>
     * get the place holder view . you can use it show loading, error or tips.
     * @return the place holder view.
     */
    @Deprecated
    public LinearLayout getPlaceHolderView(){
        return mPlaceHolderView;
    }
    /**
     * get the whole overlap view . you can use it show loading, error or tips.
     * @return the whole overlap view.
     * @since 1.1.2
     */
    public ViewGroup getWholeOverlapView(){
        return mPlaceHolderView;
    }
    /**
     * get the content overlap view . you can use it show loading, error or tips.
     * @return the content overlap view.
     * @since 1.1.2
     */
    public ViewGroup getContentOverlapView(){
        return mContentOverlapView;
    }

    /**
     * set any state as you want.
     * @param state the state.
     * @since 1.1.2
     */
    public void setState(int state){
        setState(state, false);
    }
    /**
     * set any state as you want.
     * @param state the state.
     * @param force true to force refresh state
     * @since 1.1.2
     */
    public void setState(int state, boolean force){
        if(force || this.mState != state){
            int old = this.mState;
            this.mState = state;
            mStatePerformer.performState(this, old, state);
        }
    }
    /**
     * get current state. if you not call {@linkplain #setState(int)}. default is -1.
     * @return the state
     */
    public int getState(){
        return mState;
    }
    /**
     * show the content of {@linkplain RecyclerView}/{@linkplain SwipeRefreshLayout}
     */
    //@Deprecated
    public void showContentView(){
        mPlaceHolderView.setVisibility(View.GONE);
        mRefreshlayout.setVisibility(View.VISIBLE);
    }

    /**
     * show the place holder view .  it can show loading, error or tips.
     * @param flag the flag to show place holder.
     */
    //@Deprecated
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
            if(v instanceof LoadingFooterView){
                LoadingFooterView view = (LoadingFooterView) v;
                mCallback.onClickFooter(PullToRefreshLayout.this, view, view.getState());
            }else {
                mCallback.onClickFooter(PullToRefreshLayout.this, v, mFooterDelegate.getState());
            }
        }
    }

}
