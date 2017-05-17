package pulltorefresh.android.heaven7.com.pulltorefesh;

import java.util.List;

import pulltorefresh.android.heaven7.com.pulltorefesh.sample.PullToRefreshTestActivity;

/**
 * this class help we test ui.
 * Created by heaven7 on 2017/3/24 0024.
 */

public class TestMainActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(PullToRefreshTestActivity.class, "Test pull refresh"));
    }
}

