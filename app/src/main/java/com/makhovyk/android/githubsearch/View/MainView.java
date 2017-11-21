package com.makhovyk.android.githubsearch.View;


public interface MainView {

    public void navigateToLogin();
    public void navigateToSearch();
    public void navigateToUserRepos();
    public void showUserInfo();
    public void hideUserInfo();
    public void showLoginRequiredSnackBar();
}
