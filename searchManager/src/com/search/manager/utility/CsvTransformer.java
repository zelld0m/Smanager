package com.search.manager.utility;

public interface CsvTransformer<T> {
    String[] toStringArray(T t);
}
