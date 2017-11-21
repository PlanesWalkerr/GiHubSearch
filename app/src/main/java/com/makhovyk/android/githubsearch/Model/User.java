package com.makhovyk.android.githubsearch.Model;

import com.google.gson.annotations.SerializedName;


public class User {
    @SerializedName("login")
    private String login;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("public_repos")
    private int publicRepos;
    @SerializedName("avatar_url")
    private String avatarUrl;

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public int getPublicRepos() {
        return publicRepos;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }
}
