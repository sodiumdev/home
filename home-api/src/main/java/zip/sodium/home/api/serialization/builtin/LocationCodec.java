package zip.sodium.home.api.serialization.builtin;

import org.jetbrains.annotations.NotNull;
import org.msgpack.core.MessagePack;
import zip.sodium.home.api.data.StoredLocation;
import zip.sodium.home.api.serialization.Codec;
import zip.sodium.home.api.serialization.data.Result;
import zip.sodium.home.api.serialization.exception.DecodingException;
import zip.sodium.home.api.serialization.exception.EncodingException;
import zip.sodium.home.api.util.CompressionUtil;

import java.io.IOException;
import java.util.UUID;

public final class LocationCodec implements Codec<StoredLocation, byte[]> {
    public static final Codec<StoredLocation, byte[]> INSTANCE = new LocationCodec();

    private LocationCodec() {}

    @Override
    public @NotNull Result<byte[], EncodingException> encode(final @NotNull StoredLocation storedLocation) {
        final var worldUuid = storedLocation.worldId();
        if (worldUuid == null)
            return Result.err(
                    EncodingException.from("World is null")
            );

        final byte[] data;
        try (final var packer = MessagePack.newDefaultBufferPacker()) {
            packer.packLong(worldUuid.getMostSignificantBits());
            packer.packLong(worldUuid.getLeastSignificantBits());
            packer.packDouble(storedLocation.x());
            packer.packDouble(storedLocation.y());
            packer.packDouble(storedLocation.z());
            packer.packFloat(storedLocation.yaw());
            packer.packFloat(storedLocation.pitch());

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
    public @NotNull Result<StoredLocation, DecodingException> decode(final byte @NotNull [] data) {
        final byte[] decompressedData;
        try {
            decompressedData = CompressionUtil.decompress(data);
        } catch (final IOException e) {
            return Result.err(
                    DecodingException.from(e)
            );
        }

        try (final var unpacker = MessagePack.newDefaultUnpacker(decompressedData)) {
            return Result.ok(
                    new StoredLocation(
                            new UUID(unpacker.unpackLong(), unpacker.unpackLong()),
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
