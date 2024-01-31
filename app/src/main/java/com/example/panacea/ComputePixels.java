package com.example.panacea;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class ComputePixels {
    private final CopyOnWriteArrayList<Pixels<Integer>> readings = new CopyOnWriteArrayList<Pixels<Integer>>();
    private int max = Integer.MIN_VALUE;
    private int min = Integer.MAX_VALUE;
    private final int averageSize = 4;
    void add(int reading) {
        Pixels<Integer> pixelsWithDate = new Pixels<>(new Date(), reading);
        readings.add(pixelsWithDate);
        if (reading < min)
            min = reading;
        if (reading > max)
            max = reading;
    }
    CopyOnWriteArrayList<Pixels<Float>> getStandardDeviations() {
        CopyOnWriteArrayList<Pixels<Float>> standardDeviations = new CopyOnWriteArrayList<>();
        for (int i = 0; i < readings.size(); i++) {
            int s = 0;
            for (int averageCount = 0; averageCount < averageSize; averageCount++) {
                s += readings.get(Math.max(0, i - averageCount)).reading;
            }
            Pixels<Float> standardDeviation = new Pixels<>(readings.get(i).timestamp, ((float) s / averageSize - min) / (max - min));
            standardDeviations.add(standardDeviation);
        }
        return standardDeviations;
    }
    CopyOnWriteArrayList<Pixels<Integer>> getFinalStandardDeviations(int count) {
        if (count < readings.size()) {
            return new CopyOnWriteArrayList<>(readings.subList(readings.size() - 1 - count, readings.size() - 1));
        } else {
            return readings;
        }
    }
    Date getLastTimestamp() {
        return readings.get(readings.size() - 1).timestamp;
    }

}