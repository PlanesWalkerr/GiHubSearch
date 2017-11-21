package com.makhovyk.android.githubsearch.View;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makhovyk.android.githubsearch.Model.GitHubRepo;
import com.makhovyk.android.githubsearch.Presenter.SearchPresenterImpl;
import com.makhovyk.android.githubsearch.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.SearchView.OnQueryTextListener;


public class SearchFragment extends Fragment implements SearchView{

    private static final String SAVED_DATA = "SavedData";

    private SearchPresenterImpl mSearchPresenter;
    private RecyclerView mSearchRepoRecyclerView;
    private ProgressBar mLoadingProgressBar;
    private List<GitHubRepo> mItems = new ArrayList<>();
    private TextView mPromtTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mSearchPresenter == null){
            mSearchPresenter = new SearchPresenterImpl(this);
        }

        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        mSearchRepoRecyclerView = (RecyclerView) view.findViewById(R.id.search_repos_recycler_view);
        mSearchRepoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPromtTextView = view.findViewById(R.id.search_promt);
        if (!mItems.isEmpty()){
            hideSearchPromt();
        }
        mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_progress_bar);
        mSearchRepoRecyclerView.setAdapter(new SearchRepoAdapter(mItems));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.fragment_search,menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.onActionViewCollapsed();
                mSearchPresenter.onSubmitClicked(query);
                hideSearchPromt();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private class SearchRepoHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mDescriptionTextView;
        private TextView mCreateDateTextView;

        SearchRepoHolder(View itemView) {
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

    private class SearchRepoAdapter extends RecyclerView.Adapter<SearchRepoHolder> {

        private List<GitHubRepo> repos;


        public SearchRepoAdapter(List<GitHubRepo> repos) {
            this.repos = repos;
        }

        @Override
        public SearchRepoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.search_repo_list_item, parent, false);

            return new SearchRepoHolder(v);
        }

        @Override
        public void onBindViewHolder(SearchRepoHolder holder, int position) {
            GitHubRepo repo = repos.get(position);
            holder.bindRepo(repo);
            //load next page if scrolled to the end
            if(position == repos.size()-1){
                mSearchPresenter.onBottomViewReached();
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
        mSearchRepoRecyclerView.setAdapter(new SearchRepoAdapter(mItems));
    }

    @Override
    public void addNextPage(List<GitHubRepo> items) {
        mItems = items;
        mSearchRepoRecyclerView.getAdapter().notifyDataSetChanged();
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
    public void hideSearchPromt() {
        mPromtTextView.setVisibility(View.GONE);
    }

    @Override
    public void showSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(R.id.search_layout),
                        message,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
