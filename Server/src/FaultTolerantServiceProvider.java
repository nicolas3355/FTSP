import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

public class FaultTolerantServiceProvider {
	private static final int PORT_NUMBER = 1122;

	private String mainIPAddress;
	private String backupIPAddress;
	private boolean usingBackup;

	// Constructs a Fault Tolerant Service Provider 
	// using an IP Address for the main server.
	public FaultTolerantServiceProvider(String ipAddress) throws UnknownHostException, IOException {
		this.mainIPAddress = ipAddress;
		this.usingBackup = false;

		// Read the backup server IP from the main server.
		Socket socket = new Socket(mainIPAddress, PORT_NUMBER);
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		this.backupIPAddress = in.readUTF();
	}

	// Returns the IP Address of the server we're currently
	// going to use during the call routine execution. If 
	// everything is going fine, this will return the main 
	// server IP address. If the main server fails, the 
	// backup IP address will be returned.
	public String getActiveIP() {
		return usingBackup? backupIPAddress : mainIPAddress;
	}

	// Forwards a service request to the active server.
	public Pair<Integer, Object> forward(String className, String methodName, Serializable... params) throws UnknownHostException, IOException, ClassNotFoundException {
		Socket socket = new Socket(getActiveIP(), PORT_NUMBER);
		ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
		
		// The number of objects we're going to stream 
		// to the current server to handle the request.
		Integer count = params.length + 2;

		// Send the request information to the server.
		outStream.writeObject(count);
		outStream.writeObject(className);
		outStream.writeObject(methodName);
		for (Serializable o : params) {
			outStream.writeObject(o);
		}

		// Block and wait for results from server.
		ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
		int status = inStream.readInt();
		
		Object result = inStream.readObject();

		return new Pair<Integer,Object>(status, result);
	}

	public Object call(String className, String methodName, Serializable... params) throws Throwable {
		Pair<Integer, Object> result = null;
		try {
			result = forward(className, methodName, params);
		} catch (IOException e) {
			usingBackup = true;
			result = forward(className, methodName, params);
			usingBackup = false;
		}

		// If the job was successful, we return the result.
		// Otherwise, we throw exceptions appropriately.
		switch (result.first) {
			case 0:
				return result.second;
			case 1:
				if (result.second instanceof ClassNotFoundException) {
					throw new InvalidServiceException("Invalid service class name.");
				} else if (result.second instanceof SecurityException) {
					throw new InvalidServiceException("Security violation.");
				} else if (result.second instanceof NoSuchMethodException) {
					throw new InvalidServiceException("Invalid service method name.");
				} else if (result.second instanceof IllegalAccessException) {
					throw new InvalidServiceException("Cannot access service.");
				} else if (result.second instanceof IllegalArgumentException) {
					throw new InvalidParameterException("Bad parameters given to service.");
				} else {
					((Exception)(result.second)).printStackTrace();
				}
			case 2:
				throw ((Exception)result.second).getCause();
		}
		
		return null;
	}
}