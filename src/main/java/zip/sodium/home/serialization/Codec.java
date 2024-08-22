package zip.sodium.home.serialization;

import org.jetbrains.annotations.NotNull;
import zip.sodium.home.serialization.data.Result;
import zip.sodium.home.serialization.exception.DecodingException;
import zip.sodium.home.serialization.exception.EncodingException;

public interface Codec<K, V> {
    @NotNull Result<V, EncodingException> encode(final @NotNull K k);
    @NotNull Result<K, DecodingException> decode(final @NotNull V v);
}
