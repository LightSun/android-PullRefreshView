package com.heaven7.android.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * the loading footer view
 */
public class LoadingFooterView extends RelativeLayout {

    public static final int STATE_NORMAL = 1;
    public static final int STATE_THE_EMD = 2;
    public static final int STATE_LOADING = 3;
    public static final int STATE_NET_ERROR = 4;

    private int mState = STATE_NORMAL;

    private View mLoadingView;
    private View mNetErrorView;
    private View mTheEndView;

    private StatePerformer mVsPerformer = new DefaultStatePerformer();

    /**
     * the state performer.
     */
    public interface StatePerformer {

        /**
         * called on perform ViewStub.
         * @param vs the ViewStub
         * @param state the state of footer view.
         * @return the result view of perform ViewStub.
         */
        View performViewStub(ViewStub vs, int state);
    }

    public LoadingFooterView(Context context) {
        super(context);
        init(context);
    }

    public LoadingFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        inflate(context, R.layout.footer_list, this);
        setState(STATE_NORMAL, true);
    }

    public void setStatePerformer(StatePerformer performer) {
        this.mVsPerformer = performer;
    }

    public int getState() {
        return mState;
    }

    public void setState(int status) {
        setState(status, true);
    }

    public void setState(int status, boolean showView) {
        if (mState == status) {
            return;
        }
        mState = status;

        switch (status) {

            case STATE_NORMAL:
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(GONE);
                }
                if (mTheEndView != null) {
                    mTheEndView.setVisibility(GONE);
                }
                if (mNetErrorView != null) {
                    mNetErrorView.setVisibility(GONE);
                }
                break;

            case STATE_LOADING:
                if (mTheEndView != null) {
                    mTheEndView.setVisibility(GONE);
                }
                if (mNetErrorView != null) {
                    mNetErrorView.setVisibility(GONE);
                }
                if (mLoadingView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.vs_loading);
                    mLoadingView = mVsPerformer.performViewStub(viewStub, STATE_LOADING);
                }
                mLoadingView.setVisibility(showView ? VISIBLE : GONE);
                break;

            case STATE_THE_EMD:
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(GONE);
                }
                if (mNetErrorView != null) {
                    mNetErrorView.setVisibility(GONE);
                }
                if (mTheEndView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.vs_end);
                    mTheEndView = mVsPerformer.performViewStub(viewStub, STATE_THE_EMD);
                }
                mTheEndView.setVisibility(showView ? VISIBLE : GONE);
                break;

            case STATE_NET_ERROR:
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(GONE);
                }
                if (mTheEndView != null) {
                    mTheEndView.setVisibility(GONE);
                }
                if (mNetErrorView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.vs_net_error);
                    mNetErrorView = mVsPerformer.performViewStub(viewStub, STATE_NET_ERROR);
                }
                mNetErrorView.setVisibility(showView ? VISIBLE : GONE);
                break;

            default:
                throw new RuntimeException("unknown state = " + status);
        }
    }

    public static class DefaultStatePerformer implements StatePerformer {

        private ProgressBar mLoadingProgress;
        private TextView mLoadingText;

        @Override
        public View performViewStub(ViewStub vs, int state) {

            switch (state){
                case STATE_LOADING:
                    View mLoadingView = vs.inflate();
                    mLoadingProgress = (ProgressBar) mLoadingView.findViewById(R.id.loading_progress);
                    mLoadingText = (TextView) mLoadingView.findViewById(R.id.loading_text);
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    mLoadingText.setText(R.string.list_footer_loading);
                    return mLoadingView;

                case STATE_THE_EMD:
                case STATE_NET_ERROR:
                    return vs.inflate();

                default:
                    throw new UnsupportedOperationException("unknown state = " + state);
            }
        }

    }
}
