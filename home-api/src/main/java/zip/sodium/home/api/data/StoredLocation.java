package zip.sodium.home.api.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record StoredLocation(@NotNull UUID worldId, double x, double y, double z, float yaw, float pitch) { }
