package com.sky.medialib.ui.kit.common.network;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {

    public static <T> FlowableTransformer<T, T> rxSchedulers() {
        return new FlowableTransformer<T,T>(){
            @Override
            public Publisher apply(Flowable upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        } ;
    }

    public static <T> FlowableTransformer<T, T> rxNewTheadShedulers() {
        return  new FlowableTransformer<T,T>(){
            @Override
            public Publisher apply(Flowable upstream) {
                return upstream.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
            }
        } ;
    }
}
