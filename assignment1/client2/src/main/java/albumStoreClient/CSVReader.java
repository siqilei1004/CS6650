package albumStoreClient;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {

  private String filePath;

  public CSVReader(String filePath) {
    this.filePath = filePath;
  }

  public void calculateStats() throws IOException {
    DescriptiveStatistics postLatencies = new DescriptiveStatistics();
    DescriptiveStatistics getLatencies = new DescriptiveStatistics();

    try (FileReader fileReader = new FileReader(filePath);
        CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader())) {

      for (CSVRecord record : csvParser) {
        String requestType = record.get("request type");
        long latency = Long.parseLong(record.get("latency"));

        if ("POST".equalsIgnoreCase(requestType)) {
          postLatencies.addValue(latency);
        } else if ("GET".equalsIgnoreCase(requestType)) {
          getLatencies.addValue(latency);
        }
      }
    }

    System.out.println("POST Request Statistics:");
    printStatistics(postLatencies);

    System.out.println("\nGET Request Statistics:");
    printStatistics(getLatencies);
  }

  private void printStatistics(DescriptiveStatistics stats) {
    System.out.println("Mean: " + stats.getMean() + " millisecs");
    System.out.println("Median: " + stats.getPercentile(50) + " millisecs");
    System.out.println("99th Percentile: " + stats.getPercentile(99) + " millisecs");
    System.out.println("Min: " + stats.getMin() + " millisecs");
    System.out.println("Max: " + stats.getMax() + " millisecs");
  }
}
