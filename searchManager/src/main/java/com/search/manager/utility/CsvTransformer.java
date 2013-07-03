package com.search.manager.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVWriter;

public abstract class CsvTransformer<T> {

    protected abstract String[] toStringArray(T t);

    public InputStream getCsvStream(List<T> data) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(os));

        for (T item : data) {
            writer.writeNext(this.toStringArray(item));
        }

        IOUtils.closeQuietly(writer);

        return new ByteArrayInputStream(os.toByteArray());
    }
}
