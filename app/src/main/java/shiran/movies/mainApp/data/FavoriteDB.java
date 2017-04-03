package shiran.movies.mainApp.data;

import java.util.ArrayList;
import java.util.List;

import shiran.movies.MyCallback;
import shiran.movies.mainApp.model.Movie;


public class FavoriteDB {
    public static final FavoriteDB inctance = new FavoriteDB();
    List<Movie> movies = new ArrayList<>();
    private MyCallback callback;


    private FavoriteDB(){
    }

    public void setDBChangeListener(MyCallback callback){
        this.callback = callback;

    }

    public  List<Movie> getMovies(){
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public boolean isExist(Movie movie){
        for (Movie m : movies){
            if (m.equals(movie)){
                return true;
            }
        }
        return false;
    }

    public void setFavoriteValueInDB(Movie movie , Object sender) {
        if (movie.isFavorite()) {
            if (!isExist(movie)) movies.add(movie);
        } else {
            if (isExist(movie)) movies.remove(movie);
        }
        callback.onBack(sender);

       //todo if (favoriteFragment != null) favoriteFragment.setAdapter();
    }

}
