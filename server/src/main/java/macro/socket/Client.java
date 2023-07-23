package macro.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import macro.dbo.Salary;
import macro.utils.CustomCSVReader;

public class Client {
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;

  private List<Salary> getSalaries() throws Exception {
    String workingDir = System.getProperty("user.dir");
    String pathToCsv = workingDir + "/db/salaries.csv";
    CustomCSVReader<Salary> reader = new CustomCSVReader<>(pathToCsv);

    try {
      return reader.read(Salary.class);
    } catch (Exception e) {
      System.out.println("Error reading CSV file: " + e.getMessage());
      throw e;
    }
  }

  private boolean startConnection(String ip, int port) {
    try {
      clientSocket = new Socket(ip, port);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      return true;
    } catch (Exception e) {
      System.out.println("Error starting the connection: " + e.getMessage());

      return false;
    }
  }

  private String sendMessage(Object msg) throws IOException {
    out.println(msg);

    return in.readLine();
  }

  private boolean stopConnection() {
    try {
      in.close();
      out.close();
      clientSocket.close();

      return true;
    } catch (IOException e) {
      System.out.println("Error stopping the connection: " + e.getMessage());

      return false;
    }
  }

  private String salariesToString(List<Salary> salaries) {
    Gson gson = new GsonBuilder().create();
    JsonArray jsonArray = gson.toJsonTree(salaries).getAsJsonArray();

    return jsonArray.toString();
  }

  private String buildSalaryMessage() {
    try {
      List<Salary> salaries = getSalaries();
      String salariesString = salariesToString(salaries);

      return "salaries___" + salariesString;
    } catch (Exception e) {
      System.out.println("Error building salary message: " + e.getMessage());

      return null;
    }
  }

  public static void main(String[] args) {
    Client client = new Client();
    boolean clientConnected = client.startConnection("127.0.0.1", 6666);

    if (!clientConnected) {
      System.out.println("Error connecting to server");

      return;
    }

    System.out.println("Client connected to server");

    try {
      String salaryMessage = client.buildSalaryMessage();
      String response = client.sendMessage(salaryMessage);
      System.out.println("Server response: " + response);
    } catch (Exception e) {
      System.out.println("Error sending message to server: " + e.getMessage());
    } finally {
      boolean clientDisconnected = client.stopConnection();

      if (clientDisconnected) System.out.println("Client disconnected from server");
    }
  }
}
