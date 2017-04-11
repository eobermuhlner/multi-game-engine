package ch.obermuhlner.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GameUtil {
	
	public static <E> E pickRandom(Random random, List<Tuple2<E, Double>> allEntitiesWithValue) {
		if (allEntitiesWithValue.isEmpty()) {
			return null;
		}
		
		double total = 0;
		double min = 0;
		for (Tuple2<E, Double> entityWithValue : allEntitiesWithValue) {
			double value = entityWithValue.getValue2();
			if (value == Double.POSITIVE_INFINITY) {
				return entityWithValue.getValue1();
			}
			if (value == Double.NEGATIVE_INFINITY) {
				continue;
			}
			total += value;
			min = Math.min(min, value);
		}

		double offset = -min;
		total += offset * allEntitiesWithValue.size();

		double r = random.nextDouble() * total;
		
		total = 0;
		for (Tuple2<E, Double> entityWithValue : allEntitiesWithValue) {
			double value = entityWithValue.getValue2();
			if (value == Double.NEGATIVE_INFINITY) {
				continue;
			}
			total += value + offset;
			if (r <= total) {
				return entityWithValue.getValue1();
			}
		}

		// should not happen, but just to be save in case of rounding errors
		return allEntitiesWithValue.get(0).getValue1();
	}

	public static <E> E findMax(Random random, List<Tuple2<E, Double>> allEntitiesWithValue) {
		double maxValue = Double.NEGATIVE_INFINITY;
		List<E> best = new ArrayList<>();
		
		for (Tuple2<E, Double> entry : allEntitiesWithValue) {
			double value = entry.getValue2();
			if (value >= maxValue) {
				maxValue = entry.getValue2();
				if (value > maxValue) {
					best.clear();
				}
				best.add(entry.getValue1());
			}
		}

		if (best.isEmpty()) {
			return allEntitiesWithValue.get(random.nextInt(allEntitiesWithValue.size())).getValue1();
		}
		
		return best.get(random.nextInt(best.size()));
	}

	public static <E> void sort(List<Tuple2<E, Double>> allEntitiesWithValue) {
		Collections.sort(allEntitiesWithValue, new Comparator<Tuple2<E, Double>>() {
			@Override
			public int compare(Tuple2<E, Double> o1, Tuple2<E, Double> o2) {
				return -Double.compare(o1.getValue2(), o2.getValue2());
			}
		});
	}

}
