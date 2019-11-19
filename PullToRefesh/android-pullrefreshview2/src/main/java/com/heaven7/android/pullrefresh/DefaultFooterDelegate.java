package com.heaven7.android.pullrefresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * the default footer delegate
 * @author heaven7
 * @since 1.1.0
 */
/*public*/ class DefaultFooterDelegate implements FooterDelegate {

    private LoadingFooterView mFooterView;

    @Override
    public void prepareFooterView(ViewGroup parent){
        if(mFooterView == null){
            mFooterView = new LoadingFooterView(parent.getContext());
        }
       // mFooterView.setOnClickListener(new PullToRefreshLayout.OnClickFooterListenerImpl());
    }

    @Override
    public View getView() {
        return mFooterView;
    }
    @Override
    public void setState(int state) {
        mFooterView.setState(state);
    }
    @Override
    public int getState() {
        return mFooterView.getState();
    }
}
