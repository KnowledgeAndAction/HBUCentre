package cn.ian2018.hbu.centre.model;


import cn.ian2018.hbu.centre.fragment.BaseFragment;

/**
 * tab类
 */
public class TabItem {

    /**
     * 图标
     */
    public int imageResId;
    /**
     * 文本
     */
    public int lableResId;

    public Class<? extends BaseFragment> tagFragmentClz;

    public TabItem(int imageResId, int lableResId) {
        this.imageResId = imageResId;
        this.lableResId = lableResId;
    }

    public TabItem(int imageResId, int lableResId, Class<? extends BaseFragment> tagFragmentClz) {
        this.imageResId = imageResId;
        this.lableResId = lableResId;
        this.tagFragmentClz = tagFragmentClz;
    }
}



