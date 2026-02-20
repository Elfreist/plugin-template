package dev.hytalemodding.colonies.listener;

import com.hypixel.hytale.server.core.Message;
import dev.hytalemodding.colonies.config.ColonyConfig;
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
            sendPlayerMessage(player, "Tu dois tenir l’objet de sélection.");
            return;
        }

        String[] pos = extractWorldAndXZ(interactionEvent, player);
        String worldId = pos[0];
        int x = Integer.parseInt(pos[1]);
        int z = Integer.parseInt(pos[2]);
        String clickType = PlayerAccess.getClickType(interactionEvent);

        UUID playerId = PlayerAccess.getPlayerId(player);
        if (clickType.contains("LEFT")) {
            selectionManager.setA(playerId, worldId, x, z);
            sendPlayerMessage(player, "PosA définie: x=" + x + " z=" + z);
            return;
        }

        if (clickType.contains("RIGHT")) {
            try {
                selectionManager.setB(playerId, worldId, x, z);
                sendPlayerMessage(player, "PosB définie: x=" + x + " z=" + z);
            } catch (IllegalArgumentException e) {
                sendPlayerMessage(player, e.getMessage());
            }
            return;
        }

        // Fallback if API does not expose left/right action: sneak => B, else A.
        boolean sneaking = Boolean.TRUE.equals(invokeNoArg(player, "isSneaking"));
        if (sneaking) {
            try {
                selectionManager.setB(playerId, worldId, x, z);
                sendPlayerMessage(player, "PosB définie: x=" + x + " z=" + z);
            } catch (IllegalArgumentException e) {
                sendPlayerMessage(player, e.getMessage());
            }
        } else {
            selectionManager.setA(playerId, worldId, x, z);
            sendPlayerMessage(player, "PosA définie: x=" + x + " z=" + z);
        }
    }

    private String[] extractWorldAndXZ(Object event, Object player) {
        Object loc = invokeNoArg(event, "getBlockLocation");
        if (loc == null) {
            loc = invokeNoArg(event, "getLocation");
        }
        if (loc == null) {
            return PlayerAccess.getPlayerWorldAndXZ(player);
        }

        String worldId = String.valueOf(invokeAny(loc, "getWorldId", "getWorld", "world"));
        Number x = (Number) invokeAny(loc, "getX", "x");
        Number z = (Number) invokeAny(loc, "getZ", "z");
        return new String[]{worldId, String.valueOf(x.intValue()), String.valueOf(z.intValue())};
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
