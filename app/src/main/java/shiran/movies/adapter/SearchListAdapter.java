package shiran.movies.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import shiran.movies.MyCallback;
import shiran.movies.R;
import shiran.movies.U;
import shiran.movies.mainApp.data.FavoriteDB;
import shiran.movies.mainApp.dialogs.MovieInfoDialog;
import shiran.movies.mainApp.model.Movie;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MovieHolder> {

    private List<Movie> movies;
    private LayoutInflater inflater;
    private Context c;
    private FavoriteDB db = FavoriteDB.inctance;
    //final MyCallback cb = new CloseDialog();

    public SearchListAdapter(List<Movie> movies, Context c) {
        inflater = LayoutInflater.from(c);
        this.movies = movies;
        this.c = c;

    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_row_search_movie, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieHolder holder, final int i) {
        final Movie m = movies.get(i);
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movies.get(i).setFavorite(!movies.get(i).isFavorite());
                setFavoriteImg(holder.favorite, movies.get(i).isFavorite());
                db.setFavoriteValueInDB(movies.get(i),SearchListAdapter.this);
                //-------------------------
//                if (movies.get(i).isFavorite())
//                    db.addMovieToFavoraie(movies.get(i));
//                else
//                    db.removeMovieFromFavorite(movies.get(i));

                //favoriteMovies.add(movies.get(i));

            }
        });
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U.searchQuery = "?i=" + movies.get(i).getImdbID();
                MovieInfoDialog cdd = new MovieInfoDialog(c, movies.get(i),holder.poster.getDrawable() , new CloseDialog(holder.favorite));
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });
        setFavoriteImg(holder.favorite, movies.get(i).isFavorite());

        holder.title.setText(m.getTitle());
        holder.year.setText(m.getYear() + "");


        if (m.getPoster().equals("N/A")) holder.poster.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.film2));
        else Picasso.with(c).load(m.getPoster()).into(holder.poster);
    }

    private void setFavoriteImg(ImageButton btn, boolean bool) {
        btn.setImageResource(bool ? R.drawable.favorite_up : R.drawable.favorite_down);
    }




    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView year;
        private ImageView poster;
        private ImageButton favorite;
        private View root;

        public MovieHolder(View row) {
            super(row);
            title = (TextView) row.findViewById(R.id.lbl_title);
            year = (TextView) row.findViewById(R.id.lbl_year);
            poster = (ImageView) row.findViewById(R.id.img_poster);
            favorite = (ImageButton) row.findViewById(R.id.btn_favorite_row);
            root = row.findViewById(R.id.row_root);
        }
    }


    public class CloseDialog implements MyCallback {
        final ImageButton btnF;
        public CloseDialog(ImageButton btnF) {
            this.btnF = btnF;
        }

        @Override
        public void onBack(Object movie) {
            db.setFavoriteValueInDB((Movie)movie,SearchListAdapter.this);
            setFavoriteImg(btnF,((Movie)movie).isFavorite());
        }
    }


}
