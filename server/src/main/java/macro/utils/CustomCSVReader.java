package macro.utils;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CustomCSVReader<T> {
  private final String csvFilePath;

  public CustomCSVReader(String csvFilePath) {
    this.csvFilePath = csvFilePath;
  }

  public List<T> read(Class<T> type) throws IOException {
    try {
      List<T> result = new CsvToBeanBuilder<T>(new FileReader(csvFilePath))
        .withType(type)
        .build()
        .parse();

      return result;
    } catch (IOException e) {
      System.out.println("Error reading CSV file: " + e.getMessage());
      throw e;
    }
  }
}