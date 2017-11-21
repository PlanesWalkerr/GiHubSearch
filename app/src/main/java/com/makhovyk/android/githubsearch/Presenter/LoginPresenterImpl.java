package com.makhovyk.android.githubsearch.Presenter;

import com.makhovyk.android.githubsearch.Model.AccessToken;
import com.makhovyk.android.githubsearch.Model.GitHubClient;
import com.makhovyk.android.githubsearch.Model.User;
import com.makhovyk.android.githubsearch.Utils.SessionManager;
import com.makhovyk.android.githubsearch.View.LoginFragment;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenterImpl implements LoginPresenter {

    private String BASE_LOGIN_URL = "https://github.com";
    private String BASE_URL = "https://api.github.com";
    private LoginFragment mLoginFragment;
    private SessionManager mSessionManager;


    public LoginPresenterImpl(LoginFragment loginFragment, SessionManager sessionManager) {
        mLoginFragment = loginFragment;
        mSessionManager = sessionManager;
    }

    @Override
    public void getAccessToken(String code) {
        GitHubClient client = new GitHubClient(BASE_LOGIN_URL);
        client.getAccessToken(code).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AccessToken>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AccessToken accessToken) {
                        mSessionManager.setAccessToken(accessToken.getToken());
                        getUser();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void getUser() {
        GitHubClient client = new GitHubClient(BASE_URL);
        client.getUser(mSessionManager.getAccessToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(User user) {

                        mSessionManager.setName(user.getLogin());
                        mSessionManager.setEmail(user.getEmail());
                        mSessionManager.setPhoto(user.getAvatarUrl());
                        mLoginFragment.navigateToUserRepos();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
