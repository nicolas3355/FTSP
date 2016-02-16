import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class SendObjectThroughNetwork {

	
	public static void sendObject(Socket connection,Object obj,int status){
		try{
			ObjectOutputStream outputObj = new ObjectOutputStream(connection.getOutputStream());
			Integer statusObj = new Integer(status);
			
			outputObj.writeObject(statusObj);
			outputObj.writeObject(obj);
			}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
