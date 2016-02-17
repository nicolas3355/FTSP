import java.util.*;
import java.io.*;

public class Service {

	// Computes the sum of two integers.
	public static int sum(int x, int y) {
		return x + y;
	}

	// Computes the product of two integers.
	public static long product(long x, long y) {
		return x * y;
	}

	// Performs iterations of Newton-Raphson 
	// to compute the square root of a real number.
	public static double sqrt(double x) {
		double s = x/2;
		for (int i = 0; i < 50; ++i) {
			s = 0.5*(s + x/s);
		}
		return s;
	}

	// Computes the length of a string.
	public static int length(String s) {
		return s.length();
	}

	// Does nothing.
	public static void doNothing() {
		return;
	}

	// Sieves all the primes up to n.
	public static boolean[] sieve(int n) {
		boolean[] composite = new boolean[n + 1];
		if (0 <= n) composite[0] = true;
		if (1 <= n) composite[1] = true;
		for (int i = 2; i <= n; ++i) {
			if (!composite[i]) {
				for (int j = i + i; j <= n; j += i) {
					composite[j] = true;
				}
			}
		}
		return composite;
	}
}
