package macro.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import macro.dbo.Salary;

public class Server {
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;

  private List<Salary> readSalaries(String salariesString) {
    try {
      ObjectMapper mapper = new ObjectMapper();

      return mapper.readValue(salariesString, new TypeReference<List<Salary>>(){});
    } catch (Exception e) {
      System.out.println("Error reading salaries: " + e.getMessage());
      return null;
    }
  }

  private void start(int port) {
    try {
      serverSocket = new ServerSocket(port);

      while (true) {
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          if (inputLine.contains("salaries")) {
            System.out.println("Salaries received!");
            List<Salary> salariesList = readSalaries(inputLine.split("___")[1]);

            if (Objects.nonNull(salariesList)) out.println("salaries received!");
            else out.println("error receiving salaries!");
          } else if (inputLine.equals("bye")) {
            out.println("bye");
            break;
          } else {
            out.println("not supported command");
          }
        }
      }
    } catch (IOException e) {
      System.out.println("Error starting the server: " + e.getMessage());
    }
  }

  private void stop() throws IOException {
    in.close();
    out.close();
    clientSocket.close();
    serverSocket.close();
  }

  public static void main(String[] args) {
    new Server().start(6666);
  }
}
