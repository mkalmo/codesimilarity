package com.googlecode.ounit.codesimilarity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilarityRunner2 {

	private double SIMILARITY_THRESHOLD = 1;
	private int NGRAM_SIZE;
	private int WINDOW_SIZE;
	private final String FS = System.getProperty("file.separator");
	private final String STUDENTS = "students";
	private final String TEACHER = "teacher";
	private String FILE_NAME;

	private Map<Pair, Double> comparisonResults = new HashMap<Pair, Double>();
	private Map<Pair, String> studentSubmissions = new HashMap<Pair, String>();
	private Map<Integer, List<Integer>> studentHashes = new HashMap<Integer, List<Integer>>();
	private Map<Integer, int[]> convertedHashes = new HashMap<Integer, int[]>();

	public SimilarityRunner2(String filename, int ngramsize, int windowsize, double similarityThreshold) {
		FILE_NAME = filename;
		NGRAM_SIZE = ngramsize;
		WINDOW_SIZE = windowsize;
		SIMILARITY_THRESHOLD = similarityThreshold;
	}

	private List<Integer> generateBoilerPlateHashes(String path) {
		String boilerplate = null;
		try {
			boilerplate = new String(Files.readAllBytes(Paths.get(path
					+ TEACHER + FS + FILE_NAME)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Similarity.generateAllHashes(boilerplate, NGRAM_SIZE);
	}

	public String test(String path) {
		listf(path + STUDENTS);
		for (Map.Entry<Pair, String> entry : studentSubmissions.entrySet()) {
			List<Integer> hashes = Similarity.generateAllHashes(
					studentSubmissions.get(entry.getKey()), NGRAM_SIZE);
			studentHashes.put(entry.getKey().getSecond(), hashes);
		}
		
		List<Integer> attemptsList = new ArrayList<Integer>();
		for (Pair pair : studentSubmissions.keySet()) {
			attemptsList.add(pair.getSecond());
		}
		Integer[] attemptsArray = attemptsList.toArray(new Integer[1]);

		List<Integer> hashesBoilerPlate = generateBoilerPlateHashes(path);
		convertHashes(hashesBoilerPlate);

		for (int i = 0; i < attemptsArray.length; i++) {
			int first = attemptsArray[i];
			for (int j = 0; j < attemptsArray.length; j++) {
				int second = attemptsArray[j];
				if (i != j) {
					double result = Similarity.JaccardCoefficientFromHashes(
							convertedHashes.get(first),
							convertedHashes.get(second), WINDOW_SIZE);
					comparisonResults.put(new Pair(first, second), result);
				} 
			}
		}
		comparisonResults = MapUtil.sortByValue(comparisonResults);

		Map<Integer, Integer> attemptToStudent = new HashMap<Integer, Integer>();
		for (Map.Entry<Pair, String> entry : studentSubmissions.entrySet()) {
			attemptToStudent.put(entry.getKey().getSecond(), entry.getKey().getFirst());
		}
		return composeCsv(attemptToStudent);
	}

	private String composeCsv(Map<Integer, Integer> attemptToStudent) {
		StringBuffer sb = new StringBuffer();
		sb.append("Student1\tAttempt1\tStudent2\tAttempt2\tResult\n");
		for (Map.Entry<Pair, Double> entry : comparisonResults.entrySet()) {
			int attempt1 = entry.getKey().getFirst();
			int attempt2 = entry.getKey().getSecond();
			int student1 = attemptToStudent.get(attempt1);
			int student2 = attemptToStudent.get(attempt2);
			if (student1 != student2 && !entry.getValue().isNaN()
					&& !entry.getValue().isInfinite() && entry.getValue() > SIMILARITY_THRESHOLD) {
				sb.append(student1+"\t"+entry.getKey().getFirst()+"\t"+student2+"\t"+entry.getKey().getSecond()+"\t"+entry.getValue()+"\n");
			}
		}
		return sb.toString();
	}

	private void convertHashes(List<Integer> hashesBoilerPlate) {
		for (Map.Entry<Integer, List<Integer>> entry : studentHashes.entrySet()) {
			int[] list = null;
			if (hashesBoilerPlate.size() == 0) {
				studentHashes.get(entry.getKey()).stream().mapToInt(s -> s)
						.toArray();
			} else {
				list = Similarity
						.removeBoilerplate(studentHashes.get(entry.getKey()),
								hashesBoilerPlate).stream().mapToInt(s -> s)
						.toArray();
			}
			convertedHashes.put(entry.getKey(), list);
		}
	}

	public static String INTEGER_REGEX = "\\d+";

	public List<File> listf(String directoryName) {
		File directory = new File(directoryName);
		List<File> resultList = new ArrayList<File>();

		File[] fList = directory.listFiles();
		resultList.addAll(Arrays.asList(fList));
		String[] splitted = directoryName.split("\\\\");// escaping slashes
		for (File file : fList) {
			if (file.isFile()) {
				String studentId = splitted[splitted.length - 2];
				String attemptId = splitted[splitted.length - 1];
				String submission;
				if (studentId.matches(INTEGER_REGEX)
						&& attemptId.matches(INTEGER_REGEX)) {

					try {
						submission = new String(Files.readAllBytes(Paths
								.get(directoryName + FS + FILE_NAME)));
						studentSubmissions.put(
								new Pair(Integer.parseInt(studentId), Integer
										.parseInt(attemptId)), submission);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else if (file.isDirectory()) {
				resultList.addAll(listf(file.getAbsolutePath()));
			}
		}
		return resultList;
	}
}
