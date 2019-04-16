package user;

import async.AsyncMethodCallback;
import transport.TException;

public class HelloServerImp implements HelloServer.AsyncIface {


//    public String sayString(String param) throws TException {
//        return "hihi+"+param;
//    }
    Object respose=null;
    public Object getResposen(){
        return this.respose;
    }
    public void sayString(String param, AsyncMethodCallback<String> resultHandler) throws TException {
        this.respose=resultHandler;
        System.out.println("ss");
        resultHandler.onComplete(param+"--");

    }
}
