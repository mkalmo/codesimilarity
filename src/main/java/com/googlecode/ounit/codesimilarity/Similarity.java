package com.googlecode.ounit.codesimilarity;

/**
 * @author Urmas Hoogma
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Similarity {

	/**
	 * Count similar hashes from lists of hashes h1 and h2
	 * */
	public static long similarHashes(List<Integer> h1, List<Integer> h2) {
		return h1.stream().filter(item -> h2.contains(item)).count();
	}

	/**
	 * If modulus is 1 returns all hashes otherwise returns only those hashes
	 * which are divisible by modulus
	 * */
	public static List<Integer> generateHashes(String input, int ngramSize,
			int modulus) {
		return moduloHashes(generateHashes(generateNGrams(input, ngramSize)),
				modulus);
	}

	/**
	 * Select small subset of all hashes, in this case divisible by modulus
	 * */
	private static List<Integer> moduloHashes(List<Integer> generateHashes,
			int modulus) {
		if (modulus == 1) {
			return generateHashes;
		} else {
			return generateHashes.stream().filter(h -> h % modulus == 0)
					.collect(Collectors.toList());
		}
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
		if (size > 0) {
			String[] res = new String[size];
			for (int i = 0; i < size; i++) {
				res[i] = input.substring(i, i + ngramSize);
				// implement byte shift ?
			}
			return res;
		}
		return new String[0];
	}

	/**
	 * Comparing similarity of inputs
	 * 
	 * @return https://en.wikipedia.org/wiki/Jaccard_index
	 * */
	public static double JaccardCoefficient(String first, String second,
			String boilerPlate, int ngramSize, int windowSize, int modulus) {
		List<Integer> hashes1 = generateHashes(first, ngramSize, modulus);
		List<Integer> hashes2 = generateHashes(second, ngramSize, modulus);
		List<Integer> hashesBoilerPlate;

		int[] convertedHashes1;
		int[] convertedHashes2;

		if (boilerPlate == null) {
			convertedHashes1 = hashes1.stream().mapToInt(i -> i).toArray();
			convertedHashes2 = hashes2.stream().mapToInt(i -> i).toArray();
		} else {
			hashesBoilerPlate = generateHashes(boilerPlate, ngramSize, modulus);
			convertedHashes1 = removeBoilerplate(hashes1, hashesBoilerPlate)
					.stream().mapToInt(i -> i).toArray();
			convertedHashes2 = removeBoilerplate(hashes2, hashesBoilerPlate)
					.stream().mapToInt(i -> i).toArray();
		}

		Winnowing win1 = new Winnowing(convertedHashes1, windowSize);
		Winnowing win2 = new Winnowing(convertedHashes2, windowSize);

		Map<Integer, Integer> map1 = win1.winnow();
		Map<Integer, Integer> map2 = win2.winnow();

		Collection<Integer> collection1 = map1.values();
		Collection<Integer> collection2 = map2.values();
		Collection<Integer> commonCollection = collection1.stream()
				.filter(item -> collection2.contains(item))
				.collect(Collectors.toList());

		return calculateJaccardCoefficient(collection1, collection2,
				commonCollection);
	}

	public static List<Integer> removeBoilerplate(
			List<Integer> submissionHashes, List<Integer> boilerPlateHashes) {
		submissionHashes.removeAll(boilerPlateHashes);
		return submissionHashes;
	}

	public static double JaccardCoefficientFromHashes(
			int[] convertedHashesFirst, int[] convertedHashesSecond,
			int windowSize) {
		Winnowing win1 = new Winnowing(convertedHashesFirst, windowSize);
		Winnowing win2 = new Winnowing(convertedHashesSecond, windowSize);

		Map<Integer, Integer> map1 = win1.winnow();
		Map<Integer, Integer> map2 = win2.winnow();

		Collection<Integer> collection1 = map1.values();
		Collection<Integer> collection2 = map2.values();
		Collection<Integer> commonCollection = collection1.stream()
				.filter(item -> collection2.contains(item))
				.collect(Collectors.toList());

		return calculateJaccardCoefficient(collection1, collection2,
				commonCollection);
	}

	private static double calculateJaccardCoefficient(
			Collection<Integer> collection1, Collection<Integer> collection2,
			Collection<Integer> commonCollection) {
		double common = commonCollection.size();
		double different = collection1.size() - common;
		return (common == 0) ? 0.0 : common / different;
	}

}
