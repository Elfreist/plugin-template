package dev.hytalemodding.colonies.listener;

import com.hypixel.hytale.server.core.Message;
import dev.hytalemodding.colonies.config.ColonyConfig;
import dev.hytalemodding.colonies.model.Location2D;
import dev.hytalemodding.colonies.service.SelectionManager;
import dev.hytalemodding.colonies.util.PlayerAccess;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Handles zone selection for admins using the configured wand item.
 */
public class ZoneWandListener {
    private final SelectionManager selectionManager;
    private final ColonyConfig config;

    public ZoneWandListener(SelectionManager selectionManager, ColonyConfig config) {
        this.selectionManager = selectionManager;
        this.config = config;
    }

    public void onPlayerInteract(Object interactionEvent) {
        Object player = PlayerAccess.getPlayerFromEvent(interactionEvent);
        if (!PlayerAccess.isAdmin(player, config.isOpOnly(), config.getPermissionNode())) {
            return;
        }

        String heldItemId = PlayerAccess.getHeldItemId(player).orElse("");
        if (!config.getWandItemId().equalsIgnoreCase(heldItemId)) {
            return;
        }

        Location2D loc = extractClickedLocation(interactionEvent, player);
        String clickType = PlayerAccess.getClickType(interactionEvent);

        UUID playerId = PlayerAccess.getPlayerId(player);
        if (clickType.contains("LEFT")) {
            selectionManager.setPosA(playerId, loc);
            sendPlayerMessage(player, "Pos A définie: " + loc.getX() + " " + loc.getZ() + " (" + loc.getWorldId() + ")");
            return;
        }

        if (clickType.contains("RIGHT")) {
            selectionManager.setPosB(playerId, loc);
            sendPlayerMessage(player, "Pos B définie: " + loc.getX() + " " + loc.getZ() + " (" + loc.getWorldId() + ")");
            return;
        }

        // Fallback if click side is unavailable: sneak = posB, normal = posA.
        boolean sneaking = Boolean.TRUE.equals(invokeNoArg(player, "isSneaking"));
        if (sneaking) {
            selectionManager.setPosB(playerId, loc);
            sendPlayerMessage(player, "Pos B définie (fallback sneak): " + loc.getX() + " " + loc.getZ());
        } else {
            selectionManager.setPosA(playerId, loc);
            sendPlayerMessage(player, "Pos A définie (fallback): " + loc.getX() + " " + loc.getZ());
        }
    }

    private Location2D extractClickedLocation(Object event, Object player) {
        Object loc = invokeNoArg(event, "getBlockLocation");
        if (loc == null) {
            loc = invokeNoArg(event, "getLocation");
        }
        if (loc == null) {
            return PlayerAccess.getPlayerLocation2D(player);
        }

        String worldId = String.valueOf(invokeAny(loc, "getWorldId", "getWorld", "world"));
        Number x = (Number) invokeAny(loc, "getX", "x");
        Number z = (Number) invokeAny(loc, "getZ", "z");
        return new Location2D(worldId, x.intValue(), z.intValue());
    }

    private void sendPlayerMessage(Object player, String text) {
        try {
            Method method = player.getClass().getMethod("sendMessage", Message.class);
            method.invoke(player, Message.raw(text));
        } catch (Exception ignored) {
        }
    }

    private static Object invokeAny(Object target, String... names) {
        for (String name : names) {
            Object value = invokeNoArg(target, name);
            if (value != null) {
                return value;
            }
        }
        throw new IllegalStateException("Cannot invoke required methods on " + target.getClass().getName());
    }

    private static Object invokeNoArg(Object target, String name) {
        if (target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(name);
            return method.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }
}
