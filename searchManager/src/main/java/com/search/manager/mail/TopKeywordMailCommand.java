package com.search.manager.mail;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.manager.model.TopKeyword;
import com.search.manager.utility.*;

public class TopKeywordMailCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(TopKeywordMailCommand.class);
    private static final CsvTransformer<TopKeyword> transformer = new CsvTransformer<TopKeyword>() {
        @Override
        public String[] toStringArray(TopKeyword t) {
            return new String[] { String.valueOf(t.getCount()), t.getKeyword(), String.valueOf(t.getResultCount()),
                    t.getSku() };
        }
    };

    private ReportNotificationMailService mailService;

    private static final int MAX_CONSUMER_THREADS = 100;
    private AtomicInteger threadCount = new AtomicInteger(0);

    private String store;
    private Date from;
    private Date to;
    private String[] recipients;
    private String filename;
    private ByteArrayInputStream bias;
    private String contentType;

    // Index
    private int index = 0;
    private BlockingQueue<TopKeyword> work = new ArrayBlockingQueue<TopKeyword>(1000);
    private List<TopKeyword> keywords = null;

    public TopKeywordMailCommand(ReportNotificationMailService mailService, String store, Date from, Date to,
            String[] recipients, String filename, ByteArrayInputStream bias, String contentType) {
        super();
        this.mailService = mailService;
        this.store = store;
        this.from = from;
        this.to = to;
        this.recipients = recipients;
        this.filename = filename;
        this.bias = bias;
        this.contentType = contentType;
    }

    @Override
    public void execute() {
        keywords = StatisticsUtil.getTopKeywordsInRange(from, to, store);
        startProducer();
        startConsumers(MAX_CONSUMER_THREADS);
        waitForThreads();

        if (!work.isEmpty()) {
            startConsumers(Math.min(MAX_CONSUMER_THREADS, work.size()));
            waitForThreads();
        }

        sendMail();
    }

    private void startProducer() {
        threadCount.incrementAndGet();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (index < keywords.size()) {
                    if (work.offer(keywords.get(index))) {
                        index++;
                    }
                }

                logger.trace("Stopping producer thread.");
                threadCount.decrementAndGet();
            }

        }).start();
    }

    private void startConsumers(int numThread) {
        for (int i = 0; i < numThread; i++) {
            new Thread(new SolrSearchRequest(work, getSolrUrl(), threadCount)).start();
        }
    }

    private void waitForThreads() {
        int tcount = 0;
        while ((tcount = threadCount.get()) > 0) {
            try {
                logger.trace("Remaining threads: {}, produced keywords: {}", tcount, index);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    private void sendMail() {
        mailService.sendTopKeyword(transformer.getCsvStream(keywords), filename, recipients, bias, contentType);
    }

    private String getSolrUrl() {
        return PropsUtils.getValue(store.toLowerCase() + "_solrUrl");
    }
}
