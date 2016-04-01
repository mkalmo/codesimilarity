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

public class SimilarityRunner {

	private int NGRAM_SIZE;
	private int WINDOW_SIZE;
	private final String FS = System.getProperty("file.separator");
	private final String STUDENTS = "students";
	private final String TEACHER = "teacher";
	private String FILE_NAME;

	private Map<Pair, Double> comparisonResults = new HashMap<Pair, Double>();
	private Map<Integer, String> studentSubmissions = new HashMap<Integer, String>();
	private Map<Integer, List<Integer>> studentHashes = new HashMap<Integer, List<Integer>>();
	private Map<Integer, int[]> convertedHashes = new HashMap<Integer, int[]>();
	private double[][] csvData;

	public double difference;
	
	public SimilarityRunner(String filename, int ngramsize, int windowsize) {
		FILE_NAME = filename;
		NGRAM_SIZE = ngramsize;
		WINDOW_SIZE = windowsize;
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

	public void test(String path) {
		listf(path + STUDENTS);
		Integer[] studentIds = studentSubmissions.keySet().toArray(
				new Integer[0]);
		generateHashes(studentIds);
		List<Integer> hashesBoilerPlate = generateBoilerPlateHashes(path);
		convertHashes(studentIds, hashesBoilerPlate);
		csvData = new double[studentIds.length + 1][studentIds.length + 1];
		csvData[0][0] = 2; // need number larger or equal to 1 for formatting
							// purposes
		for (int j = 0; j < studentIds.length; j++) {
			csvData[0][j + 1] = studentIds[j];
		}
		for (int i = 0; i < studentIds.length; i++) {
			int first = studentIds[i];
			csvData[i + 1][0] = first;
			for (int j = 0; j < studentIds.length; j++) {
				int second = studentIds[j];
				if (i != j) {
					double result = Similarity.JaccardCoefficientFromHashes(
							convertedHashes.get(first),
							convertedHashes.get(second), WINDOW_SIZE);
					comparisonResults.put(new Pair(first, second), result);
					csvData[i + 1][j + 1] = result;
				} else {
					csvData[i + 1][j + 1] = 0.0;
				}
			}
		}
		// System.out.println(composeCsv(csvData));
		comparisonResults = MapUtil.sortByValue(comparisonResults);

		// operating on a premise that I only have 1 plagiated work
		int placeHolder = 0;
		double inside = 0;
		double outside = 0;
		for (Map.Entry<Pair, Double> entry : comparisonResults.entrySet()) {
			Pair key = entry.getKey();
			System.out.println(entry.getValue() + " " + key.getFirst() + " "
					+ key.getSecond());
			if (placeHolder == 1) {
				inside = entry.getValue();
			}
			if (placeHolder == 2) {
				outside = entry.getValue(); 
			}
			placeHolder++;
		}
		difference = inside / outside;
	}

	private void convertHashes(Integer[] studentIds,
			List<Integer> hashesBoilerPlate) {
		for (int i = 0; i < studentIds.length; i++) {
			int[] list;
			if (hashesBoilerPlate.size() == 0) {
				list = (studentHashes.get(studentIds[i])).stream()
						.mapToInt(s -> s).toArray();
			} else {
				list = Similarity
						.removeBoilerplate(studentHashes.get(studentIds[i]),
								hashesBoilerPlate).stream().mapToInt(s -> s)
						.toArray();
			}
			convertedHashes.put(studentIds[i], list);
		}
	}

	private void generateHashes(Integer[] studentIds) {
		for (int i = 0; i < studentIds.length; i++) {
			List<Integer> hashes = Similarity.generateAllHashes(
					studentSubmissions.get(studentIds[i]), NGRAM_SIZE);
			studentHashes.put(studentIds[i], hashes);
		}
	}

	private String composeCsv(double[][] csvData) {
		StringBuilder sb = new StringBuilder();
		int rowSize;
		for (int i = 0; i < csvData.length; i++) {
			rowSize = csvData[0].length;
			for (int j = 0; j < rowSize; j++) {
				sb.append(csvData[i][j]);
				if (!(j == rowSize - 1)) {
					sb.append(",");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public List<File> listf(String directoryName) {
		File directory = new File(directoryName);
		List<File> resultList = new ArrayList<File>();

		File[] fList = directory.listFiles();
		resultList.addAll(Arrays.asList(fList));
		String[] splitted = directoryName.split("\\\\");// escaping slashes
		for (File file : fList) {
			if (file.isFile()) {
				String studentId = splitted[splitted.length - 1];
				String submission;
				try {
					submission = new String(Files.readAllBytes(Paths
							.get(directoryName + FS + FILE_NAME)));
					studentSubmissions.put(Integer.parseInt(studentId),
							submission);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (file.isDirectory()) {
				resultList.addAll(listf(file.getAbsolutePath()));
			}
		}
		return resultList;
	}
}
