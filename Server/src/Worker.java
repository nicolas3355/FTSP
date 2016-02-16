import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;


public class Worker extends Thread{


	private String backupIp;
	private String className;
	private String methodName;
	private Object[] params;
	private Class[] paramsClasses;
	private Socket connectionSocket;

	public Worker(Socket connectionSocket,String backupIp) throws IOException {
		this.connectionSocket = connectionSocket;
		ObjectInputStream object = new ObjectInputStream(connectionSocket.getInputStream());


		SendObjectThroughNetwork.sendObject(connectionSocket,object,0);

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

				try {
					params[i-2] = object.readObject();
					params[i-2] = object.readObject().getClass();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					SendObjectThroughNetwork.sendObject(connectionSocket,e,1);
					e.printStackTrace();
				}
				break;
			}

		}
	}


	public void run(){

		try{
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(methodName, paramsClasses);
			Object result = m.invoke(null, params);
			SendObjectThroughNetwork.sendObject(connectionSocket,result,0);
		}
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			SendObjectThroughNetwork.sendObject(connectionSocket,e,2);
			e.printStackTrace();
		}
		catch (Exception e){
			SendObjectThroughNetwork.sendObject(connectionSocket,e,1);
			e.printStackTrace();
		}			
	}
}
