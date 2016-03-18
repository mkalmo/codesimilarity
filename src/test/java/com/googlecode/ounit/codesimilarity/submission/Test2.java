package com.googlecode.ounit.codesimilarity.submission;

import java.util.ArrayList.*;
import java.util.ArrayList;

public class Test2 {
	public static void main(String[] args) {
		int n = 10, ptr;
		int[] a = new int[n];
		int i = 0, sum;
		System.out.println("");
		System.out.printf("%d", n);
		if (n >= 1) {
			while (i < n) {
				System.out.println("");
				System.out.printf("%d", a[i]);
				i++;
			}
			ptr = a[0];
			i = 0;
			sum = 0;
			while (i < n) {
				sum += (ptr + i);
				i++;
			}
			System.out.printf("%d\n", sum / n);
		} else
			System.out.println("");
	}
}
