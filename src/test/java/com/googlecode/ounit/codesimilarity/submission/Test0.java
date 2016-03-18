package com.googlecode.ounit.codesimilarity.submission;

import java.util.ArrayList.*;
import java.util.ArrayList;

public class Test0 {
	public static void main(String[] args) {
		int n = 10, ptr;
		int[] a = new int[n];
		int i, sum;
		System.out.println("");
		System.out.printf("", n);
		if (n > 0) {
			for (i = 0; i < n; i++) {
				System.out.printf("");
				System.out.printf("", a[i]);
			}
			ptr = a[0];
			for (i = 0, sum = 0; i < n; i++)
				sum += (ptr + i);
			System.out.printf("", sum / n);
		} else
			System.out.printf("");
	}
}
