import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;


public class Worker extends Thread{

	private String className;
	private String methodName;
	private Object[] params;
	
	public Worker(Socket connectionSocket) throws IOException, ClassNotFoundException {
		ObjectInputStream object = new ObjectInputStream(connectionSocket.getInputStream());
		int parameters =object.readInt();
		
		this.params = new Object[parameters-2];
		for(int i=0;i<parameters;i++){
			object = new ObjectInputStream(connectionSocket.getInputStream());
			switch (i) {
			case 0:
				this.className = object.readUTF();
				break;

			case 1:
				this.methodName = object.readUTF();
				break;
				
			default:
				params[i-2] = object.readObject();
				break;
			}
			
		}
	}
 
	
	public void run() {
		
	}
}
