package com.makhovyk.android.githubsearch.Presenter;

import com.makhovyk.android.githubsearch.Model.GitHubClient;
import com.makhovyk.android.githubsearch.Model.GitHubRepo;
import com.makhovyk.android.githubsearch.Model.SearchResult;
import com.makhovyk.android.githubsearch.View.SearchFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class SearchPresenterImpl implements SearchPresenter {

    private final String BASE_URL = "https://api.github.com";
    private SearchFragment mSearchFragment;
    private GitHubClient mGitHubClient;
    private String mQuery = "";
    private int mCurrentPage = 1;
    private boolean isEverythingFetched = false;
    private int mTotalCount = 0;
    private List<GitHubRepo> mItems;

    public SearchPresenterImpl(SearchFragment searchFragment) {
        mSearchFragment = searchFragment;
        mGitHubClient = new GitHubClient(BASE_URL);
        mItems = new ArrayList<>();
    }

    @Override
    public void searchRepos(String query) {
        mGitHubClient.searchRepos(query,String.valueOf(mCurrentPage))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SearchResult searchResult) {

                        mItems.addAll(searchResult.getItems());
                        mTotalCount = searchResult.getTotalCount();

                        isEverythingFetched = mTotalCount == mItems.size();
                        if (mCurrentPage == 1) {

                            if (mItems.isEmpty()){
                                mSearchFragment.showSnackBar("No results found!");
                            }else {
                                mSearchFragment.setItems(mItems);
                            }
                        }else {
                            mSearchFragment.addNextPage(mItems);
                        }

                        mSearchFragment.hideProgressBar();

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSearchFragment.showSnackBar("Error: " + e.getMessage());
                        mSearchFragment.hideProgressBar();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onSubmitClicked(String query) {

        mSearchFragment.showProgressBar();
        mItems.clear();
        mQuery = query;
        mTotalCount = 0;
        isEverythingFetched = false;
        mCurrentPage = 1;
        searchRepos(mQuery);
    }

    @Override
    public void onBottomViewReached() {

        if (!isEverythingFetched) {
            mSearchFragment.showProgressBar();
            mCurrentPage++;
            searchRepos(mQuery);
        }
    }
}
