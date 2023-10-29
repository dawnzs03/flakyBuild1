package io.quarkus.redis.datasource.graph;

import java.time.Duration;

import io.quarkus.redis.datasource.TransactionalRedisCommands;

/**
 * Allows executing commands from the {@code graph} group.
 * These commands require the <a href="https://redis.io/docs/stack/graph/">Redis Graph</a> module to be installed in the
 * Redis server.
 * <p>
 * See <a href="https://redis.io/commands/?group=graph">the graph command list</a> for further information about
 * these commands.
 * <p>
 * This API is intended to be used in a Redis transaction ({@code MULTI}), thus, all command methods return {@code void}.
 *
 * @param <K> the type of the key
 */
public interface TransactionalGraphCommands<K> extends TransactionalRedisCommands {

    /**
     * Execute the command <a href="https://redis.io/commands/graph.delete">GRAPH.DELETE</a>.
     * Summary: Completely removes the graph and all of its entities.
     * Group: graph
     *
     * @param key the key, must not be {@code null}
     */
    void graphDelete(K key);

    /**
     * Execute the command <a href="https://redis.io/commands/graph.delete">GRAPH.EXPLAIN</a>.
     * Summary: Constructs a query execution plan but does not run it. Inspect this execution plan to better understand
     * how your query will get executed.
     * Group: graph
     * <p>
     *
     * @param key the key, must not be {@code null}
     * @param query the query, must not be {@code null}
     */
    void graphExplain(K key, String query);

    /**
     * Execute the command <a href="https://redis.io/commands/graph.list">GRAPH.LIST</a>.
     * Summary: Lists all graph keys in the keyspace.
     * Group: graph
     * <p>
     */
    void graphList();

    /**
     * Execute the command <a href="https://redis.io/commands/graph.delete">GRAPH.QUERY</a>.
     * Summary: Executes the given query against a specified graph.
     * Group: graph
     * <p>
     *
     * @param key the key, must not be {@code null}
     * @param query the query, must not be {@code null}
     */
    void graphQuery(K key, String query);

    /**
     * Execute the command <a href="https://redis.io/commands/graph.delete">GRAPH.QUERY</a>.
     * Summary: Executes the given query against a specified graph.
     * Group: graph
     * <p>
     *
     * @param key the key, must not be {@code null}
     * @param query the query, must not be {@code null}
     * @param timeout a timeout, must not be {@code null}
     */
    void graphQuery(K key, String query, Duration timeout);
}
