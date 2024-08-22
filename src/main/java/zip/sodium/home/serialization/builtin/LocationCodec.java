package zip.sodium.home.serialization.builtin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.msgpack.core.MessagePack;
import zip.sodium.home.serialization.Codec;
import zip.sodium.home.serialization.data.Result;
import zip.sodium.home.serialization.exception.DecodingException;
import zip.sodium.home.serialization.exception.EncodingException;
import zip.sodium.home.util.CompressionUtil;

import java.io.IOException;
import java.util.UUID;

public final class LocationCodec implements Codec<Location, byte[]> {
    public static final Codec<Location, byte[]> INSTANCE = new LocationCodec();

    private LocationCodec() {}

    @Override
    public @NotNull Result<byte[], EncodingException> encode(final @NotNull Location location) {
        final var world = location.getWorld();
        if (world == null)
            return Result.err(
                    EncodingException.from("World is null")
            );

        final var worldUuid = world.getUID();

        final byte[] data;
        try (final var packer = MessagePack.newDefaultBufferPacker()) {
            packer.packLong(worldUuid.getMostSignificantBits());
            packer.packLong(worldUuid.getLeastSignificantBits());
            packer.packDouble(location.getX());
            packer.packDouble(location.getY());
            packer.packDouble(location.getZ());
            packer.packFloat(location.getYaw());
            packer.packFloat(location.getPitch());

            data = packer.toByteArray();
        } catch (final IOException e) {
            return Result.err(
                    EncodingException.from(e)
            );
        }

        try {
            return Result.ok(CompressionUtil.compress(data));
        } catch (final IOException e) {
            return Result.err(
                    EncodingException.from(e)
            );
        }
    }

    @Override
    public @NotNull Result<Location, DecodingException> decode(byte @NotNull [] data) {
        try {
            data = CompressionUtil.decompress(data);
        } catch (final IOException e) {
            return Result.err(
                    DecodingException.from(e)
            );
        }

        try (final var unpacker = MessagePack.newDefaultUnpacker(data)) {
            final var world = Bukkit.getWorld(
                    new UUID(unpacker.unpackLong(), unpacker.unpackLong())
            );

            if (world == null)
                return Result.err(
                        DecodingException.from("World is null")
                );

            return Result.ok(
                    new Location(
                            world,
                            unpacker.unpackDouble(),
                            unpacker.unpackDouble(),
                            unpacker.unpackDouble(),
                            unpacker.unpackFloat(),
                            unpacker.unpackFloat()
                    )
            );
        } catch (final IOException e) {
            return Result.err(
                    DecodingException.from(e)
            );
        }
    }
}
