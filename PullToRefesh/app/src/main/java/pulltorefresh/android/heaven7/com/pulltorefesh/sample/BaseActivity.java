package pulltorefresh.android.heaven7.com.pulltorefesh.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.heaven7.core.util.Toaster;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public abstract class BaseActivity extends AppCompatActivity implements AppComponentContext {


    private Toaster mToaster;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToaster = new Toaster(this, Gravity.CENTER);
        onPreSetContentView();

        setContentView(getLayoutId());
        ButterKnife.bind(this);
        onInitialize(this, savedInstanceState);
    }

    @Override
    public void onPreSetContentView() {

    }

    @Override
    public Toaster getToaster() {
        return mToaster;
    }

}
