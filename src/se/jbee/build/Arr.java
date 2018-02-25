package se.jbee.build;

import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOf;

import java.lang.reflect.Array;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Arr {

	public static <T> boolean any(T[] a, Predicate<T> accept) {
		for (int i = 0; i < a.length; i++)
			if (accept.test(a[i]))
				return true;
		return false;
	}

	public static <T> boolean all(T[] a, Predicate<T> accept) {
		for (int i = 0; i < a.length; i++)
			if (!accept.test(a[i]))
				return false;
		return true;
	}

	public static <T, E extends RuntimeException> T first(T[] a, Predicate<T> accept, E elseThrow) throws E {
		for (T e : a)
			if (accept.test(e))
				return e;
		throw elseThrow;
	}

	public static <T> T[] filter(T[] a, Predicate<T> accept) {
		T[] res = a.clone();
		int i = 0;
		for (T e : a)
			if (accept.test(e))
				res[i++] = e;
		return i == a.length ? a : copyOf(res, i);
	}

	public static <T> T[] concat(T[] a, T[] b) {
		if (a.length == 0)
			return b;
		if (b.length == 0)
			return a;
		T[] res = copyOf(a, a.length + b.length);
		arraycopy(b, 0, res, a.length, b.length);
		return res;
	}

	/*
	 * Arrays as sets
	 */

	public static <T> T[] union(T[] a, T[] b, BiPredicate<T, T> eq) {
		if (b.length == 0)
			return a;
		if (a.length == 0)
			return b;
		T[] res = copyOf(a, a.length + b.length);
		int j = a.length;
		for (T eb : b) {
			if (!any(a, ea -> eq.test(ea, eb)))
				res[j++] = eb;
		}
		if (j < res.length)
			res = copyOf(res, j);
		return res;
	}

	public static <T> T[] subtract(T[] a, T[] b, BiPredicate<T, T> eq) {
		if (b.length == 0 || a.length == 0)
			return a;
		return filter(a, ea -> !any(b, eb -> eq.test(ea, eb)));
	}

	public static <T> T[] add(T[] a, T e, BiPredicate<T, T> eq) {
		if (any(a, ea -> eq.test(ea, e)))
			return a;
		T[] res = copyOf(a, a.length + 1);
		res[a.length] = e;
		return res;
	}

	/*
	 * Transformation
	 */

	public static <A, B> B[] map(A[] a, Function<A, B> f) {
		if (a.length == 0)
			throw new IllegalArgumentException("map can only be used with non-empty arrays.");
		B b0 = f.apply(a[0]);
		@SuppressWarnings("unchecked")
		B[] b = (B[]) Array.newInstance(b0.getClass(), a.length);
		b[0] = b0;
		for (int i = 1; i < a.length; i++)
			b[i] = f.apply(a[i]);
		return b;
	}

	public static <A,B> B flatMap(A[] a, BiFunction<A, B, B> f, B init) {
		if (a.length == 0)
			return init;
		B res = init;
		for (A e : a)
			res = f.apply(e, res);
		return res;
	}

	public static <A> String toString(A[] a, String delimiter) {
		if (a.length == 0)
			return "";
		StringBuilder res = flatMap(a, (e, b) -> b.append(e.toString()).append(delimiter), new StringBuilder());
		res.setLength(res.length() - delimiter.length());
		return res.toString();
	}
}
