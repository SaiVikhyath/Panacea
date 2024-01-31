package com.example.panacea;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DBSCAN {
    private final double epsilon;
    private final int minPts;

    public DBSCAN(double epsilon, int minPts) {
        this.epsilon = epsilon;
        this.minPts = minPts;
    }

    public void run(List<HealthPoint> healthPoints) {
        int currentClusterId = 0;
        for (HealthPoint point : healthPoints) {
            if (!point.visited) {
                point.visited = true;
                List<HealthPoint> neighbours = getNeighbours(point, healthPoints);
                if (neighbours.size() < minPts) {
                    point.isOutlier = true;
                    handleOutlier(point);
                } else {
                    currentClusterId++;
                    List<HealthPoint> cluster = new ArrayList<>();
                    expandCluster(point, neighbours, healthPoints, cluster, currentClusterId);
                    handleCluster(cluster);
                }
            }
        }
    }

    private List<HealthPoint> getNeighbours(HealthPoint point, List<HealthPoint> healthPoints) {
        return healthPoints.stream()
                .filter(p -> p.distanceTo(point) <= epsilon)
                .collect(Collectors.toList());
    }

    private void expandCluster(HealthPoint corePoint, List<HealthPoint> neighbours, List<HealthPoint> healthPoints, List<HealthPoint> cluster, int currentClusterId) {
        cluster.add(corePoint);
        corePoint.setClusterId(currentClusterId);

        for (HealthPoint neighbour : neighbours) {
            if (!neighbour.visited) {
                neighbour.visited = true;
                List<HealthPoint> newNeighbours = getNeighbours(neighbour, healthPoints);
                if (newNeighbours.size() >= minPts) {
                    neighbours.addAll(newNeighbours);
                }
            }
            if (!cluster.contains(neighbour)) {
                cluster.add(neighbour);
                neighbour.setClusterId(currentClusterId);
            }
        }
    }

    private void handleOutlier(HealthPoint outlier) {
        System.out.println("Outlier detected: " + outlier.toString());
    }

    private void handleCluster(List<HealthPoint> cluster) {
        System.out.println("Cluster " + cluster.get(0).getClusterId() + " detected: " + cluster.toString());
    }
}
