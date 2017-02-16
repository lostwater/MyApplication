package com.example.turtlejk.myapplication.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.turtlejk.myapplication.R;

public class FragmentsActivity extends android.support.v4.app.FragmentActivity implements ViewPager.OnPageChangeListener{

    //实现Tab滑动效果
    public ViewPager vpager;
    private MyFragmentPagerAdapter mAdapter;
    public static final int PAGE_STEP = 0;
    public static final int PAGE_TREE = 1;
    public Button fragmentback;

    protected void onCreate(Bundle savedInstanceState)
    {
        //修改状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置顶部状态栏颜色
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        int name = bundle.getInt("name");

        fragmentback = (Button) getWindow().findViewById(R.id.back);
        fragmentback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews(name);
    }

    private void bindViews(int name) {

        vpager = (ViewPager) findViewById(R.id.vPager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(name);
        vpager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    View.OnClickListener tostepOnClick = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            vpager.setCurrentItem(PAGE_STEP);
        }
    };

    View.OnClickListener totreeOnClick = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            vpager.setCurrentItem(PAGE_TREE);
        }
    };

}
