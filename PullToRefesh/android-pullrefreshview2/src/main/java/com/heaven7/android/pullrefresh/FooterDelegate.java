package com.heaven7.android.pullrefresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * the footer delegate .
 * @author heaven7
 * @since 1.1.0
 */
public interface FooterDelegate {

    int STATE_NORMAL = 1;
    int STATE_THE_END = 2;
    int STATE_LOADING = 3;
    int STATE_NET_ERROR = 4;

    /**
     * called on prepare footer view
     * @param parent the parent View
     */
    void prepareFooterView(ViewGroup parent);

    /**
     * the footer view.
     * @return the footer view
     */
    View getView();

    /**
     * set the footer state
     * @param state the target state
     */
    void setState(int state);

    /**
     * get the footer state
     * @return the state.
     */
    int getState();
}
