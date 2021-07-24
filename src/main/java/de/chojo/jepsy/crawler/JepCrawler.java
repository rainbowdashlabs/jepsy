package de.chojo.jepsy.crawler;

import de.chojo.jepsy.data.JepData;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

public class JepCrawler implements Runnable {
    private static final Logger log = getLogger(JepCrawler.class);
    private final DocumentCreator creator = new DocumentCreator();
    private final JepData data;

    public JepCrawler(JepData jepData) {
        data = jepData;
    }

    @Override
    public void run() {
        try {
            crawlAll();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Crawler interrupted", e);
        }
    }

    public void crawlAll() throws InterruptedException, ExecutionException {
        var jep = 101; // First JEP is 101
        var fails = 0;
        while (true) {
            var jepDocument = creator.retrieveDocument(jep).get();
            if (jepDocument.isEmpty()) {
                fails++;
                if (fails > 10) break;
                Thread.sleep(500);
                jep++;
                continue;
            }
            fails = 0;
            data.addDocument(jepDocument.get());
            log.info("Added document {}", jepDocument.get().name());
            Thread.sleep(1500);
            jep++;
        }
    }
}
