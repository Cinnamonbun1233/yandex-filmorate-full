package ru.yandex.practicum.filmorate.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SlopeOnePredictor {

	private final Map<Long, HashMap<Long, Double>> inputData; // user_id, item_id
	private final List<Long> items; // item_id
	private final Map<Long, Map<Long, Double>> diff = new HashMap<>(); // item_id, item_id
	private final Map<Long, Map<Long, Integer>> freq = new HashMap<>(); // item_id, item_id
	private final Map<Long, HashMap<Long, Double>> outputData = new HashMap<>(); // user_id, item_id

	public SlopeOnePredictor(Map<Long, HashMap<Long, Double>> inputData) {

		this.inputData = inputData;

		items = inputData.entrySet()
				.stream()
				.flatMap(e -> e.getValue().keySet().stream())
				.collect(Collectors.toList());

		buildDifferencesMatrix();
		buildPredictions();

		System.out.println("Slope One - Before the Prediction");
		printData(inputData);
		System.out.println("Slope One - With Predictions");
		printData(outputData);

	}

	// PUBLIC
	public HashMap<Long, Double> getPrediction(Long userId) {

		HashMap<Long, Double> prediction = outputData.getOrDefault(userId, new HashMap<>());
		while (prediction.values().remove(null));
		return prediction;

	}


	// PRIVATE
	private void buildDifferencesMatrix() {

		for (HashMap<Long, Double> user : inputData.values()) {
			for (Map.Entry<Long, Double> e : user.entrySet()) {
				if (!diff.containsKey(e.getKey())) {
					diff.put(e.getKey(), new HashMap<Long, Double>());
					freq.put(e.getKey(), new HashMap<Long, Integer>());
				}
				for (Map.Entry<Long, Double> e2 : user.entrySet()) {
					int oldCount = 0;
					if (freq.get(e.getKey()).containsKey(e2.getKey())) {
						oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
					}
					double oldDiff = 0.0;
					if (diff.get(e.getKey()).containsKey(e2.getKey())) {
						oldDiff = diff.get(e.getKey()).get(e2.getKey()).doubleValue();
					}
					double observedDiff = e.getValue() - e2.getValue();
					freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
					diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
				}
			}
		}
		for (Long j : diff.keySet()) {
			for (Long i : diff.get(j).keySet()) {
				double oldValue = diff.get(j).get(i).doubleValue();
				int count = freq.get(j).get(i).intValue();
				diff.get(j).put(i, oldValue / count);
			}
		}

	}

	private void buildPredictions() {

		HashMap<Long, Double> uPred = new HashMap<>();
		HashMap<Long, Integer> uFreq = new HashMap<>();
		for (Long j : diff.keySet()) {
			uFreq.put(j, 0);
			uPred.put(j, 0.0);
		}
		for (Map.Entry<Long, HashMap<Long, Double>> e : inputData.entrySet()) {
			for (Long j : e.getValue().keySet()) {
				for (Long k : diff.keySet()) {
					try {
						double predictedValue = diff.get(k).get(j).doubleValue() + e.getValue().get(j).doubleValue();
						double finalValue = predictedValue * freq.get(k).get(j).intValue();
						uPred.put(k, uPred.get(k) + finalValue);
						uFreq.put(k, uFreq.get(k) + freq.get(k).get(j).intValue());
					} catch (NullPointerException e1) {
						double predictedValue = -1;
					}
				}
			}
			HashMap<Long, Double> clean = new HashMap<>();
			for (Long j : uPred.keySet()) {
				if (uFreq.get(j) > 0) {
					clean.put(j, uPred.get(j).doubleValue() / uFreq.get(j).intValue());
				}
			}
			for (Long j : items) {
				if (e.getValue().containsKey(j)) {
					//clean.put(j, e.getValue().get(j));
					clean.remove(j);
				} else if (!clean.containsKey(j)) {
					clean.put(j, null);
				}
			}
			outputData.put(e.getKey(), clean);
		}

	}

	private void printData(Map<Long, HashMap<Long, Double>> data) {
		for (Long user : data.keySet()) {
			System.out.println("User #" + user + ":");
			print(data.get(user));
		}
	}

	private void print(HashMap<Long, Double> hashMap) {
		DecimalFormat formatter = new DecimalFormat("#0.000");
		for (Long j : hashMap.keySet()) {
			Double predicionValue = hashMap.get(j);
			String predicionValueString = (predicionValue != null ? formatter.format(predicionValue.doubleValue()) : "no prediction");
			System.out.println(" Item #" + j + " --> " + predicionValueString);
		}
	}

}
