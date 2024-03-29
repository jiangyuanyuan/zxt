package com.tezwez.base.view;

import android.content.Context;

import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class NoAnimationViewPager extends ViewPager {
    public NoAnimationViewPager(Context context) {
        super(context);
    }

    public NoAnimationViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        //去除页面切换时的滑动翻页效果
        super.setCurrentItem(item, false);
    }

    @Override
    public void addOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        super.addOnPageChangeListener(listener);
    }

}
