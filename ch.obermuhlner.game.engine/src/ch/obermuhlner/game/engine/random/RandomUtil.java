package ch.obermuhlner.game.engine.random;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class RandomUtil {
	
	public static <E> E pickRandom(Random random, Map<E, Double> allEntitiesWithValue) {
		if (allEntitiesWithValue.isEmpty()) {
			return null;
		}
		
		double total = 0;
		double min = 0;
		for (Entry<E, Double> entityWithValue : allEntitiesWithValue.entrySet()) {
			double value = entityWithValue.getValue();
			total += value;
			min = Math.min(min, value);
		}

		double offset = -min;
		total += offset * allEntitiesWithValue.size();

		double r = random.nextDouble() * total;
		
		total = 0;
		for (Entry<E, Double> entityWithValue : allEntitiesWithValue.entrySet()) {
			total += entityWithValue.getValue() + offset;
			if (r <= total) {
				return entityWithValue.getKey();
			}
		}

		// should not happen, but just to be save in case of rounding errors
		return allEntitiesWithValue.keySet().iterator().next();
	}

}
