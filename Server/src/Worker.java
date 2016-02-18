import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class Worker extends Thread {
	
	// Service Request Information.
	private String methodName;
	private Object[] params;
	private Class[] paramClasses;

	// Socket streams.
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;

	// If this is flagged false, it means that the 
	// service cannot be handled properly.
	private boolean good;

	// This will construct a worker that will communicate 
	// with the client server via a given socket. The 
	// backup server IP will be sent to the client server.
	public Worker(Socket socket, String backupIP) throws IOException {
		// Mark the request as good by default.
		this.good = true;

		// Send the client the IP of the backup 
		// server in case anything goes wrong.
		this.outStream = new ObjectOutputStream(socket.getOutputStream());
		this.outStream.writeUTF(backupIP);
		this.outStream.flush();

		// Initialize the input stream.
		this.inStream = new ObjectInputStream(socket.getInputStream());

		// Read parameter count from client.
		int parameters = this.inStream.readInt();
		this.params       = new Object[parameters];
		this.paramClasses = new Class[parameters];
		
		// Read the service name from client.
		this.methodName    = this.inStream.readUTF();

		// In case the parameters are invalid, an 
		// exception will be thrown while collecting 
		// them. Hence, we will throw this exception 
		// back to the user.
		ClassNotFoundException throwable = null;

		// Read parameters from client.
		for (int i = 0; i < parameters; i++) {
			try {
				this.params[i] = this.inStream.readObject();
				this.paramClasses[i] = determineType(params[i]);
			} catch (ClassNotFoundException e) {
				this.good = false;
				if (throwable == null) {
					throwable = e;
				}
			}
		}

		// Send service failure information 
		// in case the request is bad.
		if (!this.good) {
			this.outStream.writeInt(Status.SERVICE_FAILURE);
			this.outStream.writeObject(throwable);
		}
	}

	// This function will determine the type of an Object
	// with priority to assigning primitive types.
	// TODO: Refactor this to be cleaner.
	public static Class determineType(Object o) {
		if (o instanceof Integer)        return int.class;
		else if (o instanceof Byte)      return byte.class;
		else if (o instanceof Character) return char.class;
		else if (o instanceof Short)     return short.class;
		else if (o instanceof Boolean)   return boolean.class;
		else if (o instanceof Long)      return long.class;
		else if (o instanceof Float)     return float.class;
		else if (o instanceof Double)    return double.class;
		else return o.getClass();
 	}

	public void run() {
		try {
			try {
				if (this.good) {
					// Invoke the service method.
					Class c = Class.forName("Service");
					Method m = c.getDeclaredMethod(this.methodName, this.paramClasses);
					Object result = m.invoke(null, this.params);

					// Send the client the result of the service.
					this.outStream.writeInt(Status.SUCCESS);
					this.outStream.writeObject(result);
				}
			} catch (InvocationTargetException e) {
				this.outStream.writeInt(Status.SERVICE_FAILURE);
				this.outStream.writeObject(e);
				e.printStackTrace();
			} catch (Exception e) {
				this.outStream.writeInt(Status.PROVIDER_FAILURE);
				this.outStream.writeObject(e);
				e.printStackTrace();
			}

			this.outStream.close();
			this.inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
