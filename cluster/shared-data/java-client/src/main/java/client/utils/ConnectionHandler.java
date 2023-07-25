package client.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
  private static final Logger LOGGER = LogManager.getLogger(ConnectionHandler.class);
  private final Socket socket;
  private final String message;

  public ConnectionHandler(Socket socket, String message) {
    this.socket = socket;
    this.message = message;
  }

  @Override
  public void run() {
    try {
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      // Send the message once
      out.println(message);
    } catch (IOException e) {
      LOGGER.error("Error sending message: " + e.getMessage());
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        LOGGER.error("Error closing socket: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}
