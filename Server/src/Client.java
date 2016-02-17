import java.util.*;
import java.io.*;

public class Client {
	public static void main(String[] args) throws Throwable {
		FaultTolerantServiceProvider ftsp = new FaultTolerantServiceProvider("127.0.0.1");

		// Benchmark request speed.
		long startTime = System.nanoTime();
		for (int i = 0; i < 1000; ++i) {
			ftsp.call("doNothing");
		}
		long endTime = System.nanoTime();
		double duration = (endTime - startTime) / 1000000.0;
		System.out.printf("Took %.2f milliseconds to run 1000 requests.\n", duration);

		// Test different service calls.
		Object o1 = ftsp.call("sum", 3, 5);
		Object o2 = ftsp.call("sqrt", 2.0);
		Object o3 = ftsp.call("length", "Hello World");

		// Output results.
		System.out.printf("3 + 5 = %d\n", (int)(o1));
		System.out.printf("sqrt(2) = %.2f\n", (double)(o2));
		System.out.printf("len('Hello World') = %d\n", (int)(o3));
	}
}
