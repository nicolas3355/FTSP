import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {

	private int port;
	private static String backupId = "127.0.0.1";
	ServerSocket welcomeSocket;
	
	public Server(int port) throws IOException{
		this.port = port;
		welcomeSocket =  new ServerSocket(port);
	}
	
	public void init() throws IOException{
		while (true){
			Socket connectionSocket = welcomeSocket.accept();
			try{
				Worker worker = new Worker(connectionSocket,backupId);
				worker.run();
			}
			catch(Exception e){
				
			}
		}
	}

	public static void main(String[] args){
		try{
			Server server = new Server(1122);
			server.init();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
	
}
