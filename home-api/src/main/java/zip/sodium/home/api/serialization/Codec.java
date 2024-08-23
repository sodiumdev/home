package zip.sodium.home.api.serialization;

import org.jetbrains.annotations.NotNull;
import zip.sodium.home.api.serialization.data.Result;
import zip.sodium.home.api.serialization.exception.DecodingException;
import zip.sodium.home.api.serialization.exception.EncodingException;

public interface Codec<K, V> {
    @NotNull Result<V, EncodingException> encode(final @NotNull K k);
    @NotNull Result<K, DecodingException> decode(final @NotNull V v);
}
