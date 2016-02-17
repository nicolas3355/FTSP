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
	
	// Constructs a Fault Tolerant Service Provider 
	// using an IP Address for the main server.
	public FaultTolerantServiceProvider(String ipAddress) {
		this.mainIPAddress = ipAddress;
		this.backupIPAddress = this.mainIPAddress;
	}

	// Forwards a service request to the active server.
	public Pair<Integer, Object> forward(String ip, String methodName, Serializable... params)
		throws UnknownHostException, IOException, InvalidServiceException
	{
		Socket socket = new Socket(ip, PORT_NUMBER);

		// Read the Backup IP Address from the server.
		ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
		this.backupIPAddress = inStream.readUTF();
		
		// Send the request information to the server.
		ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
		outStream.writeInt(params.length);
		outStream.writeUTF(methodName);
		for (Serializable o : params) {
			outStream.writeObject(o);
		}
		outStream.flush();

		// Wait for server response, and get 
		// the status of the service, along 
		// with the result.
		Integer status = inStream.readInt();
		Object result = null;

		try {
			result = inStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new InvalidServiceException("Result Type Invalid.");
		}

		// Close the streams.
		inStream.close();
		outStream.close();

		return new Pair<Integer,Object>(status, result);
	}

	// Provides a service for the client given 
	// a service method name, and a sequence 
	// of parameters for the service.
	public Object call(String methodName, Serializable... params)
		throws InvalidServiceException, InvalidParameterException, Throwable
	{
		Pair<Integer, Object> result = null;

		// Forward the request to the main server.
		// If something goes wrong, we will reforward 
		// this request to the backup server, and assume 
		// that everything will go smoothly for the 
		// purpose of this assignment.
		try {
			result = forward(mainIPAddress, methodName, params);
		} catch (IOException e) {
			result = forward(backupIPAddress, methodName, params);
		}

		// If the job was successful, we return the result.
		// Otherwise, we throw exceptions appropriately.
		switch (result.first) {
			case Status.SUCCESS:
				return result.second;
			case Status.PROVIDER_FAILURE:
				// This will map the exceptions related to reflection 
				// to our exceptions because we wish to hide the 
				// implementation details of this service provider.
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
			case Status.SERVICE_FAILURE:
				// The service itself threw an exception.
				throw ((Exception)result.second).getCause();
		}
		
		return null;
	}
}