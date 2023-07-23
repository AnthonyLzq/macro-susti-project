package macro.dbo;

import lombok.Data;
import lombok.Builder;

import java.io.Serializable;

import com.opencsv.bean.CsvBindByName;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Salary implements Serializable {
  @CsvBindByName(column = "work_year")
  private Integer workYear;

  @CsvBindByName(column = "experience_level")
  private String experienceLevel;

  @CsvBindByName(column = "employment_type")
  private String employmentType;

  @CsvBindByName(column = "job_title")
  private String jobTitle;

  private Integer salary;

  @CsvBindByName(column = "salary_currency")
  private String salaryCurrency;

  @CsvBindByName(column = "salary_in_usd")
  private Integer salaryInUsd;

  @CsvBindByName(column = "employee_residence")
  private String employeeResidence;

  @CsvBindByName(column = "remote_ratio")
  private Integer remoteRatio;

  @CsvBindByName(column = "company_location")
  private String companyLocation;

  @CsvBindByName(column = "company_size")
  private String companySize;
}
