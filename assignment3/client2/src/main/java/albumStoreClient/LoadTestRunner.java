package albumStoreClient;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.api.LikeApi;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTestRunner {
  private static final int MAX_ATTEMPTS = 5;

  public static int runTest(int apiCallCount, int threadGroupSize, int numThreadGroups,
      int delayInSeconds, String ipAddr, String filePath) throws InterruptedException {
    AtomicInteger successCallNum = new AtomicInteger();

    CSVWriter csvWriter = new CSVWriter(filePath);
    csvWriter.writeEntry(new String[]{"start time", "request type", "latency", "response code"});

    CountDownLatch latch = new CountDownLatch(numThreadGroups * threadGroupSize);
    for (int i = 0; i < numThreadGroups; i++) {
      // Start a new thread group
      for (int j = 0; j < threadGroupSize; j++) {
        Runnable runnableThreadMethod = () -> {
          ApiClient apiClientInstance = new ApiClient();
          apiClientInstance.setBasePath(ipAddr);
          DefaultApi apiInstance = new DefaultApi(apiClientInstance);
          LikeApi likeApiInstance = new LikeApi(apiClientInstance);
          for (int k = 0; k < apiCallCount; k++) {
            int attempted = 0;
            try {
              ApiResponse postAlbumApiResponse;
              long postRequestStartTime = System.currentTimeMillis();
              while (attempted < MAX_ATTEMPTS) {
                postAlbumApiResponse = apiInstance.newAlbumWithHttpInfo(TestData.IMAGE,
                    TestData.PROFILE);
                if (postAlbumApiResponse.getStatusCode() == 200) {
                  long postRequestEndTime = System.currentTimeMillis();
                  long latency = postRequestEndTime - postRequestStartTime;
                  csvWriter.writeEntry(new String[]{String.valueOf(postRequestStartTime), "POST", String.valueOf(latency), postAlbumApiResponse.toString()});
                  successCallNum.addAndGet(1);
                  break;
                } else if (postAlbumApiResponse.getStatusCode() >= 400
                    && postAlbumApiResponse.getStatusCode() < 600) {
                  attempted++;
                }
              }

              if (postReviewTest(likeApiInstance, csvWriter, TestData.LIKE)) {
                successCallNum.addAndGet(1);
              };

              if (postReviewTest(likeApiInstance, csvWriter, TestData.LIKE)) {
                successCallNum.addAndGet(1);
              };

              if (postReviewTest(likeApiInstance, csvWriter, TestData.DISLIKE)) {
                successCallNum.addAndGet(1);
              };

//              ApiResponse postReviewApiResponse;
//              while (attempted < MAX_ATTEMPTS) {
//                long getRequestStartTime = System.currentTimeMillis();
//                postReviewApiResponse = likeApiInstance.reviewWithHttpInfo(TestData.LIKE, TestData.ALBUM_ID_FOR_REVIEW);
//                if (postReviewApiResponse.getStatusCode() == 201) {
//                  long getRequestEndTime = System.currentTimeMillis();
//                  long latency = getRequestEndTime - getRequestStartTime;
//                  csvWriter.writeEntry(new String[]{String.valueOf(getRequestStartTime), "POST", String.valueOf(latency), postReviewApiResponse.toString()});
//                  successCallNum.addAndGet(1);
//                  break;
//                } else if (postReviewApiResponse.getStatusCode() >= 400
//                    && postReviewApiResponse.getStatusCode() < 600) {
//                  attempted++;
//                }
//              }

            } catch (ApiException e) {
              System.err.println("Fatal error when calling API: " + e.getMessage());
              e.printStackTrace();
            }
          }

          latch.countDown();
        };

        new Thread(runnableThreadMethod).start();
      }
      if (i < numThreadGroups - 1) {
        TimeUnit.SECONDS.sleep(delayInSeconds);
      }
    }
    // wait for all thread groups to complete
    latch.await();
    csvWriter.closeFile();
    return successCallNum.get();
  }

  private static boolean postReviewTest(LikeApi likeApiInstance, CSVWriter csvWriter,
      String LikeOrNot) throws ApiException {
    ApiResponse postReviewApiResponse;
    int attempted = 0;

    while (attempted < MAX_ATTEMPTS) {
      long getRequestStartTime = System.currentTimeMillis();
      postReviewApiResponse = likeApiInstance.reviewWithHttpInfo(LikeOrNot, TestData.ALBUM_ID_FOR_REVIEW);

      if (postReviewApiResponse.getStatusCode() == 201) {
        long getRequestEndTime = System.currentTimeMillis();
        long latency = getRequestEndTime - getRequestStartTime;
        //TODO: need to separate two post statistic in csv
        csvWriter.writeEntry(new String[]{String.valueOf(getRequestStartTime), "POST", String.valueOf(latency), postReviewApiResponse.toString()});
        return true;

      } else if (postReviewApiResponse.getStatusCode() >= 400
          && postReviewApiResponse.getStatusCode() < 600) {
        attempted++;
      }
    }
    return false;
  }
}