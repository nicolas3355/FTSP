import java.util.*;
import java.io.*;

public class SieveClient {
	private static final int N = 1000;
	private static boolean[] composite = null;
	private static FaultTolerantServiceProvider ftsp = null;

	public static void main(String[] args) throws Throwable {
		ftsp = new FaultTolerantServiceProvider("127.0.0.1");

		// Run the sieve here, but we'll use the 
		// sum service to take care of our needs.
		long startTime = System.nanoTime();
		composite = new boolean[N + 1];
		composite[0] = true;
		composite[1] = true;
		int i = 2;
		while (i <= N) {
			if (!composite[i]) {
				int j = (int)ftsp.call("sum", i, i);
				while (j <= N) {
					composite[j] = true;
					j = (int)ftsp.call("sum", j, i);
				}
			}
			i = (int)ftsp.call("sum", i, 1);
		}
		long endTime = System.nanoTime();
		double duration = (endTime - startTime) / 1000000.0;
		for (int k = 0; k <= N; ++k) {
			if (!composite[k])
				System.out.printf("%5d", k);
		}
		System.out.printf("\n");
		System.out.printf("Sieving using the sum service up to %d took %.2f milliseconds.\n", N, duration);

		// Run the sieve on the server instead.
		startTime = System.nanoTime();
		boolean[] composite2 = (boolean[])(ftsp.call("sieve", N));
		endTime = System.nanoTime();
		duration = (endTime - startTime) / 1000000.0;

		// Assert that the arrays match.
		for (int k = 0; k <= N; ++k) {
			assert composite[k] == composite2[k];
		}

		System.out.printf("Using sieve service up to %d took %.2f milliseconds.\n", N, duration);
	}
}