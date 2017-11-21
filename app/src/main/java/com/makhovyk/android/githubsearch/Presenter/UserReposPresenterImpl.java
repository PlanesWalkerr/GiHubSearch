package com.makhovyk.android.githubsearch.Presenter;

import com.makhovyk.android.githubsearch.Model.GitHubClient;
import com.makhovyk.android.githubsearch.Model.GitHubRepo;
import com.makhovyk.android.githubsearch.Utils.SessionManager;
import com.makhovyk.android.githubsearch.View.UserReposFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


public class UserReposPresenterImpl implements UserReposPresenter {

    private final String BASE_URL = "https://api.github.com";
    private SessionManager mSessionManager;
    private UserReposFragment mUserReposFragment;
    private GitHubClient mGitHubClient;

    private int mCurrentPage = 1;
    private boolean isEverythingFetched = false;
    private List<GitHubRepo> mItems = new ArrayList<>();

    public UserReposPresenterImpl(UserReposFragment userReposFragment, SessionManager sessionManager) {
        mSessionManager = sessionManager;
        mUserReposFragment = userReposFragment;
        mGitHubClient = new GitHubClient(BASE_URL);
    }

    @Override
    public void onBottomViewReached() {
        if (!isEverythingFetched) {
            mUserReposFragment.showProgressBar();
            fetchUserRepos();
        }
    }

    @Override
    public void getUserRepos() {
        mUserReposFragment.showProgressBar();
        mItems.clear();
        isEverythingFetched = false;
        mCurrentPage = 1;
        fetchUserRepos();
    }

    private void fetchUserRepos() {
        mGitHubClient.getUserReposResponse(mSessionManager.getAccessToken(),
                String.valueOf(mCurrentPage))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<List<GitHubRepo>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<List<GitHubRepo>> listResponse) {

                        mItems.addAll(listResponse.body());
                        if (mCurrentPage == 1) {
                            if (mItems.isEmpty()){
                                mUserReposFragment.showSnackBar("No results found!");
                            }else {
                                mUserReposFragment.setItems(mItems);
                            }
                        }else {
                            mUserReposFragment.addNextPage(mItems);
                        }
                        mUserReposFragment.hideProgressBar();

                        //check link header
                        String linkHeader = listResponse.headers().get("link");

                        if (linkHeader != null) {
                            if (linkHeader.contains("next")) {
                                mCurrentPage++;
                            } else {
                                isEverythingFetched = true;
                            }
                        } else {
                            isEverythingFetched = true;
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserReposFragment.showSnackBar("Error: " + e.getMessage());
                        mUserReposFragment.hideProgressBar();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
