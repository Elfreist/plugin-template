package dev.hytalemodding.colonies.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.hytalemodding.colonies.config.ColonyConfig;
import dev.hytalemodding.colonies.model.Colony;
import dev.hytalemodding.colonies.model.Location2D;
import dev.hytalemodding.colonies.model.Zone;
import dev.hytalemodding.colonies.service.ColonyRegistry;
import dev.hytalemodding.colonies.service.SelectionManager;
import dev.hytalemodding.colonies.service.SelectionManager.ZoneSelection;
import dev.hytalemodding.colonies.util.PlayerAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ColonyCommand extends AbstractCommand {
    private final ColonyRegistry registry;
    private final SelectionManager selectionManager;
    private final ColonyConfig config;

    public ColonyCommand(ColonyRegistry registry, SelectionManager selectionManager, ColonyConfig config) {
        super("colonie", "Create and manage colonies");
        this.registry = registry;
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
            context.sendMessage(Message.raw("Permission refusée."));
            return CompletableFuture.completedFuture(null);
        }

        List<String> args = extractArgs(context);
        if (args.size() < 2 || !"create".equalsIgnoreCase(args.get(0))) {
            context.sendMessage(Message.raw("Usage: /colonie create <familyId>"));
            return CompletableFuture.completedFuture(null);
        }

        String familyId = args.get(1);
        UUID playerId = PlayerAccess.getPlayerId(player);
        ZoneSelection selection = selectionManager.getSelection(playerId).orElse(null);
        if (selection == null || !selection.isComplete()) {
            context.sendMessage(Message.raw("Sélection incomplète: définis Pos A et Pos B d'abord."));
            return CompletableFuture.completedFuture(null);
        }

        Location2D a = selection.getA();
        Location2D b = selection.getB();
        if (!a.getWorldId().equals(b.getWorldId())) {
            context.sendMessage(Message.raw("Pos A et Pos B doivent être dans le même monde."));
            return CompletableFuture.completedFuture(null);
        }

        Zone zone = Zone.from(a, b);
        if (zone.getSizeX() < config.getMinSizeXZ() || zone.getSizeZ() < config.getMinSizeXZ()) {
            context.sendMessage(Message.raw("Zone trop petite. Min requis: " + config.getMinSizeXZ() + "x" + config.getMinSizeXZ()));
            return CompletableFuture.completedFuture(null);
        }

        Colony colony = registry.createColony(zone, familyId);
        selectionManager.clearSelection(playerId);

        context.sendMessage(Message.raw(
                "Colonie créée: " + colony.getId()
                        + " famille=" + familyId
                        + " zone=[" + zone.getPos1() + " -> " + zone.getPos2() + "]"
                        + " center=" + zone.getCenter()
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
