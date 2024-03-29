package com.jyt.baseapp.api;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jyt.baseapp.App;
import com.jyt.baseapp.bean.BaseJson;
import com.jyt.baseapp.helper.IntentHelper;
import com.jyt.baseapp.util.FinishActivityManager;
import com.jyt.baseapp.util.L;
import com.jyt.baseapp.util.T;
import com.jyt.baseapp.util.UserInfo;
import com.jyt.baseapp.view.dialog.LoadingDialog;
import com.zhy.http.okhttp.callback.Callback;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;


/**
 * OkHttpUtil Callback
 * Created by chenweiqi on 2017/1/18.
 */
public abstract class BeanCallback<T> extends Callback<T> {

    LoadingDialog dialog ;

    Context context;

    public BeanCallback() {
        this(null,false,null);
    }
    public BeanCallback(Context context){
        this(context,true,null);
    }
    public BeanCallback(Context context, boolean cancelable) {
        this(context,cancelable,null);
    }
    public BeanCallback(Context context, String  message) {
        this(context,true,message);
    }
    public BeanCallback(Context context, boolean cancelable, String message) {
        if (context!=null) {
            this.context = context;
            dialog = new LoadingDialog(context);
            dialog.setCancelable(cancelable);
            if (!TextUtils.isEmpty(message)) {
                dialog.setText(message);
            }
        }
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if (dialog!=null&&!dialog.isShowing())
            dialog.show();

    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
        if (dialog!=null&&dialog.isShowing())
            dialog.dismiss();
    }
    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        Type type = this.getClass().getGenericSuperclass();
//        if (true) {
//            throw new RuntimeException("人工异常");
//        }
        String bodyString = response.body().string() ;
            if (type instanceof ParameterizedType) {
                //如果用户写了泛型，就会进入这里，否者不会执行
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type beanType = parameterizedType.getActualTypeArguments()[0];
                try {
                    if (beanType == String.class) {
                        //如果是String类型，直接返回字符串
                        return (T) bodyString;
                    } else {
                        //如果是 Bean List Map ，则解析完后返回
                        Object object = new Gson().fromJson(bodyString, beanType);
                        if (object==null){
                            object =createReturnResultByTemplate();
                        }
                        return (T) object;
                    }
                }catch (Throwable e){
//                    e.printStackTrace();
                    Object object =createReturnResultByTemplate();
                    if (object instanceof BaseJson){
                        ((BaseJson) object).setForUser(e.getMessage());
                    }
                    return (T) object;
                }
            } else {
                //默认返回字符串
                return (T) bodyString;
            }
    }


    @Override
    public void onError(Call call, Exception e, int id) {
        L.e("error",e.getMessage());
        Object object = createReturnResultByTemplate();
        if (object instanceof BaseJson){
            ((BaseJson) object).setForUser(e.getMessage());
        }
        response(false, (T) object,id);
    }

    /**
     * 根据范型创建一个新对象
     * @return
     */
    private T createReturnResultByTemplate(){
        Type type = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type beanType = parameterizedType.getActualTypeArguments()[0];
        while (!(beanType instanceof Class)){
            beanType = ((ParameterizedType) beanType).getRawType();
        }
        Class c = (Class) beanType;
        try {
            return (T) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResponse(T response, int id) {
        if (response instanceof BaseJson){
            if (((BaseJson) response).getCode()==503){
                FinishActivityManager.getManager().finishAllActivity();
                IntentHelper.openLoginActivity(App.getApplication());
                return;
            }
        }
        response(true,response,id);
    }

    public abstract void response(boolean success,T response,int id);
}