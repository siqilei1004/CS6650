package albumStoreClient;

import io.swagger.client.model.AlbumsProfile;
import java.io.File;

public class TestData {
  public static final String ALBUM_ID = "1245";
  public static final File IMAGE = new File("nmtb.png");
  public static final AlbumsProfile PROFILE;

  static {
    PROFILE = new AlbumsProfile();
    PROFILE.setArtist("artist1");
    PROFILE.setTitle("title1");
    PROFILE.setYear("2023");
  }
}