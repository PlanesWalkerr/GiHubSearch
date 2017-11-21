package com.makhovyk.android.githubsearch.View;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makhovyk.android.githubsearch.Model.GitHubRepo;
import com.makhovyk.android.githubsearch.Presenter.UserReposPresenterImpl;
import com.makhovyk.android.githubsearch.R;
import com.makhovyk.android.githubsearch.Utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class UserReposFragment extends Fragment implements UserReposView {

    private UserReposPresenterImpl mUserReposPresenter;
    private SessionManager mSessionManager;
    private RecyclerView mUserRepoRecyclerView;
    private ProgressBar mLoadingProgressBar;
    private List<GitHubRepo> mItems = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSessionManager = new SessionManager(getActivity().getApplicationContext());
        if (mUserReposPresenter == null) {
            mUserReposPresenter = new UserReposPresenterImpl(this, mSessionManager);
        }

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_repos, container, false);
        mUserRepoRecyclerView = (RecyclerView) view.findViewById(R.id.user_repos_recycler_view);
        mUserRepoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_progress_bar);

        if (mItems.isEmpty()) {
            mUserReposPresenter.getUserRepos();
        }else {
            mUserRepoRecyclerView.setAdapter(new UserRepoAdapter(mItems));
        }

        return view;
    }


    private class UserRepoHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mDescriptionTextView;
        private TextView mCreateDateTextView;

        UserRepoHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView.findViewById(R.id.repo_title_text_view);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.repo_description_text_view);
            mCreateDateTextView = itemView.findViewById(R.id.created_date);
        }

        void bindRepo(GitHubRepo repo) {
            mTitleTextView.setText(repo.getName());
            mDescriptionTextView.setText(repo.getDescription());
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault());
            Date date = null;
            try {
                date = format.parse(repo.getCreateDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mCreateDateTextView.setText(getString(R.string.create_date,format.format(date)));
        }
    }

    private class UserRepoAdapter extends RecyclerView.Adapter<UserRepoHolder> {

        private List<GitHubRepo> repos;


        public UserRepoAdapter(List<GitHubRepo> repos) {
            this.repos = repos;
        }

        @Override
        public UserRepoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.repo_list_item, parent, false);

            return new UserRepoHolder(v);
        }

        @Override
        public void onBindViewHolder(UserRepoHolder holder, int position) {
            GitHubRepo repo = repos.get(position);
            holder.bindRepo(repo);
            //load next page if scrolled to the end
            if (position == repos.size() - 1) {
                mUserReposPresenter.onBottomViewReached();
            }

        }

        @Override
        public int getItemCount() {
            return repos.size();
        }

    }

    @Override
    public void setItems(List<GitHubRepo> items) {
        mItems = items;
        mUserRepoRecyclerView.setAdapter(new UserRepoAdapter(mItems));
    }

    @Override
    public void addNextPage(List<GitHubRepo> items) {
        mItems = items;
        mUserRepoRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void showProgressBar() {
        mLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mLoadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(R.id.user_repos_layout),
                        message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
