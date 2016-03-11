package com.googlecode.ounit.codesimilarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Similarity {

	/**
	 * Count similar hashes from lists of hashes h1 and h2
	 * */
	public static long similarHashes(List<Integer> h1, List<Integer> h2) {
		return h1.stream().filter(item -> h2.contains(item)).count();
	}

	/**
	 * Returns only subset of hashes
	 * */
	public static List<Integer> generateHashes(String input, int ngramSize,
			int hashCount) {
		return minimalHashes(generateHashes(generateNGrams(input, ngramSize)),
				hashCount);
	}

	/**
	 * Returns all hashes
	 * */
	public static List<Integer> generateAllHashes(String input, int ngramSize) {
		return generateHashes(generateNGrams(input, ngramSize));
	}

	/**
	 * Select small subset of all hashes, in this case smallest
	 * */
	private static List<Integer> minimalHashes(List<Integer> generateHashes,
			int hashCount) {
		Collections.sort(generateHashes);// custom method
		return generateHashes.stream().limit(hashCount)
				.collect(Collectors.toList());
	}

	/**
	 * Generating hashes with built-in (inefficient) method
	 * */
	public static List<Integer> generateHashes(String[] input) {
		List<Integer> res = new ArrayList<>();
		for (int i = 0; i < input.length; i++) {
			res.add(input[i].hashCode());// better hashcode
		}
		return res;
	}

	/**
	 * Generating NGrams
	 * */
	public static String[] generateNGrams(String input, int ngramSize) {
		int size = input.length() - ngramSize + 1;
		String[] res = new String[size];
		for (int i = 0; i < size; i++) {
			res[i] = input.substring(i, i + ngramSize);// implement byte shift ?
		}
		return res;
	}

	/**
	 * Comparing similarity of inputs
	 * 
	 * @return https://en.wikipedia.org/wiki/Jaccard_index
	 * */
	public static double JaccardCoefficient(String a, String b, int ngramSize,
			int windowSize) {
		List<Integer> suit1 = generateAllHashes(a, ngramSize);
		List<Integer> suit2 = generateAllHashes(b, ngramSize);

		int[] converted1 = suit1.stream().mapToInt(i -> i).toArray();
		int[] converted2 = suit2.stream().mapToInt(i -> i).toArray();

		Winnowing win1 = new Winnowing(converted1, windowSize);
		Winnowing win2 = new Winnowing(converted2, windowSize);

		Map<Integer, Integer> map1 = win1.winnow();
		Map<Integer, Integer> map2 = win2.winnow();

		Collection<Integer> set1 = map1.values();
		Collection<Integer> set2 = map2.values();

		Set<Integer> commonSet = set1.stream()
				.filter(item -> set2.contains(item))
				.collect(Collectors.toSet());

		return calculateJaccardCoefficient(set1, set2, commonSet);
	}

	private static double calculateJaccardCoefficient(Collection<Integer> set1,
			Collection<Integer> set2, Set<Integer> commonSet) {
		double cs = commonSet.size();
		double shortest;
		if (set1.size() > set2.size()) {
			shortest = set2.size();
		} else {
			shortest = set1.size();
		}
		double different = shortest - cs;
		return cs / different;
	}

}

