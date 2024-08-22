package zip.sodium.home.config.builtin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import zip.sodium.home.config.EnumConfig;
import zip.sodium.home.config.data.MessageType;

public enum MessageConfig implements EnumConfig {
    TELEPORTING(
            "<green>Teleporting after <after> seconds...",
            MessageType.SUCCESS
    ),
    MOVED(
            "<red>You moved! Can't teleport.",
            MessageType.FAIL
    ),
    NOT_PLAYER(
            "<red>You need to be a player to run this command!",
            MessageType.PASS
    ),
    TELEPORTED(
            "<green>Teleported!",
            MessageType.SUCCESS
    ),
    NO_HOME(
            "<red><player> has no home!",
            MessageType.FAIL
    ),
    NO_PLAYER_EXISTS(
            "<red><player> has no home!",
            MessageType.FAIL
    ),
    INSUFFICIENT_PERMISSIONS(
            "<red>Insufficient permissions!",
            MessageType.FAIL
    ),
    SUCCESSFULLY_SET_HOME(
            "<green>Successfully set home!",
            MessageType.SUCCESS
    ),
    COULDNT_SET_HOME(
            "<red>Couldn't set home!",
            MessageType.FAIL
    ),
    SUCCESSFULLY_DELETED_HOME(
            "<green>Successfully deleted home!",
            MessageType.SUCCESS
    ),
    COULDNT_DELETE_HOME(
            "<red>Couldn't delete home!",
            MessageType.FAIL
    ),;

    private static YamlConfiguration configFile;
    public static void saveDefaults(final Plugin plugin, final String fileName) {
        EnumConfig.trySaveDefaults(plugin, fileName, values());

        configFile = EnumConfig.tryGetConfigFile(plugin, fileName);
    }

    private Object cache = null;
    private final @NotNull Object defaultValue;

    private final MessageType messageType;

    MessageConfig(final @NotNull String defaultValue, final MessageType messageType) {
        this.defaultValue = defaultValue;
        this.messageType = messageType;
    }

    public boolean send(final CommandSender sender, final TagResolver... resolver) {
        sender.sendMessage(
                LegacyComponentSerializer.legacySection()
                        .serialize(get(resolver))
        );

        messageType.trigger(sender);

        return messageType.shouldPass();
    }

    public @NotNull Component get(final TagResolver... resolver) {
        return MiniMessage.miniMessage().deserialize(
                get(FileConfiguration::getString),
                resolver
        );
    }

    @Override
    public Object cache() {
        return cache;
    }

    @Override
    public void setCache(final Object cache) {
        this.cache = cache;
    }

    @Override
    public @NotNull Object defaultValue() {
        return defaultValue;
    }

    @Override
    public FileConfiguration ymlConfiguration() {
        return configFile;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
