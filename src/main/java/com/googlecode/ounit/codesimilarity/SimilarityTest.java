package com.googlecode.ounit.codesimilarity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to test similarity and winnowing. No actual testing frameworks are
 * implemented because it is overly difficult to calculate the expected results.
 * Instead injection of known plagiarized code or code that is plagiarized by
 * developer should be used.
 * 
 * @author Urmas Hoogma
 * */
public class SimilarityTest {

	private static final int NGRAM_SIZE = 12;
	private static final int WINDOW_SIZE = 8;
	private static final String MULTIPLE_WHITESPACE_PATTERN = "(\\s+)|(\\t+)|(\\n+)";
	private static final String FS = System.getProperty("file.separator");

	private static final String ASSIGNMENT_PATH = "src" + FS + "test" + FS
			+ "java" + FS + "com" + FS + "googlecode" + FS + "ounit" + FS
			+ "codesimilarity" + FS + "assignment";
	private static final String QUATERNION_ASSIGNEMENT_PATH = "src" + FS
			+ "test" + FS + "java" + FS + "com" + FS + "googlecode" + FS
			+ "ounit" + FS + "codesimilarity" + FS + "quaternion";
	/**
	 * hardcoded paths with real data that are not included in repository (for
	 * obvious reasons)
	 */
	private static final String MAIN_FOLDER = "C:\\Users\\Urmas Hoogma\\Desktop\\data\\";
	private static final String REAL_DATA_PATH = "C:\\Users\\Urmas Hoogma\\Desktop\\data\\treenode-res\\";
	private static final String REAL_DATA_PATH_VER2 = "C:\\Users\\Urmas Hoogma\\Desktop\\data\\treenode-2-res\\";
	private static final String REAL_DATA_PATH_VER3 = "C:\\Users\\Urmas Hoogma\\Desktop\\data\\GraphTask-res\\";
	private static final String REAL_DATA_PATH_VER4 = "C:\\Users\\Urmas Hoogma\\Desktop\\data\\Puzzle-res\\";

	private static Map<Pair, Double> comparisonResults = new HashMap<Pair, Double>();

	public static void main(String[] args) throws IOException {
		// stringsTest();
		// jaccardCoefficientTest();
		// simpleTest();
		// bulkTest();
		// test1();
		// test2();
		test3();
		// emptyTest();
	}

	/**
	 * Test on large amount of real data
	 * */
	private static void test3() {
		long start = System.currentTimeMillis();
		int ngramsize = 13;
		int windowsize = 27;
		double similarityThreshold = 0;
		int modulus = 1;
		SimilarityRunnerAdvanced sim = new SimilarityRunnerAdvanced(
				"GraphTask.java", ngramsize, windowsize, similarityThreshold,
				modulus);
		String csvResult = sim.run(REAL_DATA_PATH_VER3);
		try (PrintWriter out = new PrintWriter(
				"C:\\Users\\Urmas Hoogma\\Desktop\\result.csv")) {
			out.println(csvResult);
			long elapsedTimeMillis = System.currentTimeMillis() - start;
			System.out.println("SimilarityRunnerAdvanced runtime: "
					+ elapsedTimeMillis / 1000F + " sec");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Testing a combination of t and k values derived with help of bulkTest()
	 * */
	private static void test2() {
		SimilarityRunner sim = new SimilarityRunner("TreeNode.java", 5, 26, 1);
		sim.test(REAL_DATA_PATH);
		System.out.println(sim.difference);
	}

	/**
	 * Testing a combination of t and k values derived with help of bulkTest()
	 * */
	private static void test1() {
		SimilarityRunner sim = new SimilarityRunner("TreeNode.java", 13, 39, 1);
		sim.test(REAL_DATA_PATH);
		System.out.println(sim.difference);
	}

	/**
	 * Testing whether an empty file gives an error
	 * */
	private static void emptyTest() {
		try {
			String q0 = new String(Files.readAllBytes(Paths.get(MAIN_FOLDER
					+ "Empty.java")));

			String q1 = new String(Files.readAllBytes(Paths.get(MAIN_FOLDER
					+ "Empty.java")));

			String qBoilerplate = new String(Files.readAllBytes(Paths
					.get(MAIN_FOLDER + "Empty.java")));

			double qTest1 = Similarity.JaccardCoefficient(q0, q1, qBoilerplate,
					13, 39, 1);
			System.out.println(qTest1 + " Empty");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Testing all reasonable k and t values to find combinations of t and k
	 * that give maximal difference between original and plagiarized works. In
	 * order to function properly test data for this test must hold only one
	 * pair of plagiarized work
	 * */
	private static void bulkTest() {
		for (int k = 1; k <= 50; k++) {
			for (int t = 1 + k; t <= k + 30; t++) {
				int w = t - k + 1;
				SimilarityRunner sim2 = new SimilarityRunner("TreeNode.java",
						k, w, 1);
				sim2.test(REAL_DATA_PATH);
				comparisonResults.put(new Pair(k, t), sim2.difference);
			}
		}
		comparisonResults = MapUtil.sortByValue(comparisonResults);
		System.out.println(comparisonResults.toString());
	}

	/**
	 * Replacing multiple whitespace contributes to detection of similarity. Not
	 * necessary if input files are generated with
	 * https://github.com/uhoogma/codesimplifier as it leaves no whitespace in
	 * simplified files
	 * */
	public static String replaceMultipleWhiteSpace(String input) {
		Pattern whitespace = Pattern.compile(MULTIPLE_WHITESPACE_PATTERN);
		Matcher matcher = whitespace.matcher(input);
		String result = matcher.replaceAll(" ");
		return result;
	}

	/**
	 * Test to determine whether calculating Jaccard coefficient functions on
	 * simple strings
	 * */
	public static void stringsTest() {
		String a = "adorunrunrunadorunrun";
		String b = "adorunrungetadorunrun";
		String c = "lhg�hgfghdfghdfgdf�hg";

		List<Integer> suit1 = Similarity.generateHashes(a, NGRAM_SIZE, 1);
		List<Integer> suit2 = Similarity.generateHashes(b, NGRAM_SIZE, 1);
		List<Integer> suit3 = Similarity.generateHashes(c, NGRAM_SIZE, 1);

		System.out.println("similar " + Similarity.similarHashes(suit1, suit1));
		System.out.println("similar " + Similarity.similarHashes(suit1, suit2));
		System.out.println("similar " + Similarity.similarHashes(suit2, suit3));
		System.out.println("similar " + Similarity.similarHashes(suit3, suit1));

		double jcc = Similarity.JaccardCoefficient(a, a, null, NGRAM_SIZE,
				WINDOW_SIZE, 1);
		System.out.println(jcc);
	}

	/**
	 * Test to determine whether calculating Jaccard coefficient functions
	 * properly in case of 4 input files
	 * */
	public static void simpleTest() {
		try {
			/* Test1.txt - original */
			String simpleSource0 = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(ASSIGNMENT_PATH + FS
							+ "students" + FS + "0" + FS + "Test.txt"))));

			/*
			 * Test2.txt - simple plagiarism - variable names changed,otherwise
			 * identical to Test1.txt
			 */
			String simpleSource1 = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(ASSIGNMENT_PATH + FS
							+ "students" + FS + "1" + FS + "Test.txt"))));
			/*
			 * Test3.txt - for-loops refactored into while-loops, otherwise
			 * identical to Test1.txt
			 */
			String simpleSource2 = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(ASSIGNMENT_PATH + FS
							+ "students" + FS + "2" + FS + "Test.txt"))));
			/* Test4.txt - code that is completely different from other three */
			String simpleSource3 = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(ASSIGNMENT_PATH + FS
							+ "students" + FS + "3" + FS + "Test.txt"))));
			String boilerplate = replaceMultipleWhiteSpace(new String(
					Files.readAllBytes(Paths.get(ASSIGNMENT_PATH + FS
							+ "teacher" + FS + "Test.txt"))));
			System.out.println("");
			System.out.println("Printing testdata:");
			System.out.println("First:  " + simpleSource0);
			System.out.println("Second: " + simpleSource1);
			System.out.println("Third:  " + simpleSource2);
			System.out.println("Fourth: " + simpleSource3);

			System.out.println("");
			double jccSimple1 = Similarity.JaccardCoefficient(simpleSource0,
					simpleSource1, boilerplate, NGRAM_SIZE, WINDOW_SIZE, 1);
			System.out.println(jccSimple1
					+ " First and second: high similarity");
			double jccSimple2 = Similarity.JaccardCoefficient(simpleSource0,
					simpleSource2, boilerplate, NGRAM_SIZE, WINDOW_SIZE, 1);
			System.out
					.println(jccSimple2
							+ " First and third: lower similarity because of refactoring loops");
			double jccSimple3 = Similarity.JaccardCoefficient(simpleSource1,
					simpleSource2, boilerplate, NGRAM_SIZE, WINDOW_SIZE, 1);
			System.out.println(jccSimple3
					+ " Second and third: highest similarity");
			System.out.println("");

			double jccControl1 = Similarity.JaccardCoefficient(simpleSource0,
					simpleSource3, boilerplate, NGRAM_SIZE, WINDOW_SIZE, 1);
			System.out.println(jccControl1 + " First and fourth");
			double jccControl2 = Similarity.JaccardCoefficient(simpleSource1,
					simpleSource3, boilerplate, NGRAM_SIZE, WINDOW_SIZE, 1);
			System.out.println(jccControl2 + " Second and fourth");
			double jccControl3 = Similarity.JaccardCoefficient(simpleSource2,
					simpleSource3, boilerplate, NGRAM_SIZE, WINDOW_SIZE, 1);
			System.out.println(jccControl3 + " Third and fourth");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test to determine whether calculating Jaccard coefficient functions
	 * properly in case of 1 pair of input files
	 * */
	public static void jaccardCoefficientTest() {
		try {
			String q0 = new String(Files.readAllBytes(Paths
					.get(QUATERNION_ASSIGNEMENT_PATH + FS + "students" + FS
							+ "0" + FS + "Test.txt")));

			String q1 = new String(Files.readAllBytes(Paths
					.get(QUATERNION_ASSIGNEMENT_PATH + FS + "students" + FS
							+ "1" + FS + "Test.txt")));

			String qBoilerplate = new String(Files.readAllBytes(Paths
					.get(QUATERNION_ASSIGNEMENT_PATH + FS + "teacher" + FS
							+ "Test.txt")));

			double qTest1 = Similarity.JaccardCoefficient(q0, q1, qBoilerplate,
					NGRAM_SIZE, WINDOW_SIZE, 1);
			System.out.println(qTest1 + " Quaternion");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
