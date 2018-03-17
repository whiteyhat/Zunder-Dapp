package zunder.ebs.zunderapp.Map;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private final int location1 = 104;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(zunder.ebs.zunderapp.R.layout.activity_map);

        // set up display action bar
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // set up page viewer
        viewPager = (ViewPager) findViewById(zunder.ebs.zunderapp.R.id.viewpager);
        setupViewPager(viewPager);

        // set up tab layout
        tabLayout = (TabLayout) findViewById(zunder.ebs.zunderapp.R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    /**
     * method to create the layout of view
     * @param viewPager is the page viewer
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapFragment(), "Map");
        adapter.addFragment(new StoreFragment(), "List");
        adapter.addFragment(new FilterFragment(), "Filter");
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