package com.jyt.baseapp.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jyt.baseapp.R;
import com.jyt.baseapp.adapter.FragmentViewPagerAdapter;
import com.jyt.baseapp.annotation.ActivityAnnotation;
import com.jyt.baseapp.api.BeanCallback;
import com.jyt.baseapp.bean.BaseJson;
import com.jyt.baseapp.bean.json.Banner;
import com.jyt.baseapp.helper.IntentHelper;
import com.jyt.baseapp.helper.IntentKey;
import com.jyt.baseapp.model.BaseModel;
import com.jyt.baseapp.model.MainActModel;
import com.jyt.baseapp.model.impl.MainActModelImpl;
import com.jyt.baseapp.util.ImageLoader;
import com.jyt.baseapp.util.T;
import com.jyt.baseapp.view.fragment.RoomListFragment;
import com.jyt.baseapp.view.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Created by chenweiqi on 2017/11/6.
 */
@ActivityAnnotation(showActionBar = false)
public class MainActivity extends BaseActivity {
    @BindView(R.id.img_setting)
    ImageView imgSetting;
    @BindView(R.id.img_personalCenter)
    ImageView imgPersonalCenter;
    @BindView(R.id.v_banner)
    BGABanner vBanner;
    @BindView(R.id.v_tabLayout)
    TabLayout vTabLayout;
    @BindView(R.id.v_viewPager)
    NoScrollViewPager vViewPager;
    @BindView(R.id.img_showHideBanner)
    ImageView imgShowHideBanner;
    @BindView(R.id.img_gifppp)
    ImageView imgGifppp;
    private RoomListFragment dollFragment;
    private RoomListFragment cabbageFragment;

    FragmentViewPagerAdapter adapter;

    MainActModel model;

    Banner banners;

    View.OnClickListener bannerImgClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected View getContentView() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Glide.with(this).load(R.mipmap.start).asGif().into(imgGifppp);

        dollFragment = new RoomListFragment();
        Bundle dollFragmentBundle = new Bundle();
        dollFragmentBundle.putInt(IntentKey.KEY_TYPE, 0);
        dollFragment.setArguments(dollFragmentBundle);

        cabbageFragment = new RoomListFragment();
        Bundle cabbageFragmentBundle = new Bundle();
        cabbageFragmentBundle.putInt(IntentKey.KEY_TYPE, 1);
        cabbageFragment.setArguments(cabbageFragmentBundle);

        vViewPager.setAdapter(adapter = new FragmentViewPagerAdapter(getSupportFragmentManager()));
        adapter.addFragment(dollFragment, "娃娃首页");
        adapter.addFragment(cabbageFragment, "白菜特抓");
        adapter.addFragment(new Fragment(), "玩家分享");
        adapter.notifyDataSetChanged();
        vTabLayout.setupWithViewPager(vViewPager);

        vBanner.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner bgaBanner, View view, Object o, int i) {


//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Uri content_url = Uri.parse(banners.getSlide().get(i).getLink());
//                intent.setData(content_url);
//                startActivity(intent);


            }
        });
        model.getBanner(new BeanCallback<BaseJson<Banner>>() {
            @Override
            public void response(boolean success, BaseJson<Banner> response, int id) {
                if (response.isRet()) {
                    banners = response.getData();

                    List<View> imgs = new ArrayList<View>();
                    for (int i = 0; i < response.getData().getSlide().size(); i++) {
                        ImageView imageView = new ImageView(getContext());
                        ImageLoader.getInstance().loadRectangle(imageView, banners.getSlide().get(i).getImg());
                        imgs.add(imageView);
                    }
                    vBanner.setData(imgs);
                }
                T.showShort(getContext(), response.getForUser());
            }
        });
    }

    @Override
    public List<BaseModel> createModels() {
        List models = new ArrayList();
        model = new MainActModelImpl();
        models.add(model);
        return models;
    }

    @OnClick(R.id.img_setting)
    public void onSettingClick() {
        IntentHelper.openPersonalActivity(getContext());
    }

    @OnClick(R.id.img_showHideBanner)
    public void onShowHideBannerClick() {
        if (vBanner.getVisibility() == View.VISIBLE) {
            imgShowHideBanner.setImageDrawable(getResources().getDrawable(R.mipmap.arrow_up_pink));
            vBanner.setVisibility(View.GONE);
        } else {
            imgShowHideBanner.setImageDrawable(getResources().getDrawable(R.mipmap.arrow_down_pink));
            vBanner.setVisibility(View.VISIBLE);
        }
    }
}
