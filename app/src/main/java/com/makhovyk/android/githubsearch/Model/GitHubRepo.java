package com.makhovyk.android.githubsearch.Model;

import com.google.gson.annotations.SerializedName;


public class GitHubRepo {

    private String name;
    private String description;
    @SerializedName("created_at")
    private String createdAt;


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreateDate() {
        return createdAt;
    }
}
