package net.mat0u5.do2manager.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import net.minecraft.util.Hand;

import static net.minecraft.server.command.CommandManager.literal;


public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("decked-out")
                .executes(context -> Command.execute(context.getSource()))
                .then(literal("getItem")
                    .executes(context -> Command.executeGetItem(context.getSource()))
                )
                .then(literal("setItem")
                    .executes(context -> Command.executeSetItem(context.getSource()))
                )
                .then(literal("addRun")
                    .executes(context -> Command.executeAddRun(context.getSource()))
                )
                .then(literal("getInv")
                    .then(CommandManager.argument("runNum", IntegerArgumentType.integer())
                        .executes(context -> executeGetInv(context.getSource(), IntegerArgumentType.getInteger(context, "runNum")))
                    )
                )
        );
        //.requires(source -> source.hasPermissionLevel(2))
    }
    public static int execute(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DatabaseManager.printAllPlayers();
        try {
            DatabaseManager.updateTable();
        }catch (Exception e){}
        self.sendMessage(Text.translatable("§6Command Worked.."));
        return -1;
    }
    public static int executeGetItem(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        List<ItemStack> playerItems = DatabaseManager.getItemsByPlayerUUID(self.getUuidAsString());
        for (ItemStack item : playerItems) {
            ItemManager.giveItemStack(self,item);
        }

        self.sendMessage(Text.translatable("§6Command Worked.."));
        return -1;
    }
    public static int executeSetItem(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DatabaseManager.addItem(self.getUuidAsString(),self.getStackInHand(Hand.MAIN_HAND));

        self.sendMessage(Text.translatable("§6Command Worked.."));
        return -1;
    }
    public static int executeAddRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        int runNum = Integer.parseInt(Main.config.getProperty("runNum"));

        DatabaseManager.addRun(runNum, "casual",self.getUuidAsString(),null, 32456);
        DatabaseManager.addRunDetailed(runNum, "card1,card2",new ItemStack(Items.COMPASS, 1), new ItemStack(Items.IRON_NUGGET, 1), self.getStackInHand(Hand.MAIN_HAND), self, "-520 69 420", "ravager hihi");
        DatabaseManager.addRunSpeedrun(runNum,1,2,3,4,5,6,7,8);

        runNum++;
        Main.config.setProperty("runNum", String.valueOf(runNum));

        self.sendMessage(Text.translatable("§6Command Worked.."));
        return -1;
    }
    public static int executeGetInv(ServerCommandSource source, int runNum) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        ItemManager.giveItemStack(self, DatabaseManager.getInvByRunNumber(self,runNum));

        self.sendMessage(Text.translatable("§6Command Worked.."));
        return -1;
    }
}
