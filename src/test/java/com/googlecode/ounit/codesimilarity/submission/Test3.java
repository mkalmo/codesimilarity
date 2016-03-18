package com.googlecode.ounit.codesimilarity.submission;

public class Test3 {

	public static void main(String[] args) {
		System.out
				.println("Input a number to count until that number (including) and sum results");
		String n = args[0];
		int sum = 0;
		int n2 = Integer.parseInt(n);
		for (int i = 0; i <= n2; i++) {
			System.out.println(i);
			sum += i;
		}
		System.out.println(sum);
	}
}
