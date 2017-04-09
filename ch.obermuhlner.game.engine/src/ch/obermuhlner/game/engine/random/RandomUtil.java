package ch.obermuhlner.game.engine.random;

import java.util.List;
import java.util.Random;

import ch.obermuhlner.util.Tuple2;

public class RandomUtil {
	
	public static <E> E pickRandom(Random random, List<Tuple2<E, Double>> allEntitiesWithValue) {
		if (allEntitiesWithValue.isEmpty()) {
			return null;
		}
		
		double total = 0;
		double min = 0;
		for (Tuple2<E, Double> entityWithValue : allEntitiesWithValue) {
			double value = entityWithValue.getValue2();
			total += value;
			min = Math.min(min, value);
		}

		double offset = -min;
		total += offset * allEntitiesWithValue.size();

		double r = random.nextDouble() * total;
		
		total = 0;
		for (Tuple2<E, Double> entityWithValue : allEntitiesWithValue) {
			total += entityWithValue.getValue2() + offset;
			if (r <= total) {
				return entityWithValue.getValue1();
			}
		}

		// should not happen, but just to be save in case of rounding errors
		return allEntitiesWithValue.get(0).getValue1();
	}

}
