package cn.ian2018.hbu.centre.fragment;

import java.util.HashMap;

/**
 * Created by 陈帅 on 2018/4/4/006.
 * 值班情况 Fragment工厂 用来创建Fragment
 */

public class OnDutyFragmentFactory {
    private static HashMap<Integer,BaseFragment> mBaseFragments = new HashMap<>();

    public static BaseFragment createFragment(int position) {
        BaseFragment baseFragment = mBaseFragments.get(position);

        // 如果得到的是null 就根据位置创建Fragment
        if (baseFragment == null) {
            switch (position) {
                // 值班时长
                case 0:
                    baseFragment = new OnDutyTimeFragment();
                    break;
                // 签到记录
                case 1:
                    baseFragment = new OnDutySignInfoFragment();
                    break;
            }
            mBaseFragments.put(position,baseFragment);
        }

        return baseFragment;
    }
}
