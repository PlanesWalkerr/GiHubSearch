package com.makhovyk.android.githubsearch.Presenter;

import com.makhovyk.android.githubsearch.Utils.SessionManager;
import com.makhovyk.android.githubsearch.View.MainView;


public class MainPresenterImpl implements MainPresenter {

    private MainView mMainView;
    private SessionManager mSessionManager;


    public MainPresenterImpl(MainView mainView, SessionManager sessionManager) {
        mMainView = mainView;
        mSessionManager = sessionManager;
    }

    @Override
    public void onSearchReposClicked() {
        if (mMainView != null) {
            mMainView.navigateToSearch();
        }

    }

    @Override
    public void onShowUserReposClicked() {
        if (mMainView != null) {
            if (mSessionManager.isLoggedIn()){
                mMainView.navigateToUserRepos();
            }else {
                mMainView.navigateToLogin();
                mMainView.showLoginRequiredSnackBar();
            }

        }
    }

    @Override
    public void onLoginClicked() {
        if (mMainView != null) {
            mMainView.navigateToLogin();
        }
    }

    @Override
    public void onLogOutClicked() {

        mSessionManager.logOut();
        mMainView.navigateToLogin();
        mMainView.hideUserInfo();
    }
}
