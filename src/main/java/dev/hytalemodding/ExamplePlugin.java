package dev.hytalemodding;

import com.hypixel.hytale.server.core.event.system.EventRegistry;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.hytalemodding.colonies.command.ColonyCommand;
import dev.hytalemodding.colonies.config.ColonyConfig;
import dev.hytalemodding.colonies.listener.ZoneWandListener;
import dev.hytalemodding.colonies.persistence.ColonyRepository;
import dev.hytalemodding.colonies.persistence.JsonColonyRepository;
import dev.hytalemodding.colonies.service.ColonyRegistry;
import dev.hytalemodding.colonies.service.SelectionManager;
import dev.hytalemodding.commands.ExampleCommand;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.nio.file.Path;

public class ExamplePlugin extends JavaPlugin {

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        ColonyConfig config = ColonyConfig.defaults();
        ColonyRepository repository = new JsonColonyRepository(Path.of("data"));
        ColonyRegistry colonyRegistry = new ColonyRegistry(repository);
        SelectionManager selectionManager = new SelectionManager();

        this.getCommandRegistry().registerCommand(new ColonyCommand(colonyRegistry, selectionManager, config));
        this.getCommandRegistry().registerCommand(new ExampleCommand("example", "An example command"));

        ZoneWandListener wandListener = new ZoneWandListener(selectionManager, config);
        registerInteractionListenerReflective(this.getEventRegistry(), wandListener);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerInteractionListenerReflective(EventRegistry eventRegistry, ZoneWandListener listener) {
        try {
            Class<?> eventClass = Class.forName("com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent");
            Method registerGlobal = eventRegistry.getClass().getMethod("registerGlobal", Class.class, java.util.function.Consumer.class);
            registerGlobal.invoke(eventRegistry, eventClass, (java.util.function.Consumer) listener::onPlayerInteract);
        } catch (Exception ignored) {
            // Event class differs by API version; listener can be rebound later without affecting colony core.
        }
    }
}
