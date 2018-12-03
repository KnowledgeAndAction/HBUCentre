package cn.ian2018.hbu.centre.fragment;

import java.util.HashMap;

/**
 * Created by 陈帅 on 2018/3/6/006.
 * 排名情况 Fragment工厂 用来创建Fragment
 */

public class RankFragmentFactory {
    private static HashMap<Integer,BaseFragment> mBaseFragments = new HashMap<>();

    public static BaseFragment createFragment(int position) {
        BaseFragment baseFragment = mBaseFragments.get(position);

        // 如果得到的是null 就根据位置创建Fragment
        if (baseFragment == null) {
            switch (position) {
                // 组内排名
                case 0:
                    baseFragment = new GroupRankFragment();
                    break;
                // 中心排名
                case 1:
                    baseFragment = new CentreRankFragment();
                    break;
                // 组排名
                case 2:
                    baseFragment = new CentreGroupRankFragment();
                    break;
            }
            mBaseFragments.put(position,baseFragment);
        }

        return baseFragment;
    }
}
