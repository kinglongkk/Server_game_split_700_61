package com.ddm.server.http.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import com.ddm.server.common.CommLogD;

import BaseTask.SyncTask.SyncTaskManager;

/**
 * 
 * @author Abe
 *
 */
public abstract class IResponseHandler implements FutureCallback<HttpResponse> {

    public class CancelledException extends Exception {
        private static final long serialVersionUID = -421378063733917547L;

    }

    public abstract void compeleted(String response);

    @Override
    public void completed(HttpResponse httpResponse) {
        try {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                final StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                SyncTaskManager.task(() -> compeleted(sb.toString()));
            } catch (Exception e) {
                CommLogD.error("read httpresponse EntityString error : ", e.getMessage(), e);
            }finally {
                if(reader!=null){
                    reader.close();
                }
            }
        }catch (Exception e){
            CommLogD.error("completed："+e.getMessage());
        }
    }

    @Override
    public abstract void failed(Exception exception);

    @Override
    public void cancelled() {
        SyncTaskManager.task(() -> failed(new CancelledException()));
    }
}