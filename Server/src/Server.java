import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static final String BACKUP_SERVER = "127.0.0.1";
	private static final int PORT_NUMBER = 1122;

	private ServerSocket serverSocket;

	// Creates a server that listens to 
	// connections on a specified port.
	public Server(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
	}

	// This method will actively listen 
	// for new connections to the server, 
	// and service requests.
	public void init() throws IOException {
		while (true) {
			System.out.println("[Server] Listening...");
			Socket socket = serverSocket.accept();
			try {
				// Spawn a worker thread.
				Worker worker = new Worker(socket, BACKUP_SERVER);
				worker.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("[Server] Serviced Request.");
		}
	}

	// Server main function.
	public static void main(String[] args){
		try {
			Server server = new Server(PORT_NUMBER);
			server.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}