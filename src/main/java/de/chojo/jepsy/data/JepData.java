package de.chojo.jepsy.data;

import de.chojo.jdautil.container.Pair;
import de.chojo.jepsy.document.JepDocument;
import de.chojo.jepsy.document.JepDocumentBuilder;
import de.chojo.jepsy.document.JepDocumentMetaBuilder;
import de.chojo.jepsy.document.JepDocumentRef;
import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.chojo.sqlutil.wrapper.QueryBuilderConfig;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class JepData extends QueryFactoryHolder {
    private final ExecutorService executorService;
    public JepData(DataSource dataSource, ExecutorService executorService) {
        super(dataSource, QueryBuilderConfig.builder().build());
        this.executorService = executorService;
    }

    public void addDocument(JepDocument doc) {
        var query = builder()
                .query("INSERT INTO jeps(jep, name) VALUES(?,?) ON CONFLICT DO NOTHING")
                .paramsBuilder(stmt -> stmt.setInt(doc.jep()).setString(doc.name()));
        var metaKey = new AtomicInteger();
        for (var entry : doc.meta().values().entrySet()) {
            for (var value : entry.getValue()) {
                query.append().query("""
                        INSERT INTO jep_meta(jep, field, value, key)
                            VALUES (?,?,?,?)
                                ON CONFLICT(jep, field, value)
                                    DO UPDATE SET key = excluded.key;
                        """)
                        .paramsBuilder(stmt -> stmt.setInt(doc.jep()).setString(entry.getKey()).setString(value).setInt(metaKey.getAndIncrement()));
            }
        }

        for (var entry : doc.chapters().entrySet()) {
            query.append().query("""
                    INSERT INTO jep_chapter(jep, chapter, content) VALUES(?,?,?)
                        ON CONFLICT
                            DO NOTHING;
                    """)
                    .paramsBuilder(stmt -> stmt.setInt(doc.jep()).setString(entry.getKey()).setString(entry.getValue()));
        }

        query.update().executeSync();
    }

    public CompletableFuture<List<JepDocumentRef>> byId(int id) {
        return builder(JepDocumentRef.class).query("SELECT jep, name FROM jeps WHERE jep = ?;")
                .paramsBuilder(stmt -> stmt.setInt(id))
                .readRow(rs -> new JepDocumentRef(rs.getInt("jep"), rs.getString("name")))
                .all(executorService);
    }

    public CompletableFuture<List<JepDocumentRef>> byName(String name) {
        return builder(JepDocumentRef.class).query("SELECT jep, name FROM jeps WHERE name ILIKE ?;")
                .paramsBuilder(stmt -> stmt.setString("%" + name + "%"))
                .readRow(rs -> new JepDocumentRef(rs.getInt("jep"), rs.getString("name")))
                .all(executorService);
    }

    public CompletableFuture<List<JepDocumentRef>> byMeta(String key, String value) {
        return builder(JepDocumentRef.class).query("SELECT m.jep, j.name FROM jep_meta m left join jeps j on m.jep = j.jep WHERE key ILIKE ? AND value ILIKE ?;")
                .paramsBuilder(stmt -> stmt.setString("%" + key + "%").setString("%" + value + "%"))
                .readRow(rs -> new JepDocumentRef(rs.getInt("jep"), rs.getString("name")))
                .all(executorService);
    }

    public CompletableFuture<JepDocument> getDocument(JepDocumentRef documentRef) {
        return CompletableFuture.supplyAsync(() ->{
            var builder = new JepDocumentBuilder(documentRef.jep(), documentRef.name());

            var chapters = builder(StringPair.class)
                    .query("SELECT chapter, content from jep_chapter WHERE jep = ?")
                    .paramsBuilder(stmt -> stmt.setInt(documentRef.jep()))
                    .readRow(rs -> new StringPair(rs.getString("chapter"), rs.getString("content")))
                    .allSync();
            for (var chapter : chapters) {
                builder.addChapter(chapter.first, chapter.second);
            }
            var metas = builder(StringPair.class)
                    .query("SELECT field, value, key from jep_meta WHERE jep = ? ORDER BY key")
                    .paramsBuilder(stmt -> stmt.setInt(documentRef.jep()))
                    .readRow(rs -> new StringPair(rs.getString("field"), rs.getString("value")))
                    .allSync();

            var metaBuilder = new JepDocumentMetaBuilder();

            for (var meta : metas) {
                metaBuilder.addMeta(meta.first, meta.second);
            }

            builder.setMeta(metaBuilder.build());
            return builder.build();
        }, executorService);
    }

    private static class StringPair extends Pair<String, String> {

        /**
         * Create a new pair.
         *
         * @param first  first value
         * @param second second value
         */
        public StringPair(String first, String second) {
            super(first, second);
        }
    }
}
