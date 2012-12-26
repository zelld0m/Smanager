package com.search.manager.mail;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.search.manager.model.TopKeyword;
import com.search.manager.utility.*;

public class TopKeywordMailCommand implements Command {

    private static final CsvTransformer<TopKeyword> transformer = new CsvTransformer<TopKeyword>() {
        @Override
        public String[] toStringArray(TopKeyword t) {
            return new String[] { String.valueOf(t.getCount()), t.getKeyword(), String.valueOf(t.getResultCount()),
                    t.getSku() };
        }
    };

    private ReportNotificationMailService mailService;

    private String store;
    private Date from;
    private Date to;
    private String[] recipients;
    private String filename;
    private ByteArrayInputStream bias;
    private String contentType;

    // Index
    private AtomicInteger index = new AtomicInteger(0);
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
        startConsumers();
        waitForConsumers();
        sendMail();
    }

    private void startProducer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (index.get() < keywords.size()) {
                    if (work.offer(keywords.get(index.get()))) {
                        index.incrementAndGet();
                    }
                }
            }

        }).start();
    }

    private void startConsumers() {
        for (int i = 0; i < 100; i++) {
            new Thread(new SolrSearchRequest(work, getSolrUrl())).start();
        }
    }

    private void waitForConsumers() {
        while (!work.isEmpty() || index.get() < keywords.size()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    private void sendMail() {
        mailService.sendTopKeyword(StatisticsUtil.getCustomRangeReportStream(keywords, transformer), filename,
                recipients, bias, contentType);
    }

    private String getSolrUrl() {
        return PropsUtils.getValue(store.toLowerCase() + "_solrUrl");
    }
}
