package net.mat0u5.do2manager.gui.ingamescreen;

import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class GraphGenerator {

    public static final Box graphBox = new Box(-528.0989, 107.5056, 1946.1, -512.9092, 116.6702, 1946.1);

    public static void generateGraph(ServerWorld world, List<DO2RunAbridged> runs, String metric) {
        if (runs.isEmpty()) return;

        // Step 1: Calculate graph boundaries
        double minX = graphBox.minX;
        double maxX = graphBox.maxX;
        double minY = graphBox.minY;
        double maxY = graphBox.maxY;

        // Get the min and max timestamps
        int minTimestamp = Integer.MAX_VALUE;
        int maxTimestamp = Integer.MIN_VALUE;
        for (DO2RunAbridged run : runs) {
            int timestamp = run.timestampDate();
            if (timestamp != -1) {
                minTimestamp = Math.min(minTimestamp, timestamp);
                maxTimestamp = Math.max(maxTimestamp, timestamp);
            }
        }

        // Step 2: Calculate y-axis range based on the metric
        double maxMetricValue = 0;
        switch (metric) {
            case "winpercentage" -> maxMetricValue = 100;
            case "totalruns" -> maxMetricValue = runs.size();
            case "avg run embers" -> maxMetricValue = runs.stream().mapToInt(r -> r.embers_counted).average().orElse(0);
            case "avg run crowns" -> maxMetricValue = runs.stream().mapToInt(r -> r.crowns_counted).average().orElse(0);
            case "total embers" -> maxMetricValue = runs.stream().mapToInt(r -> r.embers_counted).sum();
            case "total crowns" -> maxMetricValue = runs.stream().mapToInt(r -> r.crowns_counted).sum();
        }

        // Step 3: Calculate data points
        List<Vec3d> graphPoints = new ArrayList<>();
        for (DO2RunAbridged run : runs) {
            int timestamp = run.timestampDate();
            if (timestamp == -1) continue;

            // Normalize x (timestamps) to graph range
            double normalizedX = minX + ((timestamp - minTimestamp) / (double) (maxTimestamp - minTimestamp)) * (maxX - minX);

            // Normalize y (metric values) to graph range
            double metricValue = switch (metric) {
                case "winpercentage" -> (run.getSuccess() ? 100 : 0);
                case "totalruns" -> 1;
                case "avg run embers" -> run.embers_counted;
                case "avg run crowns" -> run.crowns_counted;
                case "total embers" -> run.embers_counted;
                case "total crowns" -> run.crowns_counted;
                default -> 0;
            };
            double normalizedY = minY + (metricValue / maxMetricValue) * (maxY - minY);

            // Add to graph points
            graphPoints.add(new Vec3d(normalizedX, normalizedY, graphBox.minZ));
        }

        // Step 4: Summon display entities to connect points
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            Vec3d start = graphPoints.get(i);
            Vec3d end = graphPoints.get(i + 1);

            // Calculate midpoint and rotation for the line
            Vec3d midpoint = start.add(end).multiply(0.5);
            double length = start.distanceTo(end);
            double rotation = Math.toDegrees(Math.atan2(end.y - start.y, end.x - start.x));

            // Summon display entity
            world.spawnEntity(new DisplayEntity.TextDisplayEntity(world, midpoint, length, rotation));
        }

        // Step 5: Add axis labels (optional)
        addAxisLabels(world, minX, maxX, minY, maxY, minTimestamp, maxTimestamp, metric, maxMetricValue);
    }

    private static void addAxisLabels(ServerWorld world, double minX, double maxX, double minY, double maxY,
                                      int minTimestamp, int maxTimestamp, String metric, double maxMetricValue) {
        // Add X-axis labels (timestamps)
        int sections = 5;
        for (int i = 0; i <= sections; i++) {
            double t = minTimestamp + i * (maxTimestamp - minTimestamp) / (double) sections;
            double posX = minX + i * (maxX - minX) / sections;

            // Summon label entity
            world.spawnEntity(new DisplayEntity.TextDisplayEntity(world, new Vec3d(posX, minY - 1, graphBox.minZ), t, 0));
        }

        // Add Y-axis labels (metric values)
        for (int i = 0; i <= sections; i++) {
            double metricValue = i * maxMetricValue / sections;
            double posY = minY + i * (maxY - minY) / sections;

            // Summon label entity
            world.spawnEntity(new DisplayEntity.TextDisplayEntity(world, new Vec3d(minX - 1, posY, graphBox.minZ), metricValue, 0));
        }
    }
}
