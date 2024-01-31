package com.example.panacea;

public class HealthPoint {
    private final double heartRate;
    private final double respiratoryRate;
    private final double bloodOxygen;
    private final double sleep;
    boolean visited;
    boolean isOutlier;
    private int clusterId;

    public HealthPoint(double heartRate, double respiratoryRate, double bloodOxygen, double sleep) {
        this.heartRate = heartRate;
        this.respiratoryRate = respiratoryRate;
        this.bloodOxygen = bloodOxygen;
        this.sleep = sleep;
        this.visited = false;
        this.isOutlier = false;
        this.clusterId = -1; // Initialize to no cluster
    }

    public double distanceTo(HealthPoint other) {
        return Math.sqrt(
                Math.pow(this.heartRate - other.heartRate, 2) +
                        Math.pow(this.respiratoryRate - other.respiratoryRate, 2) +
                        Math.pow(this.bloodOxygen - other.bloodOxygen, 2) +
                        Math.pow(this.sleep - other.sleep, 2)
        );
    }

    @Override
    public String toString() {
        return String.format("Heart Rate: %.2f, Respiratory Rate: %.2f, Blood Oxygen: %.2f, Sleep: %.2f",
                heartRate, respiratoryRate, bloodOxygen, sleep);
    }

    public void setClusterId(int currentClusterId) {
        this.clusterId = currentClusterId;
    }

    public int getClusterId() {
        return clusterId;
    }
}
