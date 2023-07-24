package client.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CustomCSVReader<T> {
  private static final Logger LOGGER = LogManager.getLogger(CustomCSVReader.class);
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
      LOGGER.error("Error reading CSV file: " + e.getMessage());
      throw e;
    }
  }
}