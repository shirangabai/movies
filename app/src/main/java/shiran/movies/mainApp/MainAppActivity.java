package shiran.movies.mainApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;

import shiran.movies.MyCallback;
import shiran.movies.R;
import shiran.movies.U;
import shiran.movies.adapter.FavoriteGridAdapter;
import shiran.movies.adapter.PagerAdapter;
import shiran.movies.mainApp.data.FavoriteDB;
import shiran.movies.mainApp.fragments.FavoriteFragment;
import shiran.movies.mainApp.fragments.ForumFragment;
import shiran.movies.mainApp.fragments.SearchFragment;

public class MainAppActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private MenuItem actionBarItems[] = new MenuItem[5];

    private int[] tabIcons = {  // tabs icons
            android.R.drawable.star_on,
            android.R.drawable.ic_dialog_dialer,
            android.R.drawable.ic_menu_set_as
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        mAuth = FirebaseAuth.getInstance();

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        //bar.setLogo(R.drawable.film3);
        //bar.setDisplayUseLogoEnabled(true);
        //bar.setDisplayShowHomeEnabled(true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int page) {
                boolean actionBarItemState[][] = new boolean[][]{
                        {true, false, false, true, true},
                        {false, true, false, false, false},
                        {false, false, true, false, false}
                };

                for (int i = 0; i < actionBarItemState[0].length; i++) {
                    try {
                        actionBarItems[i].setVisible(actionBarItemState[page][i]);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (page == 0) favoriteFragment.setAdapter();
                U.hideKeyboard(MainAppActivity.this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("test", "onPageScrollStateChanged: " + state);
            }
        });

        FavoriteDB.inctance.setDBChangeListener(new MainAppActivity.DBChangeListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    FavoriteFragment favoriteFragment = new FavoriteFragment();
    SearchFragment searchFragment = new SearchFragment();
    ForumFragment forumFragment = new ForumFragment();

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        adapter.addFragment(favoriteFragment, getString(R.string.favorite));
        adapter.addFragment(searchFragment, getString(R.string.search));
        adapter.addFragment(forumFragment, getString(R.string.chat));
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        //tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        //tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        //tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        actionBarItems[0] = menu.findItem(R.id.action_favorite);
        actionBarItems[1] = menu.findItem(R.id.action_search);
        actionBarItems[2] = menu.findItem(R.id.action_Reminder_movies);
        actionBarItems[3] = menu.findItem(R.id.action_delete);
        actionBarItems[4] = menu.findItem(R.id.action_backup);
        final SearchView searchView = (SearchView) actionBarItems[1].getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFragment.searchMovies(query);
                U.clearFocus(MainAppActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
                U.saveBoolPreferences(this, U.PREFS_AUTO_EMAIL, false);
                U.savePreferences(this, U.PREFS_AUTO_EMAIL, null);
                U.savePreferences(this, U.PREFS_AUTO_PASS, null);
                finish();
                break;
            case R.id.action_import:
                favoriteFragment.onClickImport();
                break;
            case R.id.action_export:
                favoriteFragment.onClickExport();
                break;
            case R.id.action_delete:
                favoriteFragment.onClickDeleteAll();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class DBChangeListener implements MyCallback {
        public DBChangeListener() {
        }

        @Override
        public void onBack(Object o) {
            if (o instanceof FavoriteGridAdapter) searchFragment.setAdapter();
            //favoriteFragment.setAdapter();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        favoriteFragment.onDestroy();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);

        finish();
        System.exit(0);
    }
}
