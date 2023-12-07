import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitmqConsumer {

  private final static String RABBITMQ_HOST = "RMQ_HOST";
  private final static String RABBITMQ_USERNAME = "RMQ_USERNAME";
  private final static String RABBITMQ_PASSWORD = "RMQ_PASSWORD";
  private final static int CHANNEL_POOL_SIZE = 200;
  private final static int QOS = 200;
  private static final String QUEUE_NAME = "like_dislike_queue";
  private static final String DELIMITER = " ";

  private ReviewDao reviewDao;

  public RabbitmqConsumer() {
    this.reviewDao = new ReviewDao();
  }

  private Runnable createConsumerRunnable(Connection connection) {
    return () -> {
      try (Channel channel = connection.createChannel()) {
        // Declare a durable queue
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(QOS);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//          System.out.println(" [x] Received '" + message + "'");

          // Process the message and update the database
          String[] args = message.split(DELIMITER);
          String likeOrDislike = args[0];
          int albumId = Integer.parseInt(args[1]);
          reviewDao.createReviewById(likeOrDislike, albumId);

          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        // Start consuming messages
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
        });
      } catch (IOException e) {
        e.printStackTrace();
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
    };
  }

  public static void main(String[] args) throws IOException, TimeoutException {
    RabbitmqConsumer consumer = new RabbitmqConsumer();
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(System.getenv(RABBITMQ_HOST));
    factory.setUsername(System.getenv(RABBITMQ_USERNAME));
    factory.setPassword(System.getenv(RABBITMQ_PASSWORD));
    ExecutorService executorService = Executors.newFixedThreadPool(CHANNEL_POOL_SIZE);
    Connection connection = factory.newConnection(executorService);

    for (int i = 0; i < CHANNEL_POOL_SIZE; i++) {
      executorService.submit(consumer.createConsumerRunnable(connection));
    }

    // Shutdown the executor when no more tasks are present
//    try {
//      executorService.shutdown();
//      executorService.awaitTermination(10, TimeUnit.SECONDS);
//    } catch (InterruptedException e) {
//      Thread.currentThread().interrupt();
//      e.printStackTrace();
//    }
  }
}


