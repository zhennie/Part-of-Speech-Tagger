package me.ash.part_of_speech.prob;

import me.ash.learning.prob.ProbabilityDistribution;
import me.ash.part_of_speech.data_structure.Tag;
import me.ash.part_of_speech.data_structure.Word;
import me.ash.data_structure.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by ash on 10/19/15.
 */
public class SmoothedProbability<T> implements ProbabilityDistribution<T> {

    @Override
    public Set<T> knownEvents() {
        return null;
    }

    @Override
    public double p(T t) {
        return 0;
    }
}
