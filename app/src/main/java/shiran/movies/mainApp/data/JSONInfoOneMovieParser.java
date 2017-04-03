package shiran.movies.mainApp.data;

import org.json.JSONException;
import org.json.JSONObject;

import shiran.movies.U;
import shiran.movies.mainApp.model.InfoMovie;


public class JSONInfoOneMovieParser {

    public static InfoMovie getMovies(String data) {
        if (data==null) return null;
        try {

            JSONObject rootObj = new JSONObject(data);
                    InfoMovie m = new InfoMovie() ;
                    m.setPlot(rootObj.getString(U.PLOT));
                    m.setDirector(rootObj.getString(U.DIRECTOR));
                    m.setWriter(rootObj.getString(U.WRITER));
                    m.setGenre(rootObj.getString(U.GENRE));
                    m.setImdbID(rootObj.getString(U.IMDB_ID));
                    m.setActors(rootObj.getString(U.ACTORS));

                    return m;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
