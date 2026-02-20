package dev.hytalemodding.colonies.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class PlayerAccess {
    private PlayerAccess() {
    }

    public static UUID getPlayerId(Object player) {
        Object raw = invokeAny(player, List.of("getUniqueId", "getId", "getUuid"));
        if (raw instanceof UUID id) {
            return id;
        }
        throw new IllegalStateException("Cannot resolve player UUID");
    }

    public static boolean isAdmin(Object player, boolean opOnly, String permissionNode) {
        if (!opOnly) {
            return true;
        }

        Boolean op = asBoolean(invokeAnyOrNull(player, List.of("isOp", "isOperator")));
        if (Boolean.TRUE.equals(op)) {
            return true;
        }

        Object hasPerm = invokeByNameWithString(player, List.of("hasPermission", "hasPerm"), permissionNode);
        return Boolean.TRUE.equals(asBoolean(hasPerm));
    }

    public static Optional<String> getHeldItemId(Object player) {
        Object item = invokeAnyOrNull(player, List.of("getItemInMainHand", "getHeldItem", "getMainHandItem"));
        if (item == null) {
            return Optional.empty();
        }
        Object id = invokeAnyOrNull(item, List.of("getId", "getType", "getItemId", "name"));
        return Optional.ofNullable(id).map(Object::toString);
    }

    public static String[] getPlayerWorldAndXZ(Object player) {
        Object location = invokeAny(player, List.of("getLocation", "getPosition"));
        String world = String.valueOf(invokeAny(location, List.of("getWorldId", "getWorld", "world")));
        int x = ((Number) invokeAny(location, List.of("getX", "x"))).intValue();
        int z = ((Number) invokeAny(location, List.of("getZ", "z"))).intValue();
        return new String[]{world, String.valueOf(x), String.valueOf(z)};
    }

    public static Object getPlayerFromEvent(Object event) {
        return invokeAny(event, List.of("getPlayer", "player", "getEntity"));
    }

    public static String getClickType(Object event) {
        Object action = invokeAnyOrNull(event, List.of("getAction", "getClickType", "getButton"));
        if (action == null) {
            return "UNKNOWN";
        }
        return action.toString().toUpperCase();
    }

    private static Object invokeAny(Object target, List<String> names) {
        Object value = invokeAnyOrNull(target, names);
        if (value == null) {
            throw new IllegalStateException("Cannot call any of " + names + " on " + target.getClass().getName());
        }
        return value;
    }

    private static Object invokeAnyOrNull(Object target, List<String> names) {
        if (target == null) {
            return null;
        }
        for (String name : names) {
            try {
                Method m = target.getClass().getMethod(name);
                return m.invoke(target);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static Object invokeByNameWithString(Object target, List<String> names, String arg) {
        if (target == null) {
            return null;
        }
        for (String name : names) {
            try {
                Method m = target.getClass().getMethod(name, String.class);
                return m.invoke(target, arg);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static Boolean asBoolean(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value == null) {
            return null;
        }
        return Arrays.asList("true", "yes", "1").contains(value.toString().toLowerCase());
    }
}
