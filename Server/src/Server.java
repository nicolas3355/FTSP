import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	
	private int port;
	private String[] servers;
	ServerSocket welcomeSocket;
	
	public Server(int port, String[] servers) throws IOException{
		this.port = port;
		servers = this.servers;
		welcomeSocket =  new ServerSocket(port);
	}
	
	public void init() throws IOException{
		while (true){
			Socket connectionSocket = welcomeSocket.accept();
			try{
				Worker worker = new Worker(connectionSocket);
				worker.run();
			}
			catch(Exception e){
				
			}
		}
	}


	
	
	
}
