package albumstore;

public class AlbumInfo {
    private String artist;
    private String title;
    private String year;

  public AlbumInfo() {

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

  @Override
  public String toString() {
    return "AlbumInfo{" +
        "artist='" + artist + '\'' +
        ", title='" + title + '\'' +
        ", year='" + year + '\'' +
        '}';
  }
}
