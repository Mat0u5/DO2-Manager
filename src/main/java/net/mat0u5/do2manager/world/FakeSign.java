package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.gui.GuiInventory_Database;
import net.mat0u5.do2manager.gui.GuiPlayerSpecific;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FakeSign {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static List<BlockPos> fakeSigns = new ArrayList<>();
    public static void openFakeSign(ServerPlayer player)  {
        // Create a new sign block entity at an arbitrary position
        Level world = player.level();

        BlockPos pos = findSuitableSignPosition(world, player.blockPosition());
        if (pos == null) {
            player.displayClientMessage(Component.nullToEmpty("Could not find a suitable position for the sign."), false);
            return;
        }

        world.setBlockAndUpdate(pos, Blocks.OAK_SIGN.defaultBlockState());

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SignBlockEntity) {
            SignBlockEntity sign = (SignBlockEntity) blockEntity;

            SignText signText = new SignText();

            String playerFilter = String.join(", ",Main.openGuis.get(player).guiDatabase.filter_player);
            List<String> playerFilterText = splitStringToFit(playerFilter);
            if (!playerFilter.isEmpty()){
                signText = signText.setMessage(0,Component.nullToEmpty(playerFilterText.get(0)));
                if (playerFilterText.size() >1) signText = signText.setMessage(1,Component.nullToEmpty(playerFilter.replaceFirst(playerFilterText.get(0),"")));
            }
            signText = signText.setMessage(2,Component.nullToEmpty("^^^^^^^^^^^^^^^"));
            signText = signText.setMessage(3,Component.nullToEmpty("Enter player name"));
            sign.setText(signText, false);
            sign.setChanged();
            sign.setAllowedPlayerEditor(player.getUUID());
            MinecraftServer server = player.level().getServer();
            fakeSigns.add(pos);
            if (server != null) {
                scheduler.schedule(() -> server.execute(() -> {
                    player.connection.send(new ClientboundOpenSignEditorPacket(pos,false));
                    player.connection.send(sign.getUpdatePacket());
                }), 40, TimeUnit.MILLISECONDS);
            }
        }
    }
    private static List<String> splitStringToFit(String text) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        double currentWidth = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            double charWidth = getCharacterWidth(c);

            if (currentWidth + charWidth <= 1) {
                currentLine.append(c);
                currentWidth += charWidth;
            } else {
                // Start a new line
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                currentLine.append(c);
                currentWidth = charWidth;
            }
        }

        // Add the last line
        lines.add(currentLine.toString());

        return lines;
    }

    private static double getCharacterWidth(char c) {
        switch (c) {
            case 'k':
            case 'f':
                return (double) 1/18;
            case 't':
            case 'I':
            case ' ':
                return (double) 1/22;
            case 'l':
                return (double) 1 /30;
            case 'i':
                return (double) 1 /45;
            case ',':
                return (double) 1 /67;
            default:
                return (double)  1/15;
        }
    }
    private static BlockPos findSuitableSignPosition(Level world, BlockPos playerPos) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        mutable.set(playerPos.getX(), playerPos.getY(), playerPos.getZ());
        if (world.getBlockState(mutable).isAir()) return mutable.immutable();
        mutable.set(playerPos.getX(), playerPos.getY()+1, playerPos.getZ());
        if (world.getBlockState(mutable).isAir()) return mutable.immutable();

        // Check blocks around the player in a radius
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    mutable.set(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                    if (world.getBlockState(mutable).isAir()) {
                        return mutable.immutable();
                    }
                }
            }
        }

        return null; // No suitable position found
    }
    public static void onSignUpdate(SignText signText, CallbackInfoReturnable<Void> ci, SignBlockEntity sign) {
        MinecraftServer server = sign.getLevel().getServer();
        UUID editorUuid = sign.getPlayerWhoMayEdit();
        if (editorUuid == null || server == null) return;
        ServerPlayer player = server.getPlayerList().getPlayer(editorUuid);
        if (player == null) return;
        BlockPos pos = sign.getBlockPos();

        if (!fakeSigns.contains(pos)) return;

        sign.getLevel().setBlockAndUpdate(sign.getBlockPos(),Blocks.AIR.defaultBlockState());
        fakeSigns.remove(pos);

        GuiPlayerSpecific playerGui = Main.openGuis.get(player);
        GuiInventory_Database guiDatabase = Main.openGuis.get(player).guiDatabase;

        playerGui.filter_player.clear();
        playerGui.filter_player_uuid.clear();
        String query = signText.getMessage(0,false).getString()+signText.getMessage(1,false).getString();
        List<String> signNames = new ArrayList<>();
        if (query.contains(",")) {
            for (String playerName : query.split(",")) {
                signNames.add(playerName.trim());
            }
        }
        else if (!query.trim().isEmpty()){
            signNames.add(query.trim());
        }
        if (signNames.isEmpty()) {
            guiDatabase.filter_player.clear();
            guiDatabase.filter_player_uuid.clear();
        }
        else {
            List<String> nameChoice = new ArrayList<>();
            HashMap<String, Integer> playerRuns = new HashMap<>();

            for (String nameRaw : signNames) {
                String playerName = "";
                if (Main.allPlayers.containsValue(nameRaw)) {
                    playerName = nameRaw;
                }
                else {
                    List<String> suggestedNames = new ArrayList<>();
                    for (String suggested : Main.allPlayers.values()) {
                        if (suggested.toLowerCase().contains(nameRaw.toLowerCase())) {
                            suggestedNames.add(suggested);
                        }
                    }
                    if (suggestedNames.isEmpty()) {
                        guiDatabase.filter_player.clear();
                        guiDatabase.filter_player_uuid.clear();
                    }
                    else if (suggestedNames.size() == 1) {
                        playerName = suggestedNames.get(0);
                    }
                    else {
                        for (String name : suggestedNames) {
                            int runs = getRunNumByPlayer(name);
                            if (runs == 0) continue;
                            playerRuns.put(name,runs);
                        }
                    }
                }
                if (playerRuns.size() == 1) {
                    playerName = (String) playerRuns.keySet().toArray()[0];
                }
                if (playerName.isEmpty()) continue;
                guiDatabase.filter_player.add(playerName);
                guiDatabase.filter_player_uuid.add(OtherUtils.getPlayerUUIDFromName(playerName));
            }
            if (playerRuns.size() > 1) {
                guiDatabase.playerChoiceInventory(playerRuns);
                guiDatabase.openRunInventoryNoUpdate(player);
                return;
            }
        }
        guiDatabase.openRunInventoryNoUpdate(player);
        guiDatabase.updateSearch();
        guiDatabase.populateRunInventory();
    }
    public static int getRunNumByPlayer(String playerName) {
        int num = 0;
        String playerUUID = OtherUtils.getPlayerUUIDFromName(playerName);
        for (DO2RunAbridged run : Main.allAbridgedRuns) {
            if (run.runners.contains(playerUUID)) {
                num++;
            }
        }
        return num;
    }
    public static void shutdownExecutor() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
