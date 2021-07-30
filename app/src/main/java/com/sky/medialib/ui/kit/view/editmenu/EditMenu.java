package com.sky.medialib.ui.kit.view.editmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sky.medialib.R;
import com.sky.medialib.ui.kit.common.view.ViewPagerIndicator;

public class EditMenu extends LinearLayout {

    ViewPager mViewPager;
    ViewPagerIndicator pagerIndicator;

    private int itemCount = 1;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(EditMenuItem editMenuItem);
    }

    public EditMenu(Context context) {
        super(context);
        init();
    }

    public EditMenu(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public EditMenu(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_edit_menu, this, true);
        this.mViewPager = (ViewPager) findViewById(R.id.edit_menu_pager);
        this.pagerIndicator = (ViewPagerIndicator) findViewById(R.id.edit_menu_indicator);
        this.itemCount = (int) Math.ceil((((double) (EditMenuItem.values().length - 1)) * 1.0d) / 5.0d);
        this.pagerIndicator.setCount(itemCount, true);
        this.mViewPager.setAdapter(new MenuPagerAdapter());
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerIndicator.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class MenuPagerAdapter extends PagerAdapter {
        private SparseArray<View> mViews;

        private MenuPagerAdapter() {
            this.mViews = new SparseArray();
        }


        public int getCount() {
            return EditMenu.this.itemCount;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View view = (View) this.mViews.get(i);
            if (view == null) {
                EditMenuItem[] values = EditMenuItem.values();
                EditMenuItem[] editMenuItemArr = new EditMenuItem[5];
                int i2 = (i * 5) + 1;
                int i3 = 0;
                while (i2 < values.length && i3 < 5) {
                    editMenuItemArr[i3] = values[i2];
                    i2++;
                    i3++;
                }
                view = new EditMenuItemView(EditMenu.this.getContext(), editMenuItemArr);
                this.mViews.append(i, view);
            }
            viewGroup.addView(view);
            return view;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) this.mViews.get(i));
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
    }

    public class EditMenuItemView extends LinearLayout {
        public EditMenuItemView(Context context, EditMenuItem[] editMenuItemArr) {
            super(context);
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            for (final EditMenuItem editMenuItem : editMenuItemArr) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_edit_menu, this, false);
                if (editMenuItem != null) {
                    ((ImageView) view.findViewById(R.id.item_edit_menu_icon)).setImageResource(editMenuItem.resId);
                    ((TextView) view.findViewById(R.id.item_edit_menu_text)).setText(editMenuItem.stringId);
                    view.setOnClickListener(v -> {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(editMenuItem);
                        }
                    });
                }
                addView(view);
            }
        }
    }


}
