package com.googlecode.ounit.codesimilarity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author Urmas Hoogma
 * */
public class SimilarityTest {

	private static final int NGRAM_SIZE = 5;
	private static final int WINDOW_SIZE = 4;
	private static final String MULTIPLE_WHITESPACE_PATTERN = "(\\s+)|(\\t+)|(\\n+)";
	private static final String FS = File.separator;

	private static final String PATH = "D:\\Users\\Urmas Hoogma\\workspace\\codesimilarity\\src\\test\\java\\com\\googlecode\\ounit\\codesimilarity\\assignment";

	public static void main(String[] args) {

		String a = "adorunrunrunadorunrun";
		String b = "adorunrungetadorunrun";
		String c = "lhg�hgfghdfghdfgdf�hg";

		List<Integer> suit1 = Similarity.generateAllHashes(a, NGRAM_SIZE);
		List<Integer> suit2 = Similarity.generateAllHashes(b, NGRAM_SIZE);
		List<Integer> suit3 = Similarity.generateAllHashes(c, NGRAM_SIZE);

		System.out.println("similar " + Similarity.similarHashes(suit1, suit2));
		System.out.println("similar " + Similarity.similarHashes(suit2, suit3));
		System.out.println("similar " + Similarity.similarHashes(suit3, suit1));

		double jcc = Similarity.JaccardCoefficient(a, b, null, NGRAM_SIZE,
				WINDOW_SIZE);
		System.out.println(jcc);

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
					simpleSource2, boilerplate,  NGRAM_SIZE, WINDOW_SIZE);
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
					simpleSource3, boilerplate,  NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccControl2 + " Second and fourth");
			double jccControl3 = Similarity.JaccardCoefficient(simpleSource2,
					simpleSource3, boilerplate,  NGRAM_SIZE, WINDOW_SIZE);
			System.out.println(jccControl3 + " Third and fourth");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String replaceMultipleWhiteSpace(String input) {
		Pattern whitespace = Pattern.compile(MULTIPLE_WHITESPACE_PATTERN);
		Matcher matcher = whitespace.matcher(input);
		String result = matcher.replaceAll(" ");
		return result;
	}

}
