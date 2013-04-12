package com.search.manager.utility;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.io.FileTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransferUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(FileTransferUtils.class);

    public static FileTransfer downloadCsv(InputStream content, String fileHeader, String filename, String customFilename) {
        BufferedInputStream bis = null;

        try {
            bis = new BufferedInputStream(content);
            CombinedInputStream cis = new CombinedInputStream(new InputStream[] {
                    new ByteArrayInputStream(fileHeader.getBytes()), bis });
            // FileTransfer auto-closes the stream
            return new FileTransfer(StringUtils.isBlank(customFilename) ? filename : customFilename + ".csv",
                    "application/csv", cis);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }
}
