package albumstore;

public class AlbumAndReview {
  private String artist;
  private String title;
  private String year;
  private int like;
  private int dislike;

  public AlbumAndReview() {
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public int getLike() {
    return like;
  }

  public void setLike(int like) {
    this.like = like;
  }

  public int getDislike() {
    return dislike;
  }

  public void setDislike(int dislike) {
    this.dislike = dislike;
  }

  @Override
  public String toString() {
    return "AlbumAndReview{" +
        "artist='" + artist + '\'' +
        ", title='" + title + '\'' +
        ", year='" + year + '\'' +
        ", like=" + like +
        ", dislike=" + dislike +
        '}';
  }
}

