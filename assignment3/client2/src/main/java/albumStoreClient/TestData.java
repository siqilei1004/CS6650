package albumStoreClient;

import io.swagger.client.model.AlbumsProfile;
import java.io.File;

public class TestData {
  public static final String ALBUM_ID = "3";
  public static final File IMAGE = new File("nmtb.png");
  public static final AlbumsProfile PROFILE;
  public static final String LIKE = "like";
  public static final String DISLIKE = "dislike";
  public static final String ALBUM_ID_FOR_REVIEW = "2000";

  static {
    PROFILE = new AlbumsProfile();
    PROFILE.setArtist("artist1");
    PROFILE.setTitle("title1");
    PROFILE.setYear("2023");
  }
}