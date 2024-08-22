package zip.sodium.home.database;

import com.google.common.base.Preconditions;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.Binary;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zip.sodium.home.Entrypoint;
import zip.sodium.home.config.builtin.DatabaseConfig;
import zip.sodium.home.serialization.builtin.LocationCodec;

import java.util.concurrent.CompletableFuture;

public final class DatabaseHolder {
    private DatabaseHolder() {}

    private static MongoClient client = null;
    private static MongoCollection<Document> homeData = null;

    private static void tryConnect() {
        final var serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        final var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(Preconditions.checkNotNull(
                        DatabaseConfig.CONNECTION_STRING.get(FileConfiguration::getString),
                        "Connection string is null!"
                )))
                .serverApi(serverApi)
                .build();

        client = MongoClients.create(settings);

        final var homeDataDb = client.getDatabase(DatabaseConfig.DATABASE_NAME.get());

        homeData = homeDataDb.getCollection(DatabaseConfig.DATA_COLLECTION_NAME.get());
    }

    public static void acknowledge() {
        try {
            tryConnect();
        } catch (final MongoException | NullPointerException e) {
            Entrypoint.disable(
                    "Couldn't connect to database! Disabling.",
                    e
            );
        }
    }

    public static @NotNull CompletableFuture<Boolean> setHome(final @NotNull OfflinePlayer player, final @Nullable Location location) {
        Preconditions.checkArgument(player != null, "Player is null");

        return CompletableFuture.supplyAsync(() -> {
            final var filter = new Document("uuid", player.getUniqueId().hashCode());

            if (location == null) {
                homeData.deleteOne(
                        filter
                );

                return true;
            }

            final var encoded = LocationCodec.INSTANCE.encode(location);
            if (encoded.isErr()) {
                return false;
            }

            System.out.println("hi,");

            homeData.updateOne(
                    filter,
                    new Document(
                            "$set",
                            new Document("home", encoded.unwrap())
                    ),
                    new UpdateOptions().upsert(true)
            );

            System.out.println("h34342");

            return true;
        });
    }

    public static @NotNull CompletableFuture<@Nullable Location> getHome(@NotNull OfflinePlayer player) {
        Preconditions.checkArgument(player != null, "Player is null");

        return CompletableFuture.supplyAsync(() -> {
            final var filter = new Document("uuid", player.getUniqueId().hashCode());

            final var doc = homeData.find(filter).limit(1).first();
            if (doc == null) {
                return null;
            }

            final var objectHomeData = doc.get("home");
            if (!(objectHomeData instanceof Binary binaryHomeData))
                return null;

            final byte[] homeData = binaryHomeData.getData();

            return LocationCodec.INSTANCE.decode(homeData).unwrap();
        });
    }

    public static void cleanup() {
        if (client != null) {
            client.close();
            client = null;
            homeData = null;
        }
    }
}
