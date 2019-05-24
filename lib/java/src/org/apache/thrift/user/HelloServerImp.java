package org.apache.thrift.user;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

public class HelloServerImp implements HelloServer.AsyncIface {
    private Object respose = null;

    public Object getResposen() {
        return this.respose;
    }

    public void sayString(String param, AsyncMethodCallback<String> resultHandler) throws TException {
        this.respose = resultHandler;
        resultHandler.onComplete(param + "--");

    }
}
