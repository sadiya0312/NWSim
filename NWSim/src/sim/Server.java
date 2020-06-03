package sim;
import java.io.*;
import java.net.*;
import java.util.Date;

public class Server {
	Server sr;
	static long traveltime=System.nanoTime();
	public Server() {
		// TODO Auto-generated constructor stub
	}
	public void serverstart() {

		int port = 5321;

		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("\nServer is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();

				System.out.println("New client connected");

				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);

				writer.println(new Date().toString());
				socket.close();
			}

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}


	}
}

