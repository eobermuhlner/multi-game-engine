package ch.obermuhlner.util;

import java.util.function.Supplier;

public class CheckArgument {

	public static void isTrue(boolean condition, String message) {
		if (!condition) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isTrue(boolean condition, Supplier<String> messageSupplier) {
		if (!condition) {
			throw new IllegalArgumentException(messageSupplier.get());
		}
	}

	public static void isNull(Object value, String message) {
		if (value != null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isNull(Object value, Supplier<String> messageSupplier) {
		if (value != null) {
			throw new IllegalArgumentException(messageSupplier.get());
		}
	}
}
