package shiran.movies;

import java.util.List;


public class User {

    List<String> favorite;
    List<String> ordered;

    public List<String> getOrdered() {
        return ordered;
    }

    public void setOrdered(List<String> ordered) {
        this.ordered = ordered;
    }

    public List<String> getFavorite() {
        return favorite;
    }

    public void setFavorite(List<String> favorite) {
        this.favorite = favorite;
    }

}
