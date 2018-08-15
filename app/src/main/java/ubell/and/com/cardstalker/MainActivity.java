package ubell.and.com.cardstalker;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import ubell.and.com.cardstalker.fragment.fragmentAdapter.CustomPagerAdapter;
import ubell.and.com.cardstalker.permission.PermissionCheck;


public class MainActivity extends FragmentActivity {


    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
    TabLayout mTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new PermissionCheck().permissionChecker(getApplicationContext(), MainActivity.this);
        //
        if (savedInstanceState == null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mCustomPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mTab = (TabLayout) findViewById(R.id.tabs);
            new Thread()
            {
                public void run()
                {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            mViewPager.setAdapter(mCustomPagerAdapter); //뷰페이저에 어뎁터를 연결함.
                            mTab.setTabTextColors(getResources().getColor(R.color.gray), getResources().getColor(R.color.white));
                            mTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.gray));
                            mTab.setupWithViewPager(mViewPager); //탭과 뷰페이저를 연결함(setupWith)
                        }
                    });
                }
            }.start();


        }

        //frameLayout 이라고 되어있는 레이아웃 공간에 new UsedListFragment를 대체삽입(replace)하는 것이다.
        /*getFragmentManager().beginTransaction().replace(R.id.content_fragment, new UsedListFragment()).commit();*/
    }


}
