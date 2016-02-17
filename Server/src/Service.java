import java.util.*;
import java.io.*;

public class Service {

	public static int sum(int x, int y) {
		return x + y;
	}

	public static long product(long x, long y) {
		return x * y;
	}
	
	public static double sqrt(double x) {
		double s = x/2;

		for (int i = 0; i < 50; ++i) {
			s = 0.5*(s + x/s);
		}
		return s;
	}

	public static int length(String s) {
		return s.length();
	}

	public static void doNothing() {
		return;
	}
}
