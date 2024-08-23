package zip.sodium.home.api.serialization.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public sealed interface Result<T, E> permits Result.Ok, Result.Err {
    record Ok<T, E>(T result) implements Result<T, E> {
        @Override
        public <K> Result<K, E> map(final @NotNull Function<T, K> mapper) {
            return Result.ok(mapper.apply(result));
        }

        @Override
        public <K> Result<T, K> mapErr(final @NotNull Function<E, K> mapper) {
            return Result.ok(result);
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public boolean isErr() {
            return false;
        }

        @Override
        public T unwrap() {
            return result;
        }

        @Override
        public E unwrapErr() {
            return null;
        }
    }

    record Err<T, E>(E err) implements Result<T, E> {
        @Override
        public <K> Result<K, E> map(final @NotNull Function<T, K> mapper) {
            return Result.err(err);
        }

        @Override
        public <K> Result<T, K> mapErr(final @NotNull Function<E, K> mapper) {
            return Result.err(mapper.apply(err));
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public boolean isErr() {
            return true;
        }

        @Override
        public T unwrap() {
            return null;
        }

        @Override
        public E unwrapErr() {
            return err;
        }
    }

    static <E> Result<Void, E> ok() {
        return new Ok<>(null);
    }

    static <T, E> Result<T, E> ok(final T result) {
        return new Ok<>(result);
    }

    static <T, E> Result<T, E> err(final E err) {
        return new Err<>(err);
    }

    <K> Result<K, E> map(final @NotNull Function<T, K> mapper);
    <K> Result<T,  K> mapErr(final @NotNull Function<E, K> mapper);

    boolean isOk();
    boolean isErr();

    @Nullable T unwrap();
    @Nullable E unwrapErr();
}
