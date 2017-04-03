package shiran.movies.mainApp.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shiran.movies.R;
import shiran.movies.U;
import shiran.movies.adapter.SearchListAdapter;
import shiran.movies.mainApp.data.FavoriteDB;
import shiran.movies.mainApp.data.JSONMoviesParser;
import shiran.movies.mainApp.data.MoviesHttpClient;
import shiran.movies.mainApp.model.Movie;

public class SearchFragment extends Fragment {


    TextView lblSearchTitle;

    RecyclerView recyclerView;
    SearchListAdapter adapter;
    List<Movie> movies = new ArrayList<>();
    //DBM dbm;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = (RecyclerView) frag.findViewById(R.id.recycler_search);
        loadData();
        return frag;
    }

    public void searchMovies(String searchTitle) {
        String str[] = searchTitle.split("\\.\\.\\.", 2);
        if (str.length > 1 && !str[0].trim().isEmpty() && str[1].trim().matches("\\d{4}")) {
            U.searchQuery = "?s=" + str[0].trim().replace(" ","%20")  + "&y=" + str[1].trim();
        } else {
            U.searchQuery = "?s=" + searchTitle.replace(" ","%20");
        }
        loadData();
    }


    public void loadData() {
        moviesTask mt = new moviesTask();
        mt.execute();
    }

    private class moviesTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {
            String data = new MoviesHttpClient().getMoviesData("Sss");
            return JSONMoviesParser.getMovies(data);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            FavoriteDB db = FavoriteDB.inctance;
            for(Movie m : movies){
                m.setFavorite(db.isExist(m));
            }
            SearchFragment.this.movies = movies;

            adapter = new SearchListAdapter(movies, getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            //U.hideKeyboard(getContext());

            //callBack.onCallBack("onPostExecute search list is loaded");
        }


    }

    public void setAdapter(){
        recyclerView.setAdapter(adapter);
    }

}