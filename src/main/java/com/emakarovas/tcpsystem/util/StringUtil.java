package com.emakarovas.tcpsystem.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains utility methods dealing with Strings.
 * @author Edgaras Makarovas
 *
 */
public final class StringUtil {
	
	private StringUtil() {}
	
	/**
	 * Converts a String containing Integers separated by commas into a {@link List} of Integers.
	 * @param str
	 * @return
	 */
	public static List<Integer> convertCommaSeparatedStringToIntegerList(String str) {
		final String[] separated = str.split(",");
		return Arrays.asList(separated).stream().map(num -> Integer.parseInt(num)).collect(Collectors.toList());
	}

}
