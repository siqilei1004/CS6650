package albumStoreClient;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {
  private BufferedWriter writer;
  private String filePath;

  public CSVWriter(String filePath) {
    this.filePath = filePath;
    openFile();
  }

  private void openFile() {
    try {
      writer = new BufferedWriter(new FileWriter(filePath, true));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeEntry(String[] entry) {
    try {
      StringBuilder sb = new StringBuilder();
      for (String field : entry) {
        sb.append("\"").append(field).append("\",");
      }
      sb.deleteCharAt(sb.length() - 1);

      writer.write(sb.toString());
      writer.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void closeFile() {
    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
