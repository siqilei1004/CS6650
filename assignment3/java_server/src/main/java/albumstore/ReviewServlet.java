package albumstore;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;
import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.apache.commons.pool2.impl.GenericObjectPool;

@WebServlet(name = "ReviewServlet", value = "/review/*")
@MultipartConfig
public class ReviewServlet extends HttpServlet {

  private static final String LIKE = "like";
  private static final String DISLIKE = "dislike";

  private final static String RABBITMQ_HOST = "RMQ_HOST";
  private final static String RABBITMQ_USERNAME = "RMQ_USERNAME";
  private final static String RABBITMQ_PASSWORD = "RMQ_PASSWORD";
  private static final String QUEUE_NAME = "like_dislike_queue";
  private static final String DELIMITER = " ";
  private final Connection connection;
  private final GenericObjectPool<Channel> channelPool;


//  private AlbumDao albumDao = new AlbumDao();
  public ReviewServlet() throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(System.getProperty(RABBITMQ_HOST));
    factory.setUsername(System.getProperty(RABBITMQ_USERNAME));
    factory.setPassword(System.getProperty(RABBITMQ_PASSWORD));
    connection = factory.newConnection();
    channelPool = new GenericObjectPool<>(new ChannelFactory(connection));
    channelPool.setMaxTotal(50);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    ErrorMsg errorMsg = new ErrorMsg();

    String urlPath = request.getPathInfo();
    if (urlPath == null || urlPath.isEmpty() || !isUrlValid(urlPath)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      errorMsg.setMsg("Invalid url");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }

    String[] urlParts = urlPath.split("/");
    String likeOrNot = urlParts[urlParts.length - 2];
    int id = Integer.parseInt(urlParts[urlParts.length - 1]);


    try {

      publishToQueue(likeOrNot, id);
//      albumDao.createReviewById(likeOrNot, id);

      response.setStatus(HttpServletResponse.SC_CREATED);
      response.getOutputStream().flush();

    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      errorMsg.setMsg("Error posting the new review");
      response.getOutputStream().print(gson.toJson(errorMsg));
      response.getOutputStream().flush();
      return;
    }
  }

  private boolean isUrlValid(String urlPath) {
    String[] urlParts = urlPath.split("/");
    String digitPattern = "\\d+";

    if (urlParts.length != 3){
      return false;
    }

    String likeOrNot = urlParts[urlParts.length - 2];
    if (likeOrNot != LIKE && likeOrNot != DISLIKE) {
      return false;
    }

    if (!urlParts[urlParts.length - 1].matches(digitPattern)) {
      return false;
    }

    return true;
  }

  private void publishToQueue(String likeOrDislike, int albumId) throws Exception {
    Channel channel = channelPool.borrowObject();

    // Declare a durable queue
    channel.queueDeclare(QUEUE_NAME, true, false, false, null);

    String message = likeOrDislike + DELIMITER + albumId;

    // Publish the message to the queue
    channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
    System.out.println(" [x] Sent '" + message + "'");
    channelPool.returnObject(channel);
  }
}

