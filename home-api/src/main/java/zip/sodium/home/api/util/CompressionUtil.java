package zip.sodium.home.api.util;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdCompressCtx;
import org.jetbrains.annotations.NotNull;
import org.msgpack.core.MessagePack;

import java.io.IOException;
import java.util.Arrays;

public final class CompressionUtil {
    private CompressionUtil() {}

    public static byte @NotNull [] compress(final byte @NotNull [] bytes) throws IOException {
        if (bytes == null)
            throw new IOException("Bytes is null");

        try (final var packer = MessagePack.newDefaultBufferPacker()) {
            packer.packBinaryHeader(bytes.length);

            final byte[] compressed;
            try (final var ctx = new ZstdCompressCtx()) {
                final byte[] dst = new byte[(int) Zstd.compressBound(bytes.length)];
                final int size = ctx.compressByteArray(
                        dst,
                        0,
                        dst.length,
                        bytes,
                        0,
                        bytes.length
                );

                compressed = Arrays.copyOfRange(
                        dst,
                        0,
                        size
                );
            }

            packer.writePayload(compressed);

            return packer.toByteArray();
        }
    }

    public static byte @NotNull [] decompress(final byte @NotNull [] bytes) throws IOException {
        if (bytes == null)
            throw new IOException("Bytes is null");

        try (final var unpacker = MessagePack.newDefaultUnpacker(bytes)) {
            final var decompressedLength = unpacker.unpackBinaryHeader();
            return Zstd.decompress(
                    unpacker.readPayload((int) (bytes.length - unpacker.getTotalReadBytes())),
                    decompressedLength
            );
        }
    }
}
