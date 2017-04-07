package ch.obermuhlner.util;

import java.util.Objects;

public class Tuple2<T1, T2> {

	private final T1 value1;
	private final T2 value2;

	private Tuple2(T1 value1, T2 value2) {
		this.value1 = value1;
		this.value2 = value2;
	}
	
	public T1 getValue1() {
		return value1;
	}
	
	public T2 getValue2() {
		return value2;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value1, value2);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Tuple2)) {
			return false;
		}
		
		Tuple2<?, ?> other = (Tuple2<?, ?>) obj;
		
		return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
	}
	
	@Override
	public String toString() {
		return "(" + value1 + ", " + value2 + ")";
	}
	
	public static <T1, T2> Tuple2<T1, T2> of(T1 value1, T2 value2) {
		return new Tuple2<>(value1, value2);
	}
}
