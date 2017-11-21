package com.makhovyk.android.githubsearch.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makhovyk.android.githubsearch.Presenter.MainPresenterImpl;
import com.makhovyk.android.githubsearch.R;
import com.makhovyk.android.githubsearch.Utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainView,
        LoginFragment.OnUserDataUpdated{

    private static final String FRAGMENT_TAG = "SavedFragment";

    private SearchFragment mSearchFragment;
    private UserReposFragment mUserReposFragment;
    private LoginFragment mLoginFragment;
    private MainPresenterImpl presenter;
    private SessionManager mSessionManager;
    private FragmentManager mFragmentManager;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private View mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSessionManager = new SessionManager(getApplicationContext());
        mFragmentManager = getSupportFragmentManager();
        mSearchFragment = new SearchFragment();
        mLoginFragment = new LoginFragment();
        mUserReposFragment = new UserReposFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mHeader = mNavigationView.getHeaderView(0);

        LinearLayout userInfoLayout = mHeader.findViewById(R.id.login_layout);
        userInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onLoginClicked();
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });

        presenter = new MainPresenterImpl(this, mSessionManager);
        if (savedInstanceState != null) {
            //restoring current fragment
            Fragment fragment = mFragmentManager.getFragment(savedInstanceState, FRAGMENT_TAG);
            mFragmentManager.beginTransaction()
                    .replace(R.id.main_container,fragment)
                    .commit();
        }else {
            if (mSessionManager.isLoggedIn()) {
                navigateToUserRepos();
                mNavigationView.setCheckedItem(R.id.your_repos);
            } else {
                navigateToLogin();
            }
        }

        toggleUserInfo();

    }

    private void unCheckAllMenuItems() {
        mNavigationView.getMenu().findItem(R.id.your_repos).setChecked(false);
        mNavigationView.getMenu().findItem(R.id.search_repos).setChecked(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //saving current fragment
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState,FRAGMENT_TAG,getVisibleFragment());
        getVisibleFragment();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.your_repos) {
            presenter.onShowUserReposClicked();
        } else if (id == R.id.search_repos) {
            presenter.onSearchReposClicked();
        } else if (id == R.id.log_out) {
            presenter.onLogOutClicked();
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void navigateToLogin() {
        unCheckAllMenuItems();
        mFragmentManager.beginTransaction()
                .replace(R.id.main_container, mLoginFragment)
                .commit();
    }

    @Override
    public void navigateToSearch() {
        mFragmentManager.beginTransaction()
                .replace(R.id.main_container, mSearchFragment)
                .commit();
    }

    @Override
    public void navigateToUserRepos() {
        mNavigationView.getMenu().findItem(R.id.your_repos).setChecked(true);
        mFragmentManager.beginTransaction()
                .replace(R.id.main_container, mUserReposFragment)
                .commit();

    }

    @Override
    public void showUserInfo() {
        mHeader.findViewById(R.id.user_info_layout).setVisibility(LinearLayout.VISIBLE);
        mHeader.findViewById(R.id.login_layout).setVisibility(LinearLayout.GONE);
        Menu menuNav = mNavigationView.getMenu();
        menuNav.findItem(R.id.log_out).setVisible(true);

        TextView userLogin = mHeader.findViewById(R.id.user_login);
        userLogin.setText(getResources().getString(R.string.hello_user,mSessionManager.getName()));
        TextView userEmail = mHeader.findViewById(R.id.user_email);
        userEmail.setText(mSessionManager.getEmail());
        ImageView userLogo = mHeader.findViewById(R.id.user_logo);
        Picasso.with(this).load(mSessionManager.getPhoto()).into(userLogo);
    }

    @Override
    public void hideUserInfo() {
        mHeader.findViewById(R.id.user_info_layout).setVisibility(LinearLayout.GONE);
        mHeader.findViewById(R.id.login_layout).setVisibility(LinearLayout.VISIBLE);

        Menu menuNav = mNavigationView.getMenu();
        menuNav.findItem(R.id.log_out).setVisible(false);

    }

    @Override
    public void showLoginRequiredSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinator_layout),
                        "You need to be logged in to see your repos!",Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    // show user info after login
    @Override
    public void onUserFetched() {
        showUserInfo();
        navigateToUserRepos();
    }

    public void toggleUserInfo(){
        if (mSessionManager.isLoggedIn()){
            showUserInfo();
        }else {
            hideUserInfo();
        }
    }

    //getting current visible fragment
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }
}
