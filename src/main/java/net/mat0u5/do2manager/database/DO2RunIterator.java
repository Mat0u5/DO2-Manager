package net.mat0u5.do2manager.database;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.DO2RunAbridged;

import java.util.ArrayList;
import java.util.List;

public class DO2RunIterator {

    public void processRun(DO2Run run) {
        System.out.println("Default Processing... "+run.run_number);
    }
    public void finishedProcessing() {
        System.out.println("Default Finished Processing... ");
    }
    public void start() {
        if (!Main.reloadedRuns) {
            Main.reloadAllAbridgedRunsAsync().thenRun(() -> {
                mainLoop();
            });
        }
        else {
            mainLoop();
        }
    }
    public void mainLoop() {
        List<DO2RunAbridged> batch = new ArrayList<>();
        int pos = 0;
        for (DO2RunAbridged abridged : Main.allAbridgedRuns) {
            batch.add(abridged);
            if (pos%20==0 || pos >= Main.allAbridgedRuns.size()) {
                processBatch(batch);
            }
            pos++;
        }
        finishedProcessing();
    }
    public void processBatch(List<DO2RunAbridged> batch) {
        List<DO2Run> actualRuns = DatabaseManager.getRunsByAbridgedRuns(batch);
        for (DO2Run run : actualRuns) {
            processRun(run);
        }
        batch.clear();
    }
}
