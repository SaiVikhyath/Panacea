package com.example.panacea;

import java.util.function.Function;

public class FuzzyLogicController {

    private static double gaussian(double x, double mean, double standardDeviation) {
        return Math.exp(-0.5 * Math.pow((x - mean) / standardDeviation, 2));
    }

    private static Function<Double, Double> createGaussianMembershipFunction(double mean, double standardDeviation) {
        return x -> gaussian(x, mean, standardDeviation);
    }

    // Membership functions for inputs
    private static Function<Double, Double> heartRateLow = createGaussianMembershipFunction(55, 15);
    private static Function<Double, Double> heartRateMedium = createGaussianMembershipFunction(75, 20);
    private static Function<Double, Double> heartRateHigh = createGaussianMembershipFunction(100, 25);

    private static Function<Double, Double> respiratoryRateLow = createGaussianMembershipFunction(12, 4);
    private static Function<Double, Double> respiratoryRateMedium = createGaussianMembershipFunction(16, 4);
    private static Function<Double, Double> respiratoryRateHigh = createGaussianMembershipFunction(20, 4);

    private static Function<Double, Double> bloodOxygenLow = createGaussianMembershipFunction(80, 10);
    private static Function<Double, Double> bloodOxygenMedium = createGaussianMembershipFunction(90, 10);
    private static Function<Double, Double> bloodOxygenHigh = createGaussianMembershipFunction(100, 10);

    // Fuzzy logic rules
    private static double fuzzyLogicRule(double heartRate, double respiratoryRate, double bloodOxygen) {
        // Rule 1: If heart rate is high OR respiratory rate is high AND blood oxygen is low, then lifestyle is poor
        double rule1 = Math.max(
                Double.min(heartRateHigh.apply(heartRate), bloodOxygenLow.apply(bloodOxygen)),
                respiratoryRateHigh.apply(respiratoryRate)
        );

        // Rule 2: If heart rate is medium AND respiratory rate is medium AND blood oxygen is medium, then lifestyle is moderate
        double rule2 = Double.min(
                Double.min(heartRateMedium.apply(heartRate), respiratoryRateMedium.apply(respiratoryRate)),
                bloodOxygenMedium.apply(bloodOxygen)
        );

        // Rule 3: If heart rate is low AND respiratory rate is low AND blood oxygen is high, then lifestyle is good
        double rule3 = Double.min(
                Double.min(heartRateLow.apply(heartRate), respiratoryRateLow.apply(respiratoryRate)),
                bloodOxygenHigh.apply(bloodOxygen)
        );

        // Combine rules using max operator
        return Math.max(rule1, Math.max(rule2, rule3));
    }

    // Calculate lifestyle score
    public static double calculateLifestyleScore(double heartRate, double respiratoryRate, double bloodOxygen) {
        // For simplicity, let's use the centroid defuzzification method
        double numerator = 0;
        double denominator = 0;

        for (int i = 0; i <= 100; i++) {
            double membership = fuzzyLogicRule(heartRate, respiratoryRate, bloodOxygen);
            numerator += i * membership;
            denominator += membership;
        }

        return numerator / denominator;
    }

}
