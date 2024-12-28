public class Movie 
{
    protected String title;
    protected String genre;
    protected String summary;
    protected String posterPath;

    public Movie(String title, String genre, String summary, String posterPath) {
        this.title = title;
        this.genre = genre;
        this.summary = summary;
        this.posterPath = posterPath;
    }

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getGenre() {return genre;}
    public void setGenre(String genre) {this.genre = genre;}
    public String getSummary() {return summary;}
    public void setSummary(String summary) {this.summary = summary;}
    public String getPosterPath() {return posterPath;}
    public void setPosterPath(String posterPath) {this.posterPath = posterPath;}
}

