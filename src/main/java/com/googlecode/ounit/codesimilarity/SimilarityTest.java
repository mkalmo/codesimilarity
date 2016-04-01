package com.googlecode.ounit.codesimilarity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Urmas Hoogma
 * */
public class SimilarityTest {

	private static final int NGRAM_SIZE = 12;
	private static final int WINDOW_SIZE = 8;
	private static final String MULTIPLE_WHITESPACE_PATTERN = "(\\s+)|(\\t+)|(\\n+)";
	private static final String FS = System.getProperty("file.separator");

	private static final String PATH = "D:\\Users\\Urmas Hoogma\\workspace\\codesimilarity\\src\\test\\java\\com\\googlecode\\ounit\\codesimilarity\\assignment";
	private static final String PATHQ = "D:\\Users\\Urmas Hoogma\\workspace\\codesimilarity\\src\\test\\java\\com\\googlecode\\ounit\\codesimilarity\\quaternion";
	private static final String PATH_REAL_DATA = "C:\\Users\\Urmas Hoogma\\Desktop\\treenode-res";

	static Map<Pair, Double> comparisonResults = new HashMap<Pair, Double>();

	public static void main(String[] args) {
		// bulkTest();
		
		SimilarityRunner sim = new SimilarityRunner("TreeNode.java", 10,
				21);
		sim.test(PATH_REAL_DATA + FS);
		System.out.println(sim.difference);
		
		SimilarityRunner sim2 = new SimilarityRunner("TreeNode.java", 5,
				26);
		sim2.test(PATH_REAL_DATA + FS);
		System.out.println(sim2.difference);
	}

	private static void bulkTest() {
		for (int k = 1; k <= 50; k++) {
			for (int t = 1 + k; t <= k + 30; t++) {
				int w = t - k + 1;
				SimilarityRunner sim2 = new SimilarityRunner("TreeNode.java", k,
						w);
				sim2.test(PATH_REAL_DATA + FS);
				comparisonResults.put(new Pair(k, t), sim2.difference);
			}
		}
		comparisonResults = MapUtil.sortByValue(comparisonResults);
		System.out.println(comparisonResults.toString());
	}

	public static String replaceMultipleWhiteSpace(String input) {
		Pattern whitespace = Pattern.compile(MULTIPLE_WHITESPACE_PATTERN);
		Matcher matcher = whitespace.matcher(input);
		String result = matcher.replaceAll(" ");
		return result;
	}

	public static void stringsTest() {
		String a = "adorunrunrunadorunrun";
		String b = "adorunrungetadorunrun";
		String c = "lhg�hgfghdfghdfgdf�hg";

		List<Integer> suit1 = Similarity.generateAllHashes(a, NGRAM_SIZE);
		List<Integer> suit2 = Similarity.generateAllHashes(b, NGRAM_SIZE);
		List<Integer> suit3 = Similarity.generateAllHashes(c, NGRAM_SIZE);

		System.out.println("similar " + Similarity.similarHashes(suit1, suit1));
		System.out.println("similar " + Similarity.similarHashes(suit1, suit2));
		System.out.println("similar " + Similarity.similarHashes(suit2, suit3));
		System.out.println("similar " + Similarity.similarHashes(suit3, suit1));

		double jcc = Similarity.JaccardCoefficient(a, a, null, NGRAM_SIZE,
				WINDOW_SIZE);
		System.out.println(jcc);
	}

	public static void simpleTest() {
		try {
			/* Test1.txt - original */
			String simpleSource0 = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(PATH + FS + "students" + FS
							+ "0" + FS + "Test.txt"))));

			/*
			 * Test2.txt - simple plagiarism - variable names changed,otherwise
			 * identical to Test1.txt
			 */
			String simpleSource1 = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(PATH + FS + "students" + FS
							+ "1" + FS + "Test.txt"))));
			/*
			 * Test3.txt - for-loops refactored into while-loops, otherwise
			 * identical to Test1.txt
			 */
			String simpleSource2 = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(PATH + FS + "students" + FS
							+ "2" + FS + "Test.txt"))));
			/* Test4.txt - code that is completely different from other three */
			String simpleSource3 = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(PATH + FS + "students" + FS
							+ "3" + FS + "Test.txt"))));
			String boilerplate = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(PATH + FS + "teacher" + FS
							+ "Test.txt"))));
			System.out.println("");
			System.out.println("Printing testdata:");
			System.out.println("First:  " + simpleSource0);
			System.out.println("Second: " + simpleSource1);
			System.out.println("Third:  " + simpleSource2);
			System.out.println("Fourth: " + simpleSource3);

			System.out.println("");
			double jccSimple1 = Similarity.JaccardCoefficient(simpleSource0,
					simpleSource1, boilerplate, NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccSimple1
					+ " First and second: high similarity");
			double jccSimple2 = Similarity.JaccardCoefficient(simpleSource0,
					simpleSource2, boilerplate, NGRAM_SIZE, WINDOW_SIZE);
			System.out
					.println(jccSimple2
							+ " First and third: lower similarity because of refactoring loops");
			double jccSimple3 = Similarity.JaccardCoefficient(simpleSource1,
					simpleSource2, boilerplate, NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccSimple3
					+ " Second and third: highest similarity");
			System.out.println("");

			double jccControl1 = Similarity.JaccardCoefficient(simpleSource0,
					simpleSource3, boilerplate, NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccControl1 + " First and fourth");
			double jccControl2 = Similarity.JaccardCoefficient(simpleSource1,
					simpleSource3, boilerplate, NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccControl2 + " Second and fourth");
			double jccControl3 = Similarity.JaccardCoefficient(simpleSource2,
					simpleSource3, boilerplate, NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccControl3 + " Third and fourth");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void quaternionTest() {
		try {
			String q0 = new String(Files.readAllBytes(Paths.get(PATHQ + FS
					+ "students" + FS + "0" + FS + "Test.txt")));

			String q1 = new String(Files.readAllBytes(Paths.get(PATHQ + FS
					+ "students" + FS + "1" + FS + "Test.txt")));

			String qBoilerplate = new String(Files.readAllBytes(Paths.get(PATHQ
					+ FS + "teacher" + FS + "Test.txt")));

			double qTest1 = Similarity.JaccardCoefficient(q0, q1, qBoilerplate,
					NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(qTest1 + " Quaternion");
			/*
			 * List<Integer> q0l = Similarity.generateAllHashes(q0, NGRAM_SIZE);
			 * List<Integer> q1l = Similarity.generateAllHashes(q1, NGRAM_SIZE);
			 * System.out.println("similar " + Similarity.similarHashes(q0l,
			 * q1l));
			 */
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
