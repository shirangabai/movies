package shiran.movies.mainApp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import shiran.movies.R;
import shiran.movies.U;
import shiran.movies.adapter.FavoriteGridAdapter;
import shiran.movies.mainApp.data.FavoriteDB;
import shiran.movies.mainApp.data.JSONMoviesParser;
import shiran.movies.mainApp.data.MoviesToStrJSON;
import shiran.movies.mainApp.model.Movie;


public class FavoriteFragment extends Fragment {

    FavoriteGridAdapter adapter;
    RecyclerView recyclerView;

    FavoriteDB db = FavoriteDB.inctance;
    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        loadPrefs();




        adapter = new FavoriteGridAdapter(getContext(), db.getMovies());
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_favorite);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(manager);
        setAdapter();

        return view;
    }

    public void setAdapter() {
        //adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        U.savePreferences(getActivity(),U.PREFS_FEVORITE+U.userId,MoviesToStrJSON.convert(db.getMovies()));

    }



    private void loadPrefs() {
        String strMovies = U.loadPreferences(getActivity(), U.PREFS_FEVORITE+U.userId);
        List<Movie> movies = new ArrayList<>();
        if (strMovies != null) {
            movies = JSONMoviesParser.getMovies(strMovies);
            if (movies.size() > 0) {
                for (Movie m : movies) m.setFavorite(true);
            }

        }
        db.setMovies(movies);
    }

    public void onClickExport() {
        DatabaseReference myRef;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = database.getReference("users/" + userID);
        myRef.setValue(db.getMovies());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickImport() {
        DatabaseReference myRef;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = database.getReference("users/" + userID);

        final List<Movie> movies = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot m : dataSnapshot.getChildren() ){
                    movies.add((Movie)m.getValue(Movie.class));
                }
                db.setMovies(movies);
                adapter = new FavoriteGridAdapter(getContext(), db.getMovies());
                setAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickDeleteAll(){
        List<Movie> movies = new ArrayList<>();
        db.setMovies(movies);
        adapter = new FavoriteGridAdapter(getContext(), db.getMovies());
        setAdapter();

    }
}
