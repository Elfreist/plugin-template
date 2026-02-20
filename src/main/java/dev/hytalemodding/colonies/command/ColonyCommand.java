package dev.hytalemodding.colonies.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.hytalemodding.colonies.config.ColonyConfig;
import dev.hytalemodding.colonies.model.Colony;
import dev.hytalemodding.colonies.model.Zone;
import dev.hytalemodding.colonies.service.ColonyRegistry;
import dev.hytalemodding.colonies.service.SelectionManager;
import dev.hytalemodding.colonies.service.SelectionManager.Selection;
import dev.hytalemodding.colonies.util.PlayerAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ColonyCommand extends AbstractCommand {
    private final ColonyRegistry colonyRegistry;
    private final SelectionManager selectionManager;
    private final ColonyConfig config;

    public ColonyCommand(ColonyRegistry colonyRegistry, SelectionManager selectionManager, ColonyConfig config) {
        super("colonie", "Gestion des colonies");
        this.colonyRegistry = colonyRegistry;
        this.selectionManager = selectionManager;
        this.config = config;
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        Object player;
        try {
            player = context.getPlayer();
        } catch (Exception e) {
            context.sendMessage(Message.raw("Commande joueur uniquement."));
            return CompletableFuture.completedFuture(null);
        }

        if (!PlayerAccess.isAdmin(player, config.isOpOnly(), config.getPermissionNode())) {
            context.sendMessage(Message.raw("Tu n’as pas la permission."));
            return CompletableFuture.completedFuture(null);
        }

        List<String> args = extractArgs(context);
        if (args.size() < 2 || !"create".equalsIgnoreCase(args.get(0))) {
            context.sendMessage(Message.raw("Usage: /colonie create <familyId>"));
            return CompletableFuture.completedFuture(null);
        }

        String familyId = args.get(1);
        UUID playerId = PlayerAccess.getPlayerId(player);
        Selection selection = selectionManager.get(playerId).orElse(null);
        if (selection == null || !selection.isComplete()) {
            context.sendMessage(Message.raw("Tu dois définir PosA (clic gauche) et PosB (clic droit)."));
            return CompletableFuture.completedFuture(null);
        }

        if (selection.getWorldId() == null) {
            context.sendMessage(Message.raw("PosA et PosB doivent être dans le même monde."));
            return CompletableFuture.completedFuture(null);
        }

        Zone zone = Zone.from(
                selection.getWorldId(),
                selection.getAx(),
                selection.getAz(),
                selection.getBx(),
                selection.getBz()
        );

        if ((zone.getMaxX() - zone.getMinX()) < config.getMinSizeXZ()
                || (zone.getMaxZ() - zone.getMinZ()) < config.getMinSizeXZ()) {
            context.sendMessage(Message.raw("Zone trop petite. Min=" + config.getMinSizeXZ() + "."));
            return CompletableFuture.completedFuture(null);
        }

        Colony colony = colonyRegistry.createColony(zone, familyId);
        selectionManager.clear(playerId);
        context.sendMessage(Message.raw(
                "Colonie créée: id=" + colony.getId()
                        + " famille=" + familyId
                        + " centre=(" + zone.getCenterX() + "," + zone.getCenterZ() + ")"
        ));

        return CompletableFuture.completedFuture(null);
    }

    private List<String> extractArgs(CommandContext context) {
        try {
            Method m = context.getClass().getMethod("getArguments");
            Object value = m.invoke(context);
            if (value instanceof List<?> list) {
                return list.stream().map(String::valueOf).toList();
            }
            if (value instanceof String[] arr) {
                return List.of(arr);
            }
        } catch (Exception ignored) {
        }
        return List.of();
    }
}
