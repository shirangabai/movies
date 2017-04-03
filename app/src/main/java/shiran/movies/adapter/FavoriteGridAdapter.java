package shiran.movies.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FavoriteGridAdapter extends RecyclerView.Adapter<FavoriteGridAdapter.MyViewHolder> {

    private final List<Movie> movies;
    private final Context c;
    private FavoriteDB db = FavoriteDB.inctance;

    public FavoriteGridAdapter(Context c, List<Movie> movies) {
        this.c = c;
        this.movies = movies;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_favorite_card,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        Movie m = movies.get(i);
        holder.title.setText(m.getTitle());
        holder.year.setText(m.getYear()+"");

        if (m.getPoster().equals("N/A")) holder.poster.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.film2));
        else Picasso.with(c).load(m.getPoster()).into(holder.poster);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U.searchQuery = "?i=" + movies.get(i).getImdbID();
                MovieInfoDialog cdd = new MovieInfoDialog(c, movies.get(i),holder.poster.getDrawable() , new FavoriteGridAdapter.CloseDialog());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView year;
        public ImageView poster;
        public View root;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.lbl_card_title);
            year = (TextView) view.findViewById(R.id.lbl_year);
            poster = (ImageView) view.findViewById(R.id.img_card_poster);
            root = view.findViewById(R.id.layout_root);
        }
    }

    public class CloseDialog implements MyCallback {
        //final ImageButton btnF;
        public CloseDialog() {

        }



        @Override
        public void onBack(Object movie) {
            //db.setFavoriteValueInDB((Movie)movie,FavoriteGridAdapter.this);
            Movie m = (Movie)movie;
            db.setFavoriteValueInDB(m,FavoriteGridAdapter.this);

            if (!m.isFavorite()) {
                notifyDataSetChanged();
                notifyItemRemoved(movies.indexOf(movie));
            }
        }
    }


}
