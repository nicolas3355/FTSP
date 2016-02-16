import java.util.*;
import java.io.*;

public class Client {
	public static void main(String[] args) throws Throwable {
		FaultTolerantServiceProvider ftsp = new FaultTolerantServiceProvider("192.168.2.8");
		Integer i = (Integer)(ftsp.call("TestLib", "sum", 2, 2));
		System.out.println(i);
	}
}