package com.search.manager.response;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class ServiceResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 0;

    public static final int ERROR = -1;

    public static final int TX_SUCCESS = 1;

    private T data;

    private int status;

    private ErrorMessage<?> errorMessage;

    public T getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public ErrorMessage<?> getErrorMessage() {
        return errorMessage;
    }

    public void error(String message) {
        status = ERROR;
        errorMessage = new ErrorMessage<Void>(message);
    }

    public <S> void error(String message, S data) {
        status = ERROR;
        errorMessage = new ErrorMessage<S>(message, data);
    }

    public void success(T data) {
        status = SUCCESS;
        this.data = data;
    }
}
