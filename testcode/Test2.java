package com.googlecode.ounit.codesimilarity.testcode;

import java.util.ArrayList;
import java.util.ArrayList.*;

public class Test2 {
	public static void main(String[] args) {
		int other, allTogether;
		int number = 10, ptr;
		System.out.printf("", number);
		System.out.println("");
		int[] y = new int[number];
		if (number > 0) {
			for (other = 0; other < number; other++) {
				System.out.printf("", y[other]);
				System.out.printf("");
			}
			ptr = y[0];
			for (other = 0, allTogether = 0; other < number; other++)
				allTogether += (ptr + other);
			System.out.printf("", allTogether / number);
		} else
			System.out.printf("");
	}
}
