package com.zhuoxin.videonews.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.zhuoxin.videonews.R;
import com.zhuoxin.videonews.ui.likes.LikesFragment;
import com.zhuoxin.videonews.ui.local.LocalFragment;
import com.zhuoxin.videonews.ui.news.NewsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.btnLikes)
    Button btnLikes;
    @BindView(R.id.btnLocal)
    Button btnLocal;
    @BindView(R.id.btnNews)
    Button btnNews;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    //初始化视图
    private void initView() {
        unbinder = ButterKnife.bind(this);
        viewPager.setAdapter(adapter);
        //viewPager监听->button的切换
        viewPager.addOnPageChangeListener(listener);
        btnNews.setSelected(true);
    }

    //viewPager适配器
    private FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //跳转到在线视频
                    return new NewsFragment();
                case 1:
                    //跳转到本地视频
                    return new LocalFragment();
                case 2:
                    //跳转到我的收藏
                    return new LikesFragment();
                default:
                    throw new RuntimeException("未知错误");
            }
        }

        @Override
        public int getCount() {
            //一共只有3页
            return 3;
        }
    };
    //viewPager监听->button的切换
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        //当页面选中时
        @Override
        public void onPageSelected(int position) {
            //当button改变,ui改变
            btnNews.setSelected(position == 0);
            btnLocal.setSelected(position == 1);
            btnLikes.setSelected(position == 2);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    //button的点击事件
    @OnClick({R.id.btnNews, R.id.btnLocal, R.id.btnLikes})
    public void chooseFragment(Button button) {
        switch (button.getId()) {
            case R.id.btnNews:
                //不要平滑效果,第二个参数传false
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.btnLocal:
                //不要平滑效果,第二个参数传false
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.btnLikes:
                //不要平滑效果,第二个参数传false
                viewPager.setCurrentItem(2, false);
                break;
            default:
                throw new RuntimeException("未知错误");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

