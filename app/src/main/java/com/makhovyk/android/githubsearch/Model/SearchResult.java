package com.makhovyk.android.githubsearch.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SearchResult {
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("items")
    private List<GitHubRepo> items;

    public int getTotalCount() {
        return totalCount;
    }

    public List<GitHubRepo> getItems() {
        return items;
    }
}
