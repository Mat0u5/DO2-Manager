package net.mat0u5.do2manager.gui.ingamescreen;

import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphGenerator {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM. dd");
    public static final Box graphBox = new Box(-528.0989, 107.5056, 1946.1, -512.9092, 116.6702, 1946.1);

    public static void generateGraph(ServerWorld world, List<DO2RunAbridged> runs, String metric, ServerPlayerEntity player) {
        if (runs.isEmpty()) return;
        OtherUtils.executeCommand("kill @e[tag=graph_var]");

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
            System.out.println("timestampDATE_"+run.date);
            System.out.println("timestamp_"+timestamp);
            if (timestamp != -1) {
                minTimestamp = Math.min(minTimestamp, timestamp);
                maxTimestamp = Math.max(maxTimestamp, timestamp);
            }
        }
        System.out.println("terst0__"+minTimestamp);
        System.out.println("terstsax0__"+maxTimestamp);

        // Step 2: Calculate y-axis range based on the metric
        String uuid = player.getUuidAsString();
        double maxMetricValue = 0;
        switch (metric) {
            case "winpercentage" -> maxMetricValue = 100;
            case "runs" -> maxMetricValue = runs.size();
            case "embers" -> maxMetricValue = runs.stream().mapToInt(r -> r.embers_counted).average().orElse(0);
            case "crowns" -> maxMetricValue = runs.stream().mapToInt(r -> r.crowns_counted).average().orElse(0);
            case "totalembers" -> maxMetricValue = runs.stream().mapToInt(r -> r.embers_counted).sum();
            case "totalcrowns" -> maxMetricValue = runs.stream().mapToInt(r -> r.crowns_counted).sum();
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
                case "winpercentage" -> (run.getSuccessFor(uuid) ? 100 : 0);
                case "runs" -> 1;
                case "embers" -> run.embers_counted;
                case "crowns" -> run.crowns_counted;
                case "totalembers" -> run.embers_counted;
                case "totalcrowns" -> run.crowns_counted;
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

            summonLine(world, start, end);

            // Summon text labels for points
            summonText(world, start, "Point " + (i + 1));
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
            System.out.println("test_"+t);
            double posX = minX + i * (maxX - minX) / sections;

            summonTextRotated(world, new Vec3d(posX, minY - 0.1, graphBox.minZ), formatter.format(LocalDateTime.ofEpochSecond((long) t,0, ZoneOffset.UTC)));
        }

        // Add Y-axis labels (metric values)
        for (int i = 0; i <= sections; i++) {
            double metricValue = i * maxMetricValue / sections;
            double posY = minY + i * (maxY - minY) / sections;

            summonText(world, new Vec3d(minX - 0.2, posY, graphBox.minZ), String.format("%.1f", metricValue));
        }
    }


    private static void summonLine(ServerWorld world, Vec3d start, Vec3d end) {
        String command = String.format(
                "summon marker %f %f %f {Tags:[\"graph_var\"],CustomNameVisible:0}",
                start.x, start.y, start.z
        );
        OtherUtils.executeCommand(command);
    }

    private static void summonText(ServerWorld world, Vec3d position, String text) {
        String command = "summon minecraft:text_display "+position.x+" "+position.y+" "+position.z+" " +
                "{Tags:[\"graph_var\"],alignment: \"center\", line_width: 200, see_through: 0b, shadow: 0b, text: '\""+text+"\"', text_opacity: -1b}";
        OtherUtils.executeCommand(command);
    }
    private static void summonTextRotated(ServerWorld world, Vec3d position, String text) {
        String command = "summon minecraft:text_display "+position.x+" "+position.y+" "+position.z+" " +
                "{Tags:[\"graph_var\"],alignment: \"center\", line_width: 200, see_through: 0b, shadow: 0b, text: '\""+text+"\"', text_opacity: -1b," +
                "transformation: {left_rotation: [0.0f, 0.0f, 0.36650127f, 0.9304176f],right_rotation: [0.0f, 0.0f, 0.0f, 1.0f], " +
                "scale: [1.0f, 1.0f, 1.0f], translation: [-0.1f, -0.4f, 0.0f]}}";
        OtherUtils.executeCommand(command);
    }
}
