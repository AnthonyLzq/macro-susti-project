package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import client.utils.ConnectionHandler;
import client.utils.SalaryHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SparkAppSender {
  private static final Logger LOGGER = LogManager.getLogger(SparkAppSender.class);

  public static void main(String[] args) throws IOException {
    ServerSocket listener = new ServerSocket(9090);

    if (Objects.nonNull(listener.getInetAddress())) {
      InetAddress inetAddress = listener.getInetAddress();
      LOGGER.info("Listening on address: " + inetAddress.getCanonicalHostName());
      LOGGER.info("Listening on port: " + listener.getLocalPort());
    }

    String salaryMessage = new SalaryHandler().getSalaryMessage();
    LOGGER.info("Salary message: " + salaryMessage);

    try {
      while (true) {
        Socket socket = listener.accept();
        new Thread(new ConnectionHandler(socket, salaryMessage)).start();
      }
    } finally {
      listener.close();
    }
  }
}
