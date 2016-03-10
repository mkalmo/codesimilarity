package com.googlecode.ounit.codesimilarity;

import java.util.ArrayList;
import java.util.List;

public class RabinKarpTest {

	/*
	 * pattern: abracadabra text: abacadabrabracabracadabrabrabracad match:
	 * abracadabra
	 * 
	 * % java RabinKarp rab abacadabrabracabracadabrabrabracad pattern: rab
	 * text: abacadabrabracabracadabrabrabracad match: rab
	 * 
	 * % java RabinKarp bcara abacadabrabracabracadabrabrabracad pattern: bcara
	 * text: abacadabrabracabracadabrabrabracad
	 * 
	 * % java RabinKarp rabrabracad abacadabrabracabracadabrabrabracad text:
	 * abacadabrabracabracadabrabrabracad pattern: rabrabracad
	 * 
	 * % java RabinKarp abacad abacadabrabracabracadabrabrabracad text:
	 * abacadabrabracabracadabrabrabracad pattern: abacad
	 */
	public static void main(String[] args) {

		RabinKarp.test("abracadabra", "abacadabrabracabracadabrabrabracad");
		RabinKarp.test("rab", "abacadabrabracabracadabrabrabracad");
		RabinKarp.test("bcara", "abacadabrabracabracadabrabrabracad");
		RabinKarp.test("rabrabracad", "abacadabrabracabracadabrabrabracad");
		RabinKarp.test("abacad", "abacadabrabracabracadabrabrabracad");
		
		
	}

	public static long hash(String string) {
		long h = 1125899906842597L; // prime
		int len = string.length();

		for (int i = 0; i < len; i++) {
			h = 31 * h + string.charAt(i);
		}
		return h;
	}

	public static List<Integer> modHashes(int[] hashes, int mod) {
		List<Integer> suitable = new ArrayList<Integer>();
		for (int i = 0; i < hashes.length; i++) {
			if (hashes[i] % mod == 0) {
				suitable.add(hashes[i]);
				System.out.println(hashes[i]);
			}
		}
		return suitable;
	}

	public static int[] generateHashes2(String[] input) {
		int[] res = new int[input.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = (input[i].hashCode());
			System.out.println(res[i]);
		}
		return res;
	}
}
