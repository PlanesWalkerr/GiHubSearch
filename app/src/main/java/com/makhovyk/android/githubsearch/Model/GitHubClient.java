package com.makhovyk.android.githubsearch.Model;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;



public class GitHubClient {


    private final String CLIENT_ID = "260a01d3966085fdefb4";
    private final String CLIENT_SECRET = "3fc85da7cf473b0a2624ea95b64019e4238eee21";
    private final String ITEMS_PER_PAGE = "50";

    private GitHubService mGitHubService;

    public GitHubClient(String baseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        mGitHubService = retrofit.create(GitHubService.class);
    }

    public Observable<AccessToken> getAccessToken(String code){
        return mGitHubService.getAccessToken(CLIENT_ID,CLIENT_SECRET,code);
    }

    public Observable<User> getUser(String accessToken){
        return mGitHubService.getUser(accessToken);
    }

    public Observable<Response<List<GitHubRepo>>> getUserReposResponse(String accessToken, String page){
        return mGitHubService.getUserReposResponse(accessToken,ITEMS_PER_PAGE,page);
    }

    public Observable<SearchResult> searchRepos(String query, String page){
        return mGitHubService.searchRepos(query,ITEMS_PER_PAGE,page);
    }
}
