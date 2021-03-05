package xyz.nucleoid.plasmid.map;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.util.stream.Stream;

public final class MapTickets implements AutoCloseable {
    private final ServerWorld world;
    private final ChunkPos minChunk;
    private final ChunkPos maxChunk;

    private MapTickets(ServerWorld world, ChunkPos minChunk, ChunkPos maxChunk) {
        this.world = world;
        this.minChunk = minChunk;
        this.maxChunk = maxChunk;
    }

    public static MapTickets acquire(ServerWorld world, BlockBounds bounds) {
        ChunkPos minChunk = new ChunkPos(bounds.getMin());
        ChunkPos maxChunk = new ChunkPos(bounds.getMax());

        MapTickets tickets = new MapTickets(world, minChunk, maxChunk);
        tickets.acquire();

        return tickets;
    }

    private void acquire() {
        ServerChunkManager chunkManager = this.world.getChunkManager();
        this.stream().forEach(ticketPos -> {
            chunkManager.setChunkForced(ticketPos, true);
        });
    }

    @Override
    public void close() {
        ServerChunkManager chunkManager = this.world.getChunkManager();
        this.stream().forEach(ticketPos -> {
            chunkManager.setChunkForced(ticketPos, false);
        });
    }

    private Stream<ChunkPos> stream() {
        return ChunkPos.stream(this.minChunk, this.maxChunk);
    }
}
