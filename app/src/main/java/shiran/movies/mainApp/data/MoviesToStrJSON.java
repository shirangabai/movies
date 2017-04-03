package shiran.movies.mainApp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import shiran.movies.U;
import shiran.movies.mainApp.model.Movie;

public class MoviesToStrJSON {

    public static String convert(List<Movie> movies){
        JSONObject root = new JSONObject();
        JSONArray jsonArray = new JSONArray();
            try {
                for (Movie m : movies) {
                    JSONObject o = new JSONObject();
                    o.put(U.TITLE, m.getTitle());
                    o.put(U.IMDB_ID, m.getImdbID());
                    o.put(U.POSTER, m.getPoster());
                    o.put(U.TYPE, m.getType());
                    o.put(U.YEAR, m.getYear());
                    jsonArray.put(o);
                }
                root.put("Search",jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        return root.toString();
    }

}
