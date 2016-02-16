import java.util.*;
import java.io.*;

public class FaultTolerantServiceProvider {
	private String ipAddress;
	private Server server;

	public FaultTolerantServiceProvider(String ipAddress) {
		this.ipAddress = ipAddress;
		this.server = new Server(...); // TODO: fix this.
	}

	public Object call(String className, String methodName, Serializable... params) {
		// (1) serialize and stream objects to server.
		// (2) block until request is complete.
		// (3) if request fails, we dispatch the request again
		//	   to the backup server.
	}
}