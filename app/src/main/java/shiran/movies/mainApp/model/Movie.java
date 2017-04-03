package shiran.movies.mainApp.model;

public class Movie {
    private String title;
    private int year;
    private String imdbID;
    private String type;
    private String poster;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    private boolean favorite;

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    @Override
    public String toString() {
        return "Movie{" +
                "imdbID='" + imdbID + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", type='" + type + '\'' +
                ", poster='" + poster + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if (obj instanceof Movie){
            if (this.getImdbID().equals(((Movie)obj).getImdbID()))
                return true;
        }
        return false;
    }
}
