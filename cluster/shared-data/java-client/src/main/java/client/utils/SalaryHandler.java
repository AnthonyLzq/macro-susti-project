package client.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import client.dbo.Salary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import lombok.Getter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Getter
public class SalaryHandler {
  private static final Logger LOGGER = LogManager.getLogger(SalaryHandler.class);
  private final String salaryMessage;

  public SalaryHandler() {
    this.salaryMessage = buildSalaryMessage();
  }

  private String buildSalaryMessage() {
    try {
      String csvString = this.getSalaries();

      return csvString;
    } catch (Exception e) {
      LOGGER.error("Error building salary message: " + e.getMessage());

      return null;
    }
  }

  private String getSalaries() {
    try {
      String workingDir = System.getProperty("user.dir");
      String pathToCsv = "../db/salaries.csv";
      Path path = Paths.get(workingDir).resolve(pathToCsv).normalize();

      return new CustomCSVReader<>(pathToCsv).readAsString();
    } catch (Exception e) {
      LOGGER.error("Error building salary message: " + e.getMessage());

      return null;
    }
  }
}
