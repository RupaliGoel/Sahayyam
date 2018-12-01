package com.example.rupali.sos;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class PostHistory extends AppCompatActivity {

  //  private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public String email;

    String toolbarMessage;
    Toolbar toolbar;
    TextView appnametv;


    EmergencyHistory emergencyHistory = new EmergencyHistory();
    AppealHistory appealHistory = new AppealHistory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_history);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");


        /*Bundle bundle2 = new Bundle();
        String myMessage = email;
        bundle2.putString("email", myMessage );

        emergencyHistory.setArguments(bundle2);*/
        emergencyHistory.setEmail(email);
        appealHistory.setEmail(email);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        toolbarMessage = prefs.getString("AppName","App");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appnametv = (TextView)findViewById(R.id.appname);
        appnametv.setText(toolbarMessage);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(emergencyHistory , "Emergency");
        adapter.addFragment(appealHistory, "Appeal");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}