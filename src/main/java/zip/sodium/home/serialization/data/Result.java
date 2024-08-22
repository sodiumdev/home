package zip.sodium.home.serialization.data;

import org.jetbrains.annotations.Nullable;

public sealed interface Result<T, E> permits Result.Ok, Result.Err {
    record Ok<T, E>(T result) implements Result<T, E> {
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

    static <T, E> Result<T, E> ok(final T result) {
        return new Ok<>(result);
    }

    static <T, E> Result<T, E> err(final E err) {
        return new Err<>(err);
    }

    boolean isOk();
    boolean isErr();

    @Nullable T unwrap();
    @Nullable E unwrapErr();
}
