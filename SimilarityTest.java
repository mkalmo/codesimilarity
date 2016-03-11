package com.googlecode.ounit.codesimilarity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SimilarityTest {

	private static final int NGRAM_SIZE = 5;
	private static final int WINDOW_SIZE = 4;

	private static final String PATH = "D:\\Users\\Urmas Hoogma\\workspace\\EIK\\I231_algo_andmed\\src\\com\\googlecode\\ounit\\codesimilarity\\testcode\\";

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

		double jcc = Similarity.JaccardCoefficient(a, b, NGRAM_SIZE,
				WINDOW_SIZE);
		System.out.println(jcc);

		try {
			/* Test1.txt - original */
			String simpleSource1 = new String(Files.readAllBytes(Paths.get(PATH
					+ "Test1.txt")));

			/*
			 * Test2.txt - simple plagiarism - variable names changed,otherwise
			 * identical to Test1
			 */
			String simpleSource2 = new String(Files.readAllBytes(Paths.get(PATH
					+ "Test2.txt")));

			/*
			 * Test3.txt - for-loops refactored into while-loops, otherwise
			 * identical to Test1
			 */
			String simpleSource3 = new String(Files.readAllBytes(Paths.get(PATH
					+ "Test3.txt")));

			/* Test4.txt - code that is completely different from other three */
			String simpleSource4 = new String(Files.readAllBytes(Paths.get(PATH
					+ "Test4.txt")));

			System.out.println("");
			double jccSimple1 = Similarity.JaccardCoefficient(simpleSource1,
					simpleSource2, NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccSimple1
					+ " First and second: high similarity");
			double jccSimple2 = Similarity.JaccardCoefficient(simpleSource1,
					simpleSource3, NGRAM_SIZE, WINDOW_SIZE);
			System.out
					.println(jccSimple2
							+ " First and third: lower similarity because of refactoring loops");
			double jccSimple3 = Similarity.JaccardCoefficient(simpleSource2,
					simpleSource3, NGRAM_SIZE, WINDOW_SIZE);
			System.out
					.println(jccSimple3
							+ " Second and third: identical result to comparison of 1 and 3 -> Fluke?");
			System.out.println("");

			double jccControl1 = Similarity.JaccardCoefficient(simpleSource1,
					simpleSource4, NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccControl1 + " First and fourth");
			double jccControl2 = Similarity.JaccardCoefficient(simpleSource2,
					simpleSource4, NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccControl2 + " Second and fourth");
			double jccControl3 = Similarity.JaccardCoefficient(simpleSource3,
					simpleSource4, NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccControl3 + " Third and fourth");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
