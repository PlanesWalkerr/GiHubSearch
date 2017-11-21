package com.makhovyk.android.githubsearch.Model;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface GitHubService {
    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    Observable<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code
    );


    @GET("user")
    Observable<User> getUser(@Query("access_token") String accessToken);

    @GET("user/repos")
    Observable<Response<List<GitHubRepo>>> getUserReposResponse(@Query("access_token") String accessToken,
                                                                @Query("per_page") String perPage,
                                                                @Query("page") String page);

    @GET("search/repositories")
    Observable<SearchResult> searchRepos(@Query("q") String query,
                                         @Query("per_page") String perPage,
                                         @Query("page") String page);
}
