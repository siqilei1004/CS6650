package albumstore;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "StoreServlet", value = "/albums/*")
@MultipartConfig
public class StoreServlet extends HttpServlet {
  private static final String PROFILE = "profile";
  private static final String IMAGE = "image";
  private static final String ARTIST = "artist";
  private static final String TITLE = "title";
  private static final String YEAR = "year";

  private AlbumDao albumDao = new AlbumDao();

//  private  AlbumDao albumDao;
//
//  public StoreServlet() {
//    albumDao = new AlbumDao();
//  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    ErrorMsg errorMsg = new ErrorMsg();

    String urlPath = request.getPathInfo();
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      errorMsg.setMsg("Invalid url");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      errorMsg.setMsg("Invalid id, should only contain digits");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    int id = Integer.parseInt(urlParts[urlParts.length - 1]);
    AlbumInfo albumInfo = albumDao.getAlbumById(id);
    if (albumInfo == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      errorMsg.setMsg("Album is not found");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    response.setStatus(HttpServletResponse.SC_OK);
    response.getOutputStream().print(gson.toJson(albumInfo));
    response.getOutputStream().flush();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    ErrorMsg errorMsg = new ErrorMsg();

    String urlPath = request.getPathInfo();
    if (urlPath != null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      errorMsg.setMsg("Invalid url");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    Part profilePart = request.getPart(PROFILE);
    if (profilePart == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      errorMsg.setMsg("No profile provided");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    // retrieve the content of profile
    BufferedReader profileReader = new BufferedReader(new InputStreamReader(profilePart.getInputStream()));
    String line;
    String artist = null;
    String title = null;
    String year = null;
    while ((line = profileReader.readLine()) != null) {
      String[] pairs = line.trim().split(": ", 2);
      if (pairs.length == 2) {
        String profileField = pairs[0];
        switch (profileField) {
          case ARTIST:
            artist = pairs[1];
            break;
          case TITLE:
            title = pairs[1];
            break;
          case YEAR:
            year = pairs[1];
            break;
        }
      }
    }
    if (artist == null || title == null || year == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      errorMsg.setMsg("Album profile fields are missing");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    // validate the image part
    Part imagePart = request.getPart("image");
    if (imagePart == null || imagePart.getContentType() == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      errorMsg.setMsg("No image uploaded");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    String contentType = imagePart.getContentType();
    if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      errorMsg.setMsg("Invalid image format. Only JPEG and PNG are supported");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    try {
      InputStream imageInputStream = imagePart.getInputStream();
      BufferedImage image = ImageIO.read(imageInputStream);
      int width = image.getWidth();
      int height = image.getHeight();

      ImageMetaData imageMetaData = new ImageMetaData();
      imageMetaData.setImageSize(String.valueOf(width * height));

      AlbumInfo newAlbumInfo = new AlbumInfo();
      newAlbumInfo.setArtist(artist);
      newAlbumInfo.setTitle(title);
      newAlbumInfo.setYear(year);

      int albumId = albumDao.createAlbum(newAlbumInfo, imageInputStream);
      imageMetaData.setAlbumID(String.valueOf(albumId));

      response.setStatus(HttpServletResponse.SC_OK);
      response.getOutputStream().print(gson.toJson(imageMetaData));
      response.getOutputStream().flush();

    } catch (IOException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      errorMsg.setMsg("Error creating the new album");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }
  }

  private boolean isUrlValid(String[] urlParts) {
    String digitPattern = "\\d+";
    return urlParts.length == 2 && urlParts[urlParts.length - 1].matches(digitPattern);
  }
}
