package com.googlecode.ounit.codesimilarity;

import java.util.HashMap;
import java.util.Map;

public class Winnowing {

	private int[] hashList;
	private Map<Integer, Integer> map;
	private int windowSize;

	public Winnowing(int[] hashList, int windowSize) {
		this.hashList = hashList;
		this.windowSize = windowSize;
		this.map = new HashMap<>();
	}

	public Map<Integer, Integer> winnow() {
		for (int i = 0; i < hashList.length - windowSize + 1; i++) {
			// select the minimal hash within the window
			int[] min = min(makeWindow(i, windowSize));
			// put the minimla hash into Map (duplicates are overwritten)
			map.put(min[0] + i, min[1]);
		}
		// System.out.println(map);
		return map;
	}

	/**
	 * Select the minimal value
	 * */
	private static int[] min(int[] values) {
		int[] kvp = new int[2];
		int min = Integer.MAX_VALUE;
		int current;
		for (int i = 0; i < values.length; i++) {
			current = values[i];
			if (current < min) {
				kvp[0] = i;// position
				kvp[1] = current;// hash
				min = current;
			}
		}
		return kvp;
	}

	/**
	 * Construct and populate window with hashes
	 * */
	private int[] makeWindow(int index, int windowSize) {
		int[] res = new int[windowSize];
		for (int i = 0; i < windowSize; i++) {
			res[i] = hashList[i + index];
			// System.out.print(res[i]+",");
		}
		// System.out.println("");
		return res;
	}

}
