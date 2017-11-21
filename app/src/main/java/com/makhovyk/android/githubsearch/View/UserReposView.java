package com.makhovyk.android.githubsearch.View;

import com.makhovyk.android.githubsearch.Model.GitHubRepo;

import java.util.List;

public interface UserReposView {

    public void showProgressBar();
    public void hideProgressBar();
    public void showSnackBar(String message);
    public void setItems(List<GitHubRepo> items);
    public void addNextPage(List<GitHubRepo> items);
}
