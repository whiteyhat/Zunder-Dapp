package zunder.ebs.zunderapp.Map;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class CompanyProfile extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(zunder.ebs.zunderapp.R.layout.activity_company_profile);

        // Enable displayaction bar
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //set up viee pager
        viewPager = (ViewPager) findViewById(zunder.ebs.zunderapp.R.id.viewpager);
        setupViewPager(viewPager);

        //set yp layout tab
        tabLayout = (TabLayout) findViewById(zunder.ebs.zunderapp.R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    /**
     * method to create the layout of view
     * @param viewPager is the page viewer
     */
    private void setupViewPager(ViewPager viewPager) {
      //set tabs for tab layout
       ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new InfoCompanyFragment(), "Info");
        adapter.addFragment(new PortfolioFragment(), "Portfolio");
        viewPager.setAdapter(adapter);
    }

    /**
     * Class to create a dynamic layout based on tabs and fragments
     */
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
