package com.search.manager.mail;

import java.io.*;

import org.springframework.core.io.InputStreamSource;

/**
 * InputStreamSource implementation for file input streams.
 */
public class AttachmentSource implements InputStreamSource {

    private File file;

    private InputStream inputStream;

    public AttachmentSource(File file) {
        this.file = file;
    }

    public AttachmentSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (inputStream == null && file != null) {
            inputStream = new FileInputStream(file);
        }

        return inputStream;
    }

}
