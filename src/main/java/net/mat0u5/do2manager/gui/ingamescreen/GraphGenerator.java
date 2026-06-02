package net.mat0u5.do2manager.gui.ingamescreen;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static net.mat0u5.do2manager.Main.server;

public class GraphGenerator {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM. dd", Locale.ENGLISH);
    public static final AABB graphBox = new AABB(-528.0989, 105.5056, 1946.1, -512.9092, 114.6702, 1946.1);

    public static final int Y_AXIS_LABELS = 5;
    public static final int X_AXIS_POINTS = 10;

    public static void generateGraph(ServerLevel world, List<DO2RunAbridged> runs, String metric, boolean noFilters, ServerPlayer player) {
        OtherUtils.executeCommand("kill @e[tag=graph_var]");
        if (Main.statsViewerDisabled) return;
        if (runs == null) return;
        if (runs.isEmpty()) return;
        runs.sort(Comparator.comparingInt(DO2RunAbridged::getRunNum));
        String uuid = player.getStringUUID();

        // Step 1: Calculate graph boundaries
        double minX = graphBox.minX;
        double maxX = graphBox.maxX;
        double minY = graphBox.minY;
        double maxY = graphBox.maxY;
        int successTracked = 0;
        int runsTotalTracked = 0;

        // Get the min and max timestamps
        long minTimestamp = Integer.MAX_VALUE;
        long maxTimestamp = Integer.MIN_VALUE;
        for (DO2RunAbridged run : runs) {
            long timestamp = run.timestampDate();
            if (timestamp != -1) {
                minTimestamp = Math.min(minTimestamp, timestamp);
                maxTimestamp = Math.max(maxTimestamp, timestamp);
            }
            runsTotalTracked++;
            if (run.getSuccessFor(uuid)) {
                successTracked++;
            }
        }
        double success = 0;
        double runsTotal = 0;


        // Step 2: Calculate y-axis range based on the metric
        boolean fixedMaxMetric = false;
        double maxMetricValue = -1;
        if (metric.equalsIgnoreCase("winpercent")) {
            maxMetricValue = 100;
            fixedMaxMetric = true;
            Integer successSaved = ScoreboardUtils.getPlayerScore(server, player, "VictoryCount");
            Integer runsTotalSaved = ScoreboardUtils.getPlayerScore(server, player, "TotalRunCount");
            if (successSaved != null && runsTotalSaved != null && noFilters) {
                if (runsTotalSaved > runsTotalTracked && successSaved >= successTracked) {
                    success = successSaved - successTracked;
                    runsTotal = runsTotalSaved - runsTotalTracked;
                }
            }
        }

        // Step 3: Calculate data points
        double currentDataPoints = 0;
        double embers = 0;
        double crowns = 0;


        List<Vec3> graphPointsRaw = new ArrayList<>();
        for (DO2RunAbridged run : runs) {
            long timestamp = run.timestampDate();
            if (timestamp == -1) continue;

            // Normalize x (timestamps) to graph range
            double normalizedX = minX + ((timestamp - minTimestamp) / (double) (maxTimestamp - minTimestamp)) * (maxX - minX);
            currentDataPoints++;
            runsTotal++;
            embers += run.embers_counted;
            crowns += run.crowns_counted;
            if (run.getSuccessFor(uuid)) {
                success++;
            }
            // Normalize y (metric values) to graph range
            double metricValue = switch (metric) {
                case "winpercent" -> (success*100) / runsTotal;
                case "runs" -> currentDataPoints;
                case "embers" -> run.embers_counted;
                case "crowns" -> run.crowns_counted;
                case "totalembers" -> embers;
                case "totalcrowns" -> crowns;
                default -> 0;
            };

            // Add to graph points
            graphPointsRaw.add(new Vec3(normalizedX, metricValue, graphBox.minZ));
        }

        List<Vec3> graphPointsAveraged = new ArrayList<>();
        for (Vec3 point : graphPointsRaw) {
            if (!fixedMaxMetric) {
                double metricValue = point.y;
                maxMetricValue = Math.max(metricValue, maxMetricValue);
            }
            //TODO Add averaging maybe?
            graphPointsAveraged.add(point);
        }


        List<Vec3> graphPoints = new ArrayList<>();
        for (Vec3 point : graphPointsAveraged) {
            double metricValue = point.y;
            double normalizedY = minY + (metricValue / maxMetricValue) * (maxY - minY);
            graphPoints.add(new Vec3(point.x, normalizedY, point.z));
        }

        // Step 4: Summon display entities to connect points
        Vec3 lastStart = null;
        boolean lastFailed = false;
        for (int i = 1; i < graphPoints.size(); i++) {
            Vec3 start = graphPoints.get(i - 1);
            Vec3 end = graphPoints.get(i);
            if (lastFailed) {
                lastFailed = false;
                start = lastStart;
            }
            if (!summonLine(world, start, end)) {
                lastStart = start;
                lastFailed = true;
            }
        }

        // Step 5: Add axis labels (optional)
        addAxisLabels(world, minX, maxX, minY, maxY, minTimestamp, maxTimestamp, metric, maxMetricValue);
    }

    private static void addAxisLabels(ServerLevel world, double minX, double maxX, double minY, double maxY,
                                      long minTimestamp, long maxTimestamp, String metric, double maxMetricValue) {
        // Add X-axis labels (timestamps)
        for (int i = 0; i <= X_AXIS_POINTS; i++) {
            long t = minTimestamp + (long)(i * (maxTimestamp - minTimestamp) / (double) X_AXIS_POINTS);
            double posX = minX + i * (maxX - minX) / X_AXIS_POINTS;

            summonTextRotated(world, new Vec3(posX, minY - 0.1, 1946.15), formatter.format(LocalDateTime.ofEpochSecond(t,0, ZoneOffset.UTC)));
        }

        // Add Y-axis labels (metric values)
        for (int i = 0; i <= Y_AXIS_LABELS; i++) {
            double metricValue = i * maxMetricValue / Y_AXIS_LABELS;
            double posY = minY + i * (maxY - minY) / Y_AXIS_LABELS;

            if (i != 0) summonTextDots(world, new Vec3(-520.8,posY-0.09,1946.15), ". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .");

            String text = String.valueOf((int)metricValue);
            if (metric.equalsIgnoreCase("winpercent")) text += " %";
            summonText(world, new Vec3(minX - 0.8, posY-0.15, 1946.15), text);
        }
    }

    public static float[] getQuaternion(float angleRadians, float axisX, float axisY, float axisZ) {

        // Normalize the axis
        float length = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
        axisX /= length;
        axisY /= length;
        axisZ /= length;

        // Calculate quaternion components
        float halfAngle = angleRadians / 2;
        float sinHalfAngle = (float) Math.sin(halfAngle);
        float cosHalfAngle = (float) Math.cos(halfAngle);

        float qX = axisX * sinHalfAngle;
        float qY = axisY * sinHalfAngle;
        float qZ = axisZ * sinHalfAngle;
        float qW = cosHalfAngle;

        return new float[]{qX, qY, qZ, qW};
    }

    private static boolean summonLine(ServerLevel world, Vec3 start, Vec3 end) {
        double distance = start.distanceTo(end);
        if (distance < 0.01) return false;
        double angle = Math.atan2(end.y - start.y, end.x - start.x)+Math.PI/2;
        float[] quaternion = getQuaternion((float) angle, 0, 0, 1);
        Vec3 center = start.lerp(end, 0.5);
        //summonTextNoBG(world, start, ".");
        String command = "summon minecraft:item_display "+center.x + " " + center.y + " " + center.z + " {Tags:[\"graph_var\"],item: {count: 1, id: \"minecraft:black_concrete\"},transformation: {left_rotation: [0.0f, 0.0f, "+quaternion[2]+"f, "+quaternion[3]+"f], right_rotation: [0.0f, 0.0f, 0.0f, 1.0f], scale: [0.04f, "+distance+"f, 0.04f], translation: [0.0f, 0.0f, 0.0f]}}";
        OtherUtils.executeCommand(command);
        return true;
    }

    private static void summonTextNoBG(ServerLevel world, Vec3 position, String text) {
        String command = "summon minecraft:text_display "+position.x+" "+position.y+" "+position.z+" " +
                "{Tags:[\"graph_var\"],alignment: \"center\", background: 0, default_background: 0b, line_width: 200, see_through: 0b, shadow: 0b, text: '\""+text+"\"', text_opacity: -1b}";
        OtherUtils.executeCommand(command);
    }
    private static void summonTextDots(ServerLevel world, Vec3 position, String text) {
        String command = "summon minecraft:text_display "+position.x+" "+position.y+" "+position.z+" " +
                "{Tags:[\"graph_var\"],alignment: \"center\", background: 0, default_background: 0b, line_width: 200, see_through: 0b, shadow: 0b, text: '\""+text+"\"', text_opacity: -1b,transformation: {left_rotation: [0.0f, 0.0f, 0.0f, 1.0f], right_rotation: [0.0f, 0.0f, 0.0f, 1.0f], scale: [3.192f, 1.0f, 1.0f], translation: [0.0f, 0.0f, 0.0f]}}";
        OtherUtils.executeCommand(command);
    }
    private static void summonText(ServerLevel world, Vec3 position, String text) {
        String command = "summon minecraft:text_display "+position.x+" "+position.y+" "+position.z+" " +
                "{Tags:[\"graph_var\"],alignment: \"center\", line_width: 200, see_through: 0b, shadow: 0b, text: '\""+text+"\"', text_opacity: -1b}";
        OtherUtils.executeCommand(command);
    }
    private static void summonTextRotated(ServerLevel world, Vec3 position, String text) {
        String command = "summon minecraft:text_display "+position.x+" "+position.y+" "+position.z+" " +
                "{Tags:[\"graph_var\"],alignment: \"center\", line_width: 200, see_through: 0b, shadow: 0b, text: '\""+text+"\"', text_opacity: -1b," +
                "transformation: {left_rotation: [0.0f, 0.0f, 0.36650127f, 0.9304176f],right_rotation: [0.0f, 0.0f, 0.0f, 1.0f], " +
                "scale: [1.0f, 1.0f, 1.0f], translation: [-0.15f, -0.5f, 0.0f]}}";
        OtherUtils.executeCommand(command);
    }
}
