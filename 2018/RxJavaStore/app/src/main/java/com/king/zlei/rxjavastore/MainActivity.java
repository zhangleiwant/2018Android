package com.king.zlei.rxjavastore;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";

    /**
     * 操作符
     * create
     * from
     * just
     * map
     * flatmap
     * zip
     * zipwith
     * retry
     * retrywhen
     * filter
     * timer
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_create).setOnClickListener(this);
        findViewById(R.id.btn_from).setOnClickListener(this);
        findViewById(R.id.btn_just).setOnClickListener(this);
        findViewById(R.id.btn_map).setOnClickListener(this);
        findViewById(R.id.btn_flatmap).setOnClickListener(this);
        findViewById(R.id.btn_zip).setOnClickListener(this);
        findViewById(R.id.btn_zipwith).setOnClickListener(this);
        findViewById(R.id.btn_retry).setOnClickListener(this);
        findViewById(R.id.btn_retrywhen).setOnClickListener(this);
        findViewById(R.id.btn_filter).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                create();
                break;

            case R.id.btn_from:
                fromRx();
                break;

            case R.id.btn_just:
                just();
                break;
            case R.id.btn_map:
                map();
                break;
            case R.id.btn_flatmap:
                flatmap();
                break;
            case R.id.btn_zip:
                zip();
                break;
            case R.id.btn_zipwith:
                zipwith();
                break;

            case R.id.btn_retry:
                retry();
                break;

            case R.id.btn_retrywhen:
                retrywhen();
                break;

            case R.id.btn_filter:
                filter();
                break;

        }
    }

    /**
     * 过滤
     * 按照规则过滤
     */
    private void filter() {
        Observable.just(0, 1, 2, 3, 4, 5)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer < 4;//过滤规则
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "call: " + integer);
            }
        });


    }

    /**
     * 异常的时候执行
     * 区别：
     * retry直接执行
     * retrywhen 网络请求框架中，一般使用retrywhen,
     * 1.要执行的操作是连接网络（类似总出错），连接出异常的时候，可以直接重复执行连接网络，（retry）
     * 2.同时可以判断连接异常的类型(retrywhen中判断) 再做决定是否重连              (retrywhen)
     */
    private void retrywhen() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.i(TAG, "call: 总出错");
                subscriber.onError(new Throwable());
            }
        }).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observable) {//异常泛型，重试几次 range产生几个observable操作符
                return observable.zipWith(Observable.range(1, 3), new Func2<Throwable, Integer, Integer>() {//合并，返回的泛型延迟几秒
                    @Override
                    public Integer call(Throwable throwable, Integer integer) {
                        return integer;
                    }
                }).flatMap(new Func1<Integer, Observable<?>>() {//3个observable 转换成一个返回
                    @Override
                    public Observable<?> call(Integer integer) {
                        Log.i(TAG, "call: " + "延迟了" + integer + "秒");
                        return Observable.timer(integer, TimeUnit.SECONDS);//timer延迟执行操作符
                    }
                });
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "onNext: " + integer);
            }
        });

    }

    /**
     * 出错的时候，异常的时候重新执行
     * 用处：网络链接异常的时候，重试几次。
     */
    private void retry() {
        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    for (int i = 0; i < 5; i++) {
                        if (i == 3) {
                            throw new Exception("出错了");
                        }
                        subscriber.onNext(i);
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).retry(2).subscribe(new Subscriber<Integer>() {//本身一次，重试两次
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "onNext: " + integer);
            }
        });
    }

    /**
     * 将本身与其他的Observable按照规则严格的合并，多余舍弃
     */
    private void zipwith() {
        Observable.just(10, 20, 30, 40).zipWith(Observable.just("a", "b", "c"), new Func2<Integer, String, String>() {//本身泛型，第二个泛型，合并完成的泛型
            @Override
            public String call(Integer integer, String s) {
                return integer + s;//合并规则
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i(TAG, "call: " + s);
            }
        });

    }

    /**
     * 将两个Observable按照规则严格的合成一个Observable
     */
    private void zip() {
        Observable<Integer> just1 = Observable.just(10, 20, 30);
        Observable<Integer> just2 = Observable.just(1, 2, 3, 4);//4会被舍弃
        Observable.zip(just1, just2, new Func2<Integer, Integer, Integer>() {//第三个类型合成的Observable的类型
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;//定义规则
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "call: " + integer);
            }
        });


    }

    /**
     * 将多个Observable转换成一个Observable然后发送
     */
    private void flatmap() {

        /**
         * 区别
         * map将一种类型转换为另一种类型，这种类型是任意的；
         * flatmap将一种类型转换为Observable类型泛型可以是任意的
         * map将一个Observable转换成一个Observable
         * flatmap将多个Observable转换成一个Observable然后发送
         */
        String[] citys = {"北京", "上海", "南京"};
        Observable.from(citys)
                .flatMap(new Func1<String, Observable<Weather>>() {
                    @Override
                    public Observable<Weather> call(String s) {
                        return getCithWeatherData(s);
                    }
                }).subscribe(new Action1<Weather>() {
            @Override
            public void call(Weather weather) {
                Log.i(TAG, "call: " + weather.name + weather.state);
            }
        });
    }

    /**
     * 获取城市的天气
     *
     * @param city
     * @return
     */
    private Observable<Weather> getCithWeatherData(String city) {
        return Observable.just(city)
                .map(new Func1<String, Weather>() {
                    @Override
                    public Weather call(String s) {
                        //通过网络请求获取天气数据，组装城对象，String转换为对象
                        Weather weather = new Weather();
                        weather.name = s;
                        weather.state = "晴";
                        return weather;
                    }
                });
    }

    /**
     * 变换，转换操作符
     * 作用：在实际开发中获取图片url转换为Bitmap
     * 在该实例中将Integer转换为String
     */
    private void map() {
//        Observable.just(0, 1, 2, 3)
//                .map(new Func1<Integer, String>() {
//                    @Override
//                    public String call(Integer integer) {
//                        return integer + "转换了";
//                    }
//                }).subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                Log.i(TAG, "call: " + s);
//            }
//        });

        String[] urls = {};

        Observable.from(urls)
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        //s为图片的url,通过网络请求获取到图片

                        return null;
                    }
                }).subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                //iv设置图片
            }
        });
    }

    /**
     * 可变参数创建Observable
     */
    private void just() {
        Observable.just(1, 2, 3, 4)
//                .subscribe(new Subscriber<Integer>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.i(TAG, "onCompleted: 我执行完毕了");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Integer s) {
//                        Log.i(TAG, "onNext: 我被执行了" + s);
//                    }
//                });
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, "call: " + integer);
                    }
                });
    }

    /**
     * 数组转换成被观察者
     */
    private void fromRx() {
        Integer[] attr = {1, 2, 3, 4, 5};//基本数据类型报错的 from(attr)
        Observable.from(attr).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: 我执行完毕了");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer s) {
                Log.i(TAG, "onNext: 我被执行了" + s);
            }
        });


    }

    /**
     * 任意类型
     * 创建操作符
     * 观察者，subcreblers
     * 被观察者 Observable
     */
    private void create() {
        //创建的是被观察者
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                /*
                  * 如果没有被取消订阅
                 */
                if (subscriber.isUnsubscribed()) {
                    Log.i(TAG, "call: ----------");
                    subscriber.onNext("1");
                    subscriber.onNext("2");
                    subscriber.onNext("3");
                    subscriber.onNext("4");
                    subscriber.onCompleted();

                }
            }
        });

        observable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: 我执行完毕了");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "onNext: 我被执行了");
            }
        });
    }

    public class Weather {
        public String state;
        public String name;


    }
}
