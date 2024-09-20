package net.mat0u5.do2manager.world;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.gui.GuiInventory_Database;
import net.mat0u5.do2manager.gui.GuiPlayerSpecific;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FakeSign {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static List<BlockPos> fakeSigns = new ArrayList<>();
    public static void openFakeSign(ServerPlayerEntity player)  {
        // Create a new sign block entity at an arbitrary position
        World world = player.getWorld();

        BlockPos pos = findSuitableSignPosition(world, player.getBlockPos());
        if (pos == null) {
            player.sendMessage(Text.of("Could not find a suitable position for the sign."), false);
            return;
        }

        world.setBlockState(pos, Blocks.OAK_SIGN.getDefaultState());

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SignBlockEntity) {
            SignBlockEntity sign = (SignBlockEntity) blockEntity;

            SignText signText = new SignText();

            String playerFilter = String.join(", ",Main.openGuis.get(player).guiDatabase.filter_player);
            List<String> playerFilterText = splitStringToFit(playerFilter);
            if (!playerFilter.isEmpty()){
                signText = signText.withMessage(0,Text.of(playerFilterText.get(0)));
                if (playerFilterText.size() >1) signText = signText.withMessage(1,Text.of(playerFilter.replaceFirst(playerFilterText.get(0),"")));
            }
            signText = signText.withMessage(2,Text.of("^^^^^^^^^^^^^^^"));
            signText = signText.withMessage(3,Text.of("Enter player name"));
            sign.setText(signText, false);
            sign.markDirty();
            sign.setEditor(player.getUuid());
            MinecraftServer server = player.getServer();
            fakeSigns.add(pos);
            if (server != null) {
                scheduler.schedule(() -> server.execute(() -> {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeBlockPos(pos);
                    buf.writeBoolean(false); // This value is for the "front" field, adapt as needed

                    player.networkHandler.sendPacket(new SignEditorOpenS2CPacket(buf));
                    player.networkHandler.sendPacket(sign.toUpdatePacket());
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
    private static BlockPos findSuitableSignPosition(World world, BlockPos playerPos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        mutable.set(playerPos.getX(), playerPos.getY(), playerPos.getZ());
        if (world.getBlockState(mutable).isAir()) return mutable.toImmutable();
        mutable.set(playerPos.getX(), playerPos.getY()+1, playerPos.getZ());
        if (world.getBlockState(mutable).isAir()) return mutable.toImmutable();

        // Check blocks around the player in a radius
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    mutable.set(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                    if (world.getBlockState(mutable).isAir()) {
                        return mutable.toImmutable();
                    }
                }
            }
        }

        return null; // No suitable position found
    }
    public static void onSignUpdate(SignText signText, CallbackInfoReturnable<Void> ci, SignBlockEntity sign) {
        MinecraftServer server = sign.getWorld().getServer();
        UUID editorUuid = sign.getEditor();
        if (editorUuid == null || server == null) return;
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(editorUuid);
        if (player == null) return;
        BlockPos pos = sign.getPos();

        if (!fakeSigns.contains(pos)) return;

        sign.getWorld().setBlockState(sign.getPos(),Blocks.AIR.getDefaultState());
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
                        nameChoice.addAll(suggestedNames);
                    }
                }
                if (playerName.isEmpty()) continue;
                guiDatabase.filter_player.add(playerName);
                guiDatabase.filter_player_uuid.add(Main.getUUIDFromName(playerName));
            }
            if (!nameChoice.isEmpty()) {
                guiDatabase.playerChoiceInventory(nameChoice);
                guiDatabase.openRunInventoryNoUpdate(player);
                return;
            }
        }
        guiDatabase.openRunInventoryNoUpdate(player);
        guiDatabase.updateSearch();
        guiDatabase.populateRunInventory();
    }
}
