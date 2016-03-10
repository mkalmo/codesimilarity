package com.googlecode.ounit.codesimilarity;

import java.util.List;

public class SimilarityTest {

    private static final int NGRAM_SIZE = 5;
	private static final int WINDOW_SIZE = 4;

	public static void main(String[] args) {

		String a = "adorunrunrunadorunrun";
		String b = "adorunrungetadorunrun";
		String c = "lhgähgfghdfghdfgdfähg";

		List<Integer> suit1 = Similarity.generateAllHashes(a, NGRAM_SIZE);
		List<Integer> suit2 = Similarity.generateAllHashes(b, NGRAM_SIZE);
		List<Integer> suit3 = Similarity.generateAllHashes(c, NGRAM_SIZE);
		
		System.out.println("similar " + Similarity.similarHashes(suit1, suit2));
		System.out.println("similar " + Similarity.similarHashes(suit2, suit3));
		System.out.println("similar " + Similarity.similarHashes(suit3, suit1));

		// int[] hashList = new int[] { 77, 74, 42, 17, 98, 50, 17, 98, 8, 88, 67, 39, 77, 74, 42, 17, 98 };

		double jcc = Similarity.JaccardCoefficient(a, b, NGRAM_SIZE, WINDOW_SIZE);
		System.out.println(jcc);
	}
}
