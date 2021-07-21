package de.chojo.jepsy;

import de.chojo.jepsy.crawler.DocumentCreator;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

public class Jepsy {

    private static final Logger log = getLogger(Jepsy.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var documentCreator = new DocumentCreator();
        var jepDocument = documentCreator.retrieveDocument(395).get();
        log.info("Done.");
    }
}
