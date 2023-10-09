package AlbumStoreClient;

import java.util.concurrent.TimeUnit;

public class ApiTestClient {
  private static final int ARGS_NUM = 4;
  private static final int STARTUP_THREAD_GROUP_SIZE = 10;
  private static final int STARTUP_API_CALL_COUNT = 100;
  private static final int RUNNING_API_CALL_COUNT = 1000;

  public static void main(String[] args) throws InterruptedException {
    // process args
    if (args.length != ARGS_NUM) {
      System.err.println("command line should contain <threadGroupSize> <numThreadGroups> <delayInSeconds> <IPAddr>");
      System.exit(1);
    }
    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    int delayInSeconds = Integer.parseInt(args[2]);
    String IPAddr = args[3];

    int successReqForStartUp = LoadTestRunner.runTest(STARTUP_API_CALL_COUNT, STARTUP_THREAD_GROUP_SIZE, 1, 0, IPAddr);
    long startTime = System.currentTimeMillis();
    int successReqForRunning = LoadTestRunner.runTest(RUNNING_API_CALL_COUNT, threadGroupSize, numThreadGroups, delayInSeconds, IPAddr);

    long endTime = System.currentTimeMillis();
    long wallTime = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);

    long throughput = (2 * RUNNING_API_CALL_COUNT * threadGroupSize * numThreadGroups) / wallTime;
    long throughput2 = (successReqForRunning) / wallTime;

    System.out.println(String.format("wall Time: %d sec", wallTime));
    System.out.println(String.format("throughput(for all request): %d requests/sec", throughput));
    System.out.println(String.format("the number of successful requests: %d", successReqForRunning));
    System.out.println(String.format("throughput(only for successful request): %d requests/sec", throughput2));
  }
}
