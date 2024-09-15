package net.mat0u5.do2manager.world;

import java.util.ArrayList;
import java.util.List;

public class DO2RunAbridged {
    public String run_type = null;
    public List<String> runners = new ArrayList<>();
    public List<String> finishers = new ArrayList<>();
    public int id = -1;
    public int difficulty = -1;
    public int run_number = -1;
    public int run_length = -1;
    public int embers_counted = -1;
    public int crowns_counted = -1;
    public int compass_level = -1;
    public int getRunNum() {
        return run_number;
    }
    public boolean getSuccess() {
        return !String.join("",finishers).isEmpty();
    }
    public boolean getSuccessFor(String uuid) {
        return finishers.contains(uuid);
    }
    public boolean isLackey() {
        if (runners == null) return false;
        if (runners.isEmpty()) return false;
        if (runners.size() == 1) return false;
        return true;
    }
}
