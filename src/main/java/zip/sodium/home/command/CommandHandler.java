package zip.sodium.home.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import zip.sodium.home.Entrypoint;
import zip.sodium.home.command.builtin.DelHomeCommand;
import zip.sodium.home.command.builtin.HomeCommand;
import zip.sodium.home.command.builtin.SetHomeCommand;

public final class CommandHandler {
    private CommandHandler() {}

    private static CommandMap findCommandMap() {
        final CommandMap map;
        try {
            final var server = Bukkit.getServer();

            final var field = server
                    .getClass()
                    .getDeclaredField("commandMap");
            field.setAccessible(true);

            map = (CommandMap) field.get(server);
        } catch (final ReflectiveOperationException e) {
            Entrypoint.disable("Couldn't find command map!", e);

            return null;
        }

        if (map == null) {
            Entrypoint.disable("Couldn't find command map!");

            return null;
        }

        return map;
    }

    public static void acknowledge() {
        final var map = findCommandMap();
        if (map == null)
            return;

        map.register(HomeCommand.NAME, HomeCommand.INSTANCE);
        map.register(SetHomeCommand.NAME, SetHomeCommand.INSTANCE);
        map.register(DelHomeCommand.NAME, DelHomeCommand.INSTANCE);
    }
}
