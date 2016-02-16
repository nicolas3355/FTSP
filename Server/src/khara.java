import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
	
	public Server() {

	}

	public Object handleRequest() {
		// handle the TCP request.
		// create a new thread and throw 
		// the new job onto the thread.
		
		// This is what the server should do in principle:
		/*
		ArrayList<Class> paramTypes = new ArrayList<Class>();
		for (Object o : params) {
			paramTypes.add(o.getClass());
		}

		try {
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(methodName, paramTypes);
			return m.invoke(null, params);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;*/
	}
}