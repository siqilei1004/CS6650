package albumstore;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "StoreServlet", value = "/albums/*")
@MultipartConfig
public class StoreServlet extends HttpServlet {

  private class AlbumInfo {
    private String artist;
    private String title;
    private String year;
  }

  private class ImageMetaData {
    private String albumID;
    private String imageSize;
  }

  private class ErrorMsg {
    private String msg;
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();

    // check we have a URL
    String urlPath = request.getPathInfo();
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ErrorMsg errorMsg = new ErrorMsg();
      errorMsg.msg = "missing parameters";
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }
    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ErrorMsg errorMsg = new ErrorMsg();
      errorMsg.msg = "invalid request";
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    // TODO: get albumInfo
    AlbumInfo albumInfo = new AlbumInfo();
    albumInfo.artist = "Sex Pistols";
    albumInfo.title = "Never Mind The Bollocks!";
    albumInfo.year = "1977";

    response.setStatus(HttpServletResponse.SC_OK);
    response.getOutputStream().print(gson.toJson(albumInfo));
    response.getOutputStream().flush();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();

    // check if URL is valid
    String urlPath = request.getPathInfo();
    if (urlPath != null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ErrorMsg errorMsg = new ErrorMsg();
      errorMsg.msg = "invalid request";
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    Part imagePart = request.getPart("image");
    if (imagePart == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ErrorMsg errorMsg = new ErrorMsg();
      errorMsg.msg = "No image uploaded";
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    String contentType = imagePart.getContentType();
    if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ErrorMsg errorMsg = new ErrorMsg();
      errorMsg.msg = "Invalid image format. Only JPEG and PNG are supported.";
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    // get its size (width and height)
    try {
      BufferedImage image = ImageIO.read(imagePart.getInputStream());
      int width = image.getWidth();
      int height = image.getHeight();

      ImageMetaData imageMetaData = new ImageMetaData();
      imageMetaData.albumID = "4";
      imageMetaData.imageSize = String.valueOf(width * height);

      response.setStatus(HttpServletResponse.SC_OK);
      response.getOutputStream().print(gson.toJson(imageMetaData));
      response.getOutputStream().flush();

    } catch (IOException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      ErrorMsg errorMsg = new ErrorMsg();
      errorMsg.msg = "Error processing the uploaded image.";
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

  }

  private boolean isUrlValid(String[] urlParts) {
    // TODO: more validation to the request url path according to the API spec
    String digitPattern = "\\d+";
    return urlParts[urlParts.length - 1].matches(digitPattern);
  }
}
