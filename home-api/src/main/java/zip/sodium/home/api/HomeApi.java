package zip.sodium.home.api;

import com.google.common.base.Preconditions;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.Binary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zip.sodium.home.api.data.StoredLocation;
import zip.sodium.home.api.serialization.builtin.LocationCodec;
import zip.sodium.home.api.serialization.data.Result;
import zip.sodium.home.api.serialization.exception.DecodingException;
import zip.sodium.home.api.serialization.exception.EncodingException;
import zip.sodium.home.api.serialization.exception.ParsingException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class HomeApi {
    public static HomeApi create(final String connectionString,
                                 final String databaseName,
                                 final String collectionName) {
        return new HomeApi(
                connectionString,
                databaseName,
                collectionName
        );
    }

    private final MongoClient client;
    private final MongoCollection<Document> homeData;

    private HomeApi(final String connectionString,
                    final String databaseName,
                    final String collectionName) {
        Preconditions.checkArgument(connectionString != null, "Connection string is null");
        Preconditions.checkArgument(databaseName != null, "Connection string is null");
        Preconditions.checkArgument(collectionName != null, "Connection string is null");

        client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionString))
                        .serverApi(
                                ServerApi.builder()
                                        .version(ServerApiVersion.V1)
                                        .build()
                        )
                        .build()
        );

        final var homeDataDb = client.getDatabase(databaseName);
        homeData = homeDataDb.getCollection(collectionName);
    }

    public @NotNull CompletableFuture<Result<Void, EncodingException>> setHome(final @NotNull UUID player, final @Nullable StoredLocation storedLocation) {
        Preconditions.checkArgument(player != null, "Player is null");

        return CompletableFuture.supplyAsync(() -> {
            final var filter = new Document("uuid", player.hashCode());

            if (storedLocation == null) {
                homeData.deleteOne(
                        filter
                );

                return Result.ok();
            }

            final var encoded = LocationCodec.INSTANCE.encode(storedLocation);
            if (encoded.isErr()) {
                return encoded.map(x -> null);
            }

            homeData.updateOne(
                    filter,
                    new Document(
                            "$set",
                            new Document("home", encoded.unwrap())
                    ),
                    new UpdateOptions().upsert(true)
            );

            return Result.ok();
        });
    }

    public @NotNull CompletableFuture<Result<@Nullable StoredLocation, ParsingException>> getHome(final @NotNull UUID player) {
        Preconditions.checkArgument(player != null, "Player is null");

        return CompletableFuture.supplyAsync(() -> {
            final var filter = new Document("uuid", player.hashCode());

            final var doc = homeData.find(filter).limit(1).first();
            if (doc == null) {
                return Result.ok(null);
            }

            final var objectHomeData = doc.get("home");
            if (!(objectHomeData instanceof Binary binaryHomeData))
                return Result.err(
                        ParsingException.from("Home binary data is invalid!")
                );;

            final byte[] homeData = binaryHomeData.getData();

            return LocationCodec.INSTANCE.decode(homeData)
                    .mapErr(ParsingException::from);
        });
    }

    public void cleanup() {
        if (client != null) {
            client.close();
        }
    }
}
