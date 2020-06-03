package sim;
import java.io.*;
import java.net.*;

public class Client {

	Client cl;
	public Client() {
		// TODO Auto-generated constructor stub
	}
	public void clientstart()
	{  
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			String hostname = ip.getHostName();

			int port = 5321;

			try (Socket socket = new Socket(hostname, port)) {

				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));

				String time = reader.readLine();

				System.out.println("Server is found " +time);
				Server.traveltime=System.nanoTime()-Server.traveltime;
				System.out.println("Travel time of each flow in milliseconds in: "+(Server.traveltime/1000000));



			} catch (UnknownHostException ex) {

				System.out.println("Server not found: " + ex.getMessage());

			} catch (IOException ex) {

				System.out.println("I/O error: " + ex.getMessage());
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
