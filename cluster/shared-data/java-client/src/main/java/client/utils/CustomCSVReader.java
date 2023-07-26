package client.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomCSVReader<T> {
  private static final Logger LOGGER = LogManager.getLogger(CustomCSVReader.class);
  private final String csvFilePath;

  public CustomCSVReader(String csvFilePath) {
    this.csvFilePath = csvFilePath;
  }

  public String readAsString() throws IOException {
    try {
      Stream<String> lines = Files.lines(Paths.get(csvFilePath));
      String content = lines
        .skip(1)  // Esto omite la primera línea
        .collect(Collectors.joining("\n"));

      return content;
    } catch (IOException e) {
      LOGGER.error("Error reading CSV file: " + e.getMessage());
      throw e;
    }
  }
}