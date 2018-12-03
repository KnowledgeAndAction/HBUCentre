package cn.ian2018.hbu.centre.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hicc.information.sensorsignin.R;

import cn.ian2018.hbu.centre.fragment.BaseFragment;
import cn.ian2018.hbu.centre.fragment.RankFragmentFactory;


public class RankManageActivity extends AppCompatActivity {

    private TabLayout mTab;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_manage);

        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("排名情况");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTab = (TabLayout) findViewById(R.id.tab);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // 设置viewpager适配器
        ShortPagerAdapter adapter = new ShortPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        // 绑定tablayout
        mTab.setupWithViewPager(mViewPager);
    }

    private class ShortPagerAdapter extends FragmentPagerAdapter {
        public String[] mTilte = {"各组排名","中心排名","组间排名"};

        public ShortPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTilte[position];
        }

        @Override
        public BaseFragment getItem(int position) {
            BaseFragment fragment = RankFragmentFactory.createFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return mTilte.length;
        }
    }
}
