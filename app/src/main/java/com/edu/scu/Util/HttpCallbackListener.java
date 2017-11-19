package com.edu.scu.Util;
//用于某些请求操作完成后回调函数

public interface HttpCallbackListener {
    //成功时回调
    void onSuccess(String response);
    //出错时回调
    void onError(Exception e);
}
