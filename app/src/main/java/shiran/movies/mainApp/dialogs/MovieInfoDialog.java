package shiran.movies.mainApp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import shiran.movies.MyCallback;
import shiran.movies.R;
import shiran.movies.U;
import shiran.movies.mainApp.data.JSONInfoOneMovieParser;
import shiran.movies.mainApp.data.MoviesHttpClient;
import shiran.movies.mainApp.model.InfoMovie;
import shiran.movies.mainApp.model.Movie;

public class MovieInfoDialog extends Dialog {

    private Button btnX, btnClose;
    private ImageButton imgFavorite;
    private TextView lblTitle, lblPlot ,lblGener , lblWriter , lblActors , lblDirector ;
    private ImageView imgPoster;
    private final Movie movie;
    private final Context c;
    private final Drawable drawable;
    private ScrollView sv;
    private final MyCallback callback;

    public MovieInfoDialog(Context c, Movie movie, Drawable drawable , MyCallback callback) {

        super(c);
        this.movie = movie;
        this.c = c;
        this.drawable = drawable;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_movie_info);

        btnX = (Button) findViewById(R.id.btn_dialog_x);
        btnClose = (Button) findViewById(R.id.btn_dialog_close);
        imgPoster = (ImageView) findViewById(R.id.img_poster);
        sv = (ScrollView) findViewById(R.id.scroll_view_movie_info);
        lblPlot = (TextView) findViewById(R.id.lbl_plot);
        lblGener = (TextView) findViewById(R.id.lbl_gener);
        lblWriter = (TextView) findViewById(R.id.lbl_writer);
        lblActors = (TextView) findViewById(R.id.lbl_actors);
        lblDirector = (TextView) findViewById(R.id.lbl_director);
        imgFavorite = (ImageButton) findViewById(R.id.img_btn_dialog_favorite);

        moviesTask mt = new moviesTask();
        mt.execute();


        //Picasso.with(c).load(movie.getPoster()).into(imgPoster);
        imgPoster.setImageDrawable(drawable);
        imgPoster.post(new Runnable() {
            @Override
            public void run() {
                U.fixSize(imgPoster);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    sv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                            imgPoster.setY(i1 / 1.5f);
                        }
                    });

                }

            }
        });

        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movie.setFavorite(!movie.isFavorite());
                imgFavorite.setBackgroundResource(movie.isFavorite()?R.drawable.favorite_r:R.drawable.favorite_w);
            }
        });

        btnX.setOnClickListener(onClickClose);
        btnClose.setOnClickListener(onClickClose);

        lblTitle = (TextView) findViewById(R.id.lbl_title);
        lblTitle.setText(movie.getTitle());
    }

    private View.OnClickListener onClickClose = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.onBack(movie);
            dismiss();

        }
    };

    private class moviesTask extends AsyncTask<String, Void, InfoMovie> {

        @Override
        protected InfoMovie doInBackground(String... strings) {
            String data = new MoviesHttpClient().getMoviesData("Sss");
            return JSONInfoOneMovieParser.getMovies(data);
        }

        @Override
        protected void onPostExecute(InfoMovie infoMovie) {
            super.onPostExecute(infoMovie);
            if (infoMovie==null) return;
            lblPlot.setText(infoMovie.getPlot());
            lblGener.setText(infoMovie.getGenre());
            lblWriter.setText(infoMovie.getWriter().replace(", ","\n"));
            lblActors.setText(infoMovie.getActors().replace(", ","\n"));
            lblDirector.setText(infoMovie.getDirector().replace(", ","\n"));
            if (movie.isFavorite()) imgFavorite.setBackgroundResource(R.drawable.favorite_r);

        }


    }


}