package com.jyt.baseapp.view.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyt.baseapp.R;
import com.jyt.baseapp.api.Api;
import com.jyt.baseapp.util.T;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by v7 on 2017/11/9.
 */

public class VersionInfoActivity extends BaseActivity {


    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.tv_url)
    TextView mTvUrl;
    @BindView(R.id.rl_evaluation)
    RelativeLayout rlEvaluation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_version_info;
    }

    @Override
    protected View getContentView() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getContext().getPackageName(), 0);
            mTvVersion.setText("PPP抓娃娃 " + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mTvUrl.setText(Api.domainText);

    }

    @OnClick(R.id.rl_evaluation)
    public void setStart() {
        T.showShort(getContext(),"敬请期待");
    }
}
