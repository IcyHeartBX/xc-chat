package com.pix.xcandroid.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pix.xcandroid.R;
import com.pix.xcandroid.utils.Utils;

/**
 *
 * @author TPX
 * @version 1.0.0
 * @modify
 */
public class LevelLayout extends FrameLayout {
    private RelativeLayout mBGLayout ;
    private TextView tvLevel;
    private final ImageView ivLevelIcon;
    private final View layoutLevel;


    public LevelLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.li_layout_level,this);
        layoutLevel = findViewById(R.id.layout_level);
        ivLevelIcon = (ImageView) findViewById(R.id.iv_level_icon);
        tvLevel = (TextView) findViewById(R.id.tv_level);
    }
    public LevelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.li_layout_level,this);
        layoutLevel = findViewById(R.id.layout_level);
        ivLevelIcon = (ImageView) findViewById(R.id.iv_level_icon);
        tvLevel = (TextView) findViewById(R.id.tv_level);
    }
    public void setLevel(int level) {
        if(level < 1 ) {
            level = 1;
        }
        tvLevel.setText(String.valueOf(level ));
        if (level >= 100) {
            layoutLevel.setBackgroundResource(R.drawable.user_level_bg_level_5);
            ivLevelIcon.setImageResource(R.drawable.user_level_icon_5);
            layoutLevel.setPadding(Utils.Companion.dip2px(getContext(), 1), 0, Utils.Companion.dip2px(getContext(), 1), 0);
        }else if (level > 80) {
            layoutLevel.setBackgroundResource(R.drawable.user_level_bg_level_5);
            ivLevelIcon.setImageResource(R.drawable.user_level_icon_5);
            layoutLevel.setPadding(Utils.Companion.dip2px(getContext(), 2), 0, Utils.Companion.dip2px(getContext(), 2), 0);
        } else if (level > 60) {
            layoutLevel.setBackgroundResource(R.drawable.user_level_bg_level_4);
            ivLevelIcon.setImageResource(R.drawable.user_level_icon_4);
            layoutLevel.setPadding(Utils.Companion.dip2px(getContext(), 2), 0, Utils.Companion.dip2px(getContext(), 2), 0);
        } else if (level > 40) {
            layoutLevel.setBackgroundResource(R.drawable.user_level_bg_level_3);
            ivLevelIcon.setImageResource(R.drawable.user_level_icon_3);
            layoutLevel.setPadding(Utils.Companion.dip2px(getContext(), 2), 0, Utils.Companion.dip2px(getContext(), 2), 0);
        } else if (level > 20) {
            layoutLevel.setBackgroundResource(R.drawable.user_level_bg_level_2);
            ivLevelIcon.setImageResource(R.drawable.user_level_icon_2);
            layoutLevel.setPadding(Utils.Companion.dip2px(getContext(), 2), 0, Utils.Companion.dip2px(getContext(), 2), 0);
        } else {
            layoutLevel.setBackgroundResource(R.drawable.user_level_bg_level_1);
            ivLevelIcon.setImageResource(R.drawable.user_level_icon_1);
            layoutLevel.setPadding(Utils.Companion.dip2px(getContext(), 2), 0, Utils.Companion.dip2px(getContext(), 2), 0);
        }
    }

}
