package shiran.movies.mainApp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import shiran.movies.U;
import shiran.movies.mainApp.model.Movie;


/**
 * Created by shiran on 16/02/2017.
 */

public class JSONMoviesParser {

    public static List<Movie> getMovies(String data) {

        List<Movie> movies = new ArrayList<>();
        if (data==null) return movies;
        try {

            JSONObject rootObj = new JSONObject(data);
            JSONArray moviesArray = rootObj.getJSONArray("Search");


                for (int i = 0; i < moviesArray.length(); i++) {
                    Movie m = new Movie();
                    m.setTitle(moviesArray.getJSONObject(i).getString(U.TITLE));
                    m.setImdbID(moviesArray.getJSONObject(i).getString(U.IMDB_ID));
                    m.setPoster(moviesArray.getJSONObject(i).getString(U.POSTER));
                    m.setType(moviesArray.getJSONObject(i).getString(U.TYPE));
                    String yearStr = moviesArray.getJSONObject(i).getString(U.YEAR);
                    yearStr = yearStr.replaceAll("[^\\d.]", "");
                    m.setYear(Integer.parseInt(yearStr));
                    movies.add(m);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }
}
