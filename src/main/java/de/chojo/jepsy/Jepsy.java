package de.chojo.jepsy;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.jdautil.command.dispatching.CommandHub;
import de.chojo.jepsy.commands.Jep;
import de.chojo.jepsy.configuration.Configuration;
import de.chojo.jepsy.crawler.JepCrawler;
import de.chojo.jepsy.data.JepData;
import de.chojo.jepsy.listener.ButtonListener;
import de.chojo.sqlutil.datasource.DataSourceCreator;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.slf4j.LoggerFactory.getLogger;

public class Jepsy {

    private static final Logger log = getLogger(Jepsy.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(5, createThreadFactory(new ThreadGroup("Jepsy")));
    private static Jepsy jepsy;
    private HikariDataSource dataSource;
    private Configuration configuration;
    private ShardManager shardManager;
    private JepData jepData;

    private static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER =
            (t, e) -> log.error("An uncaught exception occured in " + t.getName() + "-" + t.getId() + ".", e);

    private static ThreadFactory createThreadFactory(ThreadGroup group) {
        return r -> {
            var thread = new Thread(group, r, group.getName());
            thread.setUncaughtExceptionHandler(EXCEPTION_HANDLER);
            return thread;
        };
    }

    public static void main(String[] args) throws IOException, LoginException {
        Jepsy.jepsy = new Jepsy();
        jepsy.init();
    }

    private void init() throws IOException, LoginException {
        configuration = Configuration.load();

        initDb();

        initBot();

        initCommands();

        var jepCrawler = new JepCrawler(jepData);

        jepCrawler.run();
    }

    private void initBot() throws LoginException {
        shardManager = DefaultShardManagerBuilder.createDefault(configuration.general().token())
                .setThreadFactory(createThreadFactory(new ThreadGroup("JDA")))
                .build();
    }

    private void initCommands() {
        var renderService = new DocumentRenderService(jepData);
        var builder = CommandHub.builder(shardManager, "")
                .withCommands(new Jep(jepData, renderService))
                .withSlashCommands()
                .receiveGuildCommands();

        shardManager.addEventListener(new ButtonListener(jepData, renderService));

        if (configuration.debug().isDebug()) {
            builder.onlyGuildCommands(configuration.debug().guildIds());
        }
        builder.build();
    }

    private void initDb() {
        var database = configuration.database();
        dataSource = DataSourceCreator.create(PGSimpleDataSource.class)
                .withUser(database.user())
                .withPassword(database.password())
                .withAddress(database.host())
                .withPort(database.port())
                .forDatabase(database.database())
                .create()
                .forSchema(database.schema())
                .build();

        jepData = new JepData(dataSource, executorService);
    }
}
