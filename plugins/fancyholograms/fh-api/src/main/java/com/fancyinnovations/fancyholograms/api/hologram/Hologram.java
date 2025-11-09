package com.fancyinnovations.fancyholograms.api.hologram;

import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.google.common.collect.Sets;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


/**
 * This class provides core functionalities for managing viewers, spawning, despawning, and updating holograms.
 */
public abstract class Hologram {

    public static final int LINE_WIDTH = 1000;
    public static final Color TRANSPARENT = Color.fromARGB(0);
    protected static final int MINIMUM_PROTOCOL_VERSION = 762;

    protected final @NotNull HologramData data;
    protected final @NotNull Set<UUID> viewers;

    private static final UUID NULL_PLAYER_KEY = new UUID(0L, 0L);
    private static final long TEXT_CACHE_TTL_MS = 1000L;
    private static final long TEXT_CACHE_MAX_AGE_MS = 5 * 60 * 1000L;
    private static final int TEXT_CACHE_MAX_SIZE = 512;

    private final Map<UUID, CacheEntry> cachedTextPerPlayer = new ConcurrentHashMap<>();
    private String lastRawText = "";

    private static final class CacheEntry {
        private final Component text;
        private final long lastUpdated;

        private CacheEntry(Component text, long lastUpdated) {
            this.text = text;
            this.lastUpdated = lastUpdated;
        }

        private Component getText() {
            return text;
        }

        private long getLastUpdated() {
            return lastUpdated;
        }
    }

    protected Hologram(@NotNull final HologramData data) {
        this.data = data;
        this.viewers = new HashSet<>();

        this.data.getTraitTrait().attachHologram(this);

        final Runnable traitOnModify = this.data.getTraitTrait()::onModify;
        final Runnable clearCache = this::clearTextCache;

        final Runnable composedOnModify = () -> {
            clearCache.run();
            traitOnModify.run();
        };

        this.data.setOnModify(composedOnModify);
    }

    /**
     * Forcefully spawns the hologram and makes it visible to the specified player.
     *
     * @param player the player to whom the hologram should be shown; must not be null
     */
    @ApiStatus.Internal
    public abstract void spawnTo(@NotNull final Player player);

    /**
     * Forcefully despawns the hologram and makes it invisible to the specified player.
     *
     * @param player the player from whom the hologram should be hidden; must not be null
     */
    @ApiStatus.Internal
    public abstract void despawnFrom(@NotNull final Player player);

    /**
     * Updates the hologram for the specified player.
     *
     * @param player the player for whom the hologram should be updated; must not be null
     */
    @ApiStatus.Internal
    public abstract void updateFor(@NotNull final Player player);


    /**
     * @return a copy of the set of UUIDs of players currently viewing the hologram
     */
    public final @NotNull Set<UUID> getViewers() {
        return Sets.newHashSet(this.viewers);
    }

    @ApiStatus.Internal
    public void setViewers(@NotNull final Set<UUID> viewers) {
        this.viewers.clear();
        this.viewers.addAll(viewers);
    }

    @ApiStatus.Internal
    public void removeViewer(@NotNull final UUID viewer) {
        this.viewers.remove(viewer);
    }

    /**
     * @param player the player to check for
     * @return whether the player is currently viewing the hologram
     */
    public final boolean isViewer(@NotNull final Player player) {
        return isViewer(player.getUniqueId());
    }

    /**
     * @param player the uuid of the player to check for
     * @return whether the player is currently viewing the hologram
     */
    public final boolean isViewer(@NotNull final UUID player) {
        return this.viewers.contains(player);
    }

    public final @NotNull HologramData getData() {
        return this.data;
    }

    /**
     * Retrieves the data associated with the hologram and casts it to the specified type.
     *
     * @param <T>   the type of {@code HologramData} to retrieve
     * @param clazz the class of the data type to retrieve; must not be null
     * @return the hologram data cast to the specified type
     */
    @ApiStatus.Experimental
    public final <T extends HologramData> @NotNull T getData(@NotNull Class<T> clazz) {
        return clazz.cast(this.data);
    }

    /**
     * Retrieves the data associated with the hologram, if it can be cast to the specified type.
     *
     * @param <T>   the type of {@code HologramData}
     * @param clazz the class of the data type to retrieve; must not be null
     * @return the hologram data cast to the specified type, or null if the cast fails
     */
    @ApiStatus.Experimental
    public final <T extends HologramData> @Nullable T getDataNullable(@NotNull Class<T> clazz) {
        try {
            return clazz.cast(this.data);
        } catch (ClassCastException ignored) {
            return null;
        }
    }

    /**
     * Consumes the data associated with the hologram if it can be cast to the specified type.
     *
     * @param <T>      the type of {@link HologramData} to consume
     * @param clazz    the class of the data type to consume; must not be null
     * @param consumer the action to perform with the consumed data; must not be null
     */
    @ApiStatus.Experimental
    public final <T extends HologramData> void consumeData(@NotNull Class<T> clazz, @NotNull Consumer<T> consumer) {
        final T data = getDataNullable(clazz);

        if (data != null) {
            consumer.accept(data);
        }
    }

    /**
     * Gets the text shown in the hologram, with lightweight caching to reduce repeated placeholder and color processing.
     *
     * Caching rules:
     * - Cache key: player UUID (or NULL_PLAYER_KEY for non-player specific text)
     * - Cache is invalidated whenever the underlying text changes via the onModify callback.
     * - Per-entry timestamps are used to validate freshness against TEXT_CACHE_TTL_MS.
     *
     * @param player the player to get the placeholders for, or null if no placeholders should be replaced
     * @return the text shown in the hologram
     */
    public final Component getShownText(@Nullable final Player player) {
        if (!(getData() instanceof TextHologramData textData)) {
            return null;
        }

        final String rawText = String.join("\n", textData.getText());
        final long now = System.currentTimeMillis();

        if (player != null) {
            final UUID uuid = player.getUniqueId();

            if (rawText.equals(lastRawText)) {
                final CacheEntry entry = cachedTextPerPlayer.get(uuid);
                if (entry != null && now - entry.getLastUpdated() <= TEXT_CACHE_TTL_MS) {
                    return entry.getText();
                }
            } else {
                clearTextCache();
            }

            final Component translated = ModernChatColorHandler.translate(rawText, player);
            cachedTextPerPlayer.put(uuid, new CacheEntry(translated, now));
            lastRawText = rawText;
            evictStaleCacheEntries(now);
            return translated;
        }

        if (rawText.equals(lastRawText)) {
            final CacheEntry entry = cachedTextPerPlayer.get(NULL_PLAYER_KEY);
            if (entry != null && now - entry.getLastUpdated() <= TEXT_CACHE_TTL_MS) {
                return entry.getText();
            }
        } else {
            clearTextCache();
        }

        final Component translated = ModernChatColorHandler.translate(rawText, (Player) null);
        cachedTextPerPlayer.put(NULL_PLAYER_KEY, new CacheEntry(translated, now));
        lastRawText = rawText;
        evictStaleCacheEntries(now);
        return translated;
    }

    /**
     * Clears the cached text for all players. Intended to be invoked via the onModify hook
     * when text-related data changes to prevent stale content from being displayed.
     */
    @ApiStatus.Internal
    public void clearTextCache() {
        cachedTextPerPlayer.clear();
        lastRawText = "";
    }

    /**
     * Evicts cache entries that are either:
     * - No longer associated with an active viewer (except the null-player key), or
     * - Older than TEXT_CACHE_MAX_AGE_MS, or
     * - Beyond TEXT_CACHE_MAX_SIZE entries (dropping the oldest first).
     *
     * This prevents unbounded growth and stale data for players who are no longer viewing.
     */
    private void evictStaleCacheEntries(long now) {
        cachedTextPerPlayer.entrySet().removeIf(entry -> {
            final UUID uuid = entry.getKey();
            final CacheEntry cacheEntry = entry.getValue();

            if (uuid.equals(NULL_PLAYER_KEY)) {
                return now - cacheEntry.getLastUpdated() > TEXT_CACHE_MAX_AGE_MS;
            }

            if (!viewers.contains(uuid)) {
                return true;
            }

            return now - cacheEntry.getLastUpdated() > TEXT_CACHE_MAX_AGE_MS;
        });

        if (cachedTextPerPlayer.size() > TEXT_CACHE_MAX_SIZE) {
            cachedTextPerPlayer.entrySet().stream()
                    .sorted((a, b) -> Long.compare(a.getValue().getLastUpdated(), b.getValue().getLastUpdated()))
                    .limit(cachedTextPerPlayer.size() - TEXT_CACHE_MAX_SIZE)
                    .map(Map.Entry::getKey)
                    .forEach(cachedTextPerPlayer::remove);
        }
    }
}
