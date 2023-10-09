package AlbumStoreClient;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.DefaultApi;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTestRunner {
  private static final int MAX_ATTEMPTS = 5;

  public static int runTest(int apiCallCount, int threadGroupSize, int numThreadGroups,
      int delayInSeconds, String ipAddr) throws InterruptedException {
    AtomicInteger successCallNum = new AtomicInteger();

    CountDownLatch latch = new CountDownLatch(numThreadGroups * threadGroupSize);
    for (int i = 0; i < numThreadGroups; i++) {
      // Start a new thread group
      for (int j = 0; j < threadGroupSize; j++) {
        Runnable runnableThreadMethod = () -> {
          ApiClient apiClientInstance = new ApiClient();
          apiClientInstance.setBasePath(ipAddr);
          DefaultApi apiInstance = new DefaultApi(apiClientInstance);
          for (int k = 0; k < apiCallCount; k++) {
            int attempted = 0;
            try {
              ApiResponse postApiResponse;
              while (attempted < MAX_ATTEMPTS) {
                postApiResponse = apiInstance.newAlbumWithHttpInfo(TestData.IMAGE,
                    TestData.PROFILE);
//                if (postApiResponse.getStatusCode() == 201) {
                if (postApiResponse.getStatusCode() == 200) {
                  successCallNum.addAndGet(1);
                  break;
                } else if (postApiResponse.getStatusCode() >= 400
                    && postApiResponse.getStatusCode() < 600) {
                  attempted++;
                }
              }

              attempted = 0;
              ApiResponse getApiResponse;
              while (attempted < MAX_ATTEMPTS) {
                getApiResponse = apiInstance.getAlbumByKeyWithHttpInfo(TestData.ALBUM_ID);
                if (getApiResponse.getStatusCode() == 200) {
                  successCallNum.addAndGet(1);
                  break;
                } else if (getApiResponse.getStatusCode() >= 400
                    && getApiResponse.getStatusCode() < 600) {
                  attempted++;
                }
              }

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

    return successCallNum.get();
  }
}

//  public class RunnerbleThreadMethod implements Runnable {
//    private static final int MAX_ATTEMPTS = 5;
////    private int apiCallCount;
////    private String ipAddr;
////    private DefaultApi apiInstance;
//
////    public RunnerbleThreadMethod(int apiCallCount, String ipAddr) {
////      this.apiCallCount = apiCallCount;
////      this.ipAddr = ipAddr;
////      ApiClient apiClientInstance = new ApiClient();
////      apiClientInstance.setBasePath(ipAddr);
////      this.apiInstance = new DefaultApi(apiClientInstance);
////    }
//
//    @Override
//    public void run() {
//      ApiClient apiClientInstance = new ApiClient();
//      apiClientInstance.setBasePath(ipAddr);
//      DefaultApi apiInstance = new DefaultApi(apiClientInstance);
//      System.out.println("run one thread");
//      for (int i = 0; i < apiCallCount; i++) {
//        int attempted = 0;
//        try {
//          ApiResponse postApiResponse;
//          while (attempted < MAX_ATTEMPTS) {
//            postApiResponse = apiInstance.newAlbumWithHttpInfo(TestData.IMAGE, TestData.PROFILE);
//            if (postApiResponse.getStatusCode() == 201) {
//              successCallNum += 1;
//              break;
//            } else if (postApiResponse.getStatusCode() >= 400 && postApiResponse.getStatusCode() < 600) {
//              System.out.println("4XX");
//              attempted++;
//            }
//          }
//
//          attempted = 0;
//          ApiResponse getApiResponse;
//          while (attempted < MAX_ATTEMPTS) {
//            getApiResponse = apiInstance.getAlbumByKeyWithHttpInfo(TestData.ALBUM_ID);
//            if (getApiResponse.getStatusCode() == 201) {
//              successCallNum += 1;
//              break;
//            } else if (getApiResponse.getStatusCode() >= 400 && getApiResponse.getStatusCode() < 600) {
//              attempted++;
//            }
//          }
//
//        } catch (ApiException e) {
//          System.err.println("Fatal error when calling API: " + e.getMessage());
//          e.printStackTrace();
//        }
//      }
//      latch.countDown();
//      System.out.println("countdown - 1");
//    }
//  }
//}
