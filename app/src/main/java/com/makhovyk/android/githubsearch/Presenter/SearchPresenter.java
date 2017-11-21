package com.makhovyk.android.githubsearch.Presenter;

public interface SearchPresenter {
    public void searchRepos(String query);
    public void onSubmitClicked(String query);
    public void onBottomViewReached();
}
