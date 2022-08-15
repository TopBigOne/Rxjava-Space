package com.jar.rxjava2_backpressure;

import android.util.Log;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : dev
 * @version :
 * @Date :  2022/8/15 17:29
 * @Desc :
 */
public class MainFlowableFunction {
    private static final String TAG = "MainFlowableFunction : ";
    private Subscription mSubscriptionSecond;

    public MainFlowableFunction() {
        System.err.println("============ 测试rxjava2的被压问题 ==========");
    }

    public void startOne() {
        // 创建被观察者Flowable
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                        Log.d(TAG, "发送事件 1");
                        emitter.onNext(1);
                        Log.d(TAG, "发送事件 2");
                        emitter.onNext(2);
                        Log.d(TAG, "发送事件 3");
                        emitter.onNext(3);
                        Log.d(TAG, "发送完成");
                        emitter.onComplete();
                    }
                }, BackpressureStrategy.ERROR)//传入背压参数BackpressureStrategy
                .subscribeOn(Schedulers.io()) // 设置被观察者在io线程中进行
                .observeOn(AndroidSchedulers.mainThread()) // 设置观察者在主线程中进行
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        //观察者通过request获取事件
                        // 这就说明观察者能够接收3个事件（多出的事件存放在缓存区）
                        s.request(3);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "--->接收到了事件" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "final ---> onComplete");
                    }
                });
    }

    public void startTwo() {
        //每次点击按钮获取2个事件，直到获取完毕
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                        Log.d(TAG, "发送事件 1");
                        emitter.onNext(1);
                        Log.d(TAG, "发送事件 2");
                        emitter.onNext(2);
                        Log.d(TAG, "发送事件 3");
                        emitter.onNext(3);
                        Log.d(TAG, "发送事件 4");
                        emitter.onNext(4);
                        Log.d(TAG, "发送完成");
                        emitter.onComplete();
                    }
                }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()) // 设置被观察者在io线程中进行
                .observeOn(AndroidSchedulers.mainThread()) // 设置观察者在主线程中进行

                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        // 保存Subscription对象，等待点击按钮时观察者再接收事件
                        mSubscriptionSecond = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "----->接收到了事件" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

        mSubscriptionSecond.request(2);
    }

    public void startThree() {
        Log.d(TAG, "startThree: 如果是同步订阅关系，则不存在缓冲区，在同步订阅当中，被观察者发送一个事件后，要等到观察者接收以后才能继续发送下一个事件");
        Log.d(TAG, "startThree: 观察者和被观察者都在 main 线程");
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                        // 发送3个事件
                        Log.d(TAG, "发送了事件1  thread name :" + Thread.currentThread().getName());
                        emitter.onNext(1);

                        Log.d(TAG, "发送了事件2  thread name :" + Thread.currentThread().getName());
                        emitter.onNext(2);

                        Log.d(TAG, "发送了事件3  thread name :" + Thread.currentThread().getName());
                        emitter.onNext(3);

                        emitter.onComplete();
                    }
                }, BackpressureStrategy.ERROR)
                //.subscribeOn(Schedulers.io()) // 设置 被观察者在io线程中进行
                // .observeOn(AndroidSchedulers.mainThread()) // 设置观察者在主线程中进行
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        s.request(3);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "---->接收到了事件 " + integer + " and the thread name is :" + Thread.currentThread().getName() + "\t");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    public void startFour() {
        String decc = "所以同步订阅关系中没有流速不一致的问题，因为是同步的，会阻塞等待，\n" + "但是却会出现被观察者发送事件数量 大于观察者接收事件数量的问题。例如，观察者只接受3个事件，但被观察者却发送了4个事件就会出问题";
        Log.d(TAG, "startFour: " + decc);
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                        // 发送3个事件
                        Log.d(TAG, "发送了事件1  thread name :" + Thread.currentThread().getName());
                        emitter.onNext(1);

                        Log.d(TAG, "发送了事件2  thread name :" + Thread.currentThread().getName());
                        emitter.onNext(2);

                        Log.d(TAG, "发送了事件3  thread name :" + Thread.currentThread().getName());
                        emitter.onNext(3);

                        Log.d(TAG, "发送了事件4  thread name :" + Thread.currentThread().getName());
                        emitter.onNext(4);

                        emitter.onComplete();
                    }
                }, BackpressureStrategy.ERROR)
                //.subscribeOn(Schedulers.io()) // 设置 被观察者在io线程中进行
                // .observeOn(AndroidSchedulers.mainThread()) // 设置观察者在主线程中进行
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        s.request(3);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "---->接收到了事件 " + integer + " and the thread name is :" + Thread.currentThread().getName() + "\t");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    public void startFive() {
        String desc = "解决方法：控制被观察者发送事件的数量，主要通过FlowableEmitter类的requested()方法实现，\n" + "被观察者通过 FlowableEmitter.requested()可获得观察者自身接收事件的能力，\n" + "从而根据该信息控制事件发送速度，从而达到了观察者反向控制被观察者的效果。\n";
        Log.d(TAG, "startFive: " + desc);

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                // 调用emitter.requested()获取当前观察者需要接收的事件数量
                long n = emitter.requested();
                Log.d(TAG, "--------------: 在 subscribe中获取 观察者可接收事件个数 : " + n);

                // 根据emitter.requested()的值，即当前观察者需要接收的事件数量来发送事件
                for (int i = 0; i < n; i++) {
                    Log.d(TAG, "发送事件: " + i + "---↓");
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.ERROR).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                int max = 16;
                Log.d(TAG, "--------------:  在onSubscribe设置观察者每次能接受" + max + "个事件");
                s.request(max);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "接收事件: " + integer + "---↑");
                Log.d(TAG, " ");
            }

            @Override
            public void onError(Throwable t) {
                Log.w(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });


    }

    public void startSix() {
        String desc = "Subscription request() 的可叠加性";
        Log.d(TAG, "startFive: " + desc);

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                // 调用emitter.requested()获取当前观察者需要接收的事件数量
                long n = emitter.requested();
                Log.d(TAG, "--------------: 在 subscribe中获取 观察者可接收事件个数 : " + n);

                // 根据emitter.requested()的值，即当前观察者需要接收的事件数量来发送事件
                for (int i = 0; i < n; i++) {
                    Log.d(TAG, "发送事件: " + i + "---↓");
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.ERROR).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                int max1 = 3;
                int max2 = 5;
                Log.d(TAG, "--------------:  注意，我在这里设置里两次哦");
                Log.d(TAG, "--------------:  在onSubscribe设置观察者每次能接受" + max1 + "个事件");
                s.request(max1);

                Log.d(TAG, "--------------:  在onSubscribe设置观察者每次能接受" + max2 + "个事件");
                s.request(max2);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "接收事件: " + integer + "---↑");
                Log.d(TAG, " ");
            }

            @Override
            public void onError(Throwable t) {
                Log.w(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }

    public void startSeven() {
        String desc = "每次发送事件后，emitter.requested()会实时更新观察者能接受的剩余事件数量\n";
        Log.d(TAG, "startSeven: " + desc);

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                // 1. 调用emitter.requested()获取当前观察者需要接收的事件数量
                Log.d(TAG, "观察者可接收事件数量 = " + emitter.requested());

                // 2. 每次发送事件后，emitter.requested()会实时更新观察者能接受的事件
                // 即一开始观察者要接收10个事件，发送了1个后，会实时更新为9个
                Log.d(TAG, "发送了事件 1");
                emitter.onNext(1);
                Log.d(TAG, "发送了事件1后, 还需要发送事件数量 = " + emitter.requested());

                Log.d(TAG, "发送了事件 2");
                emitter.onNext(2);
                Log.d(TAG, "发送事件2后, 还需要发送事件数量 = " + emitter.requested());

                Log.d(TAG, "发送了事件 3");
                emitter.onNext(3);
                Log.d(TAG, "发送事件3后, 还需要发送事件数量 = " + emitter.requested());

                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
                // 设置观察者每次能接受10个事件
                s.request(10);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "接收到了事件" + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.w(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }

    public void startEight() {
        String desc = "当FlowableEmitter.requested()减到0时，代表观察者已经不可接收事件，\n" + "若此时被观察者继续发送事件，则会抛出MissingBackpressureException异常。";
        Log.d(TAG, "startEight: " + desc);

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "观察者可接收事件数量 = " + emitter.requested());
                Log.d(TAG, "发送了事件 1");
                emitter.onNext(1);
                Log.d(TAG, "发送了事件1后, 还需要发送事件数量 = " + emitter.requested());
                Log.d(TAG, "发送了事件 2");
                emitter.onNext(2);
                Log.d(TAG, "发送事件2后, 还需要发送事件数量 = " + emitter.requested());
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
                // 设置观察者每次能接受1个事件
                s.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "接收到了事件 : " + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.w(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });

    }

    public void startNine(BackpressureStrategy strategy) {
        String str = getBackpressureStrategyDesc(strategy);
        Log.d(TAG, "startNine: 被压异常类型：" + strategy.name());
        Log.d(TAG, "startNine: 被压异常描述：" + str);
        int maxCount = 128;
        final int[] testCount = {maxCount + 1};
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                        long requestedCount = emitter.requested();
                        Log.d(TAG, "subscribe: requestedCount : " + requestedCount);
                        if (strategy == BackpressureStrategy.DROP || strategy == BackpressureStrategy.BUFFER || strategy == BackpressureStrategy.LATEST) {
                            testCount[0] = 150;
                        }
                        for (int i = 0; i < testCount[0]; i++) {
                            emitter.onNext(i);
                            Log.d(TAG, "发送了事件" + i);
                        }
                        emitter.onComplete();
                    }
                }, strategy)// 设置被压异常策略
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        if (strategy == BackpressureStrategy.DROP || strategy == BackpressureStrategy.BUFFER || strategy == BackpressureStrategy.LATEST) {
                            s.request(testCount[0]);
                        }

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "====================> 接收到了事件 : " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        String string = "";
                        if (strategy == BackpressureStrategy.DROP) {
                            string = strategy.name() + " 模式:只接收到127, 127后面的都没了, 即被丢弃了";
                        }
                        if (strategy == BackpressureStrategy.LATEST) {
                            string = strategy.name() + " 尝试拉取150个事件，但只能取到 前127 + 最后1个（第149个） 事件";
                        }

                        Log.d(TAG, "onComplete:" + string);
                    }
                });
    }

    public void startTen(BackpressureMode backpressureMode) {

        // 通过interval自动创建被观察者Flowable（从0开始每隔1ms发送1个事件）
        Flowable.interval(1, TimeUnit.MILLISECONDS) //interval操作符会默认新开1个新的工作线程
                .onBackpressureBuffer()// 此处选择Buffer背压模式，即缓存区大小无限制
                .observeOn(Schedulers.newThread()) //观察者同样工作在一个新开线程中
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        //默认可以接收Long.MAX_VALUE个事件
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "onNext 消费:" + aLong);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    private String getBackpressureStrategyDesc(BackpressureStrategy strategy) {
        if (strategy == BackpressureStrategy.ERROR) {
            return " 测试：BackpressureStrategy.ERROR, 当缓存区大小存满（默认缓存区大小128），\n" + "被观察者仍然继续发送下一个事件时，直接抛出异常MissingBackpressureException";
        }
        if (strategy == BackpressureStrategy.MISSING) {
            return "当缓存区大小存满，被观察者仍然继续发送下一个事件时，抛出异常MissingBackpressureException , 提示缓存区满了";
        }

        if (strategy == BackpressureStrategy.BUFFER) {
            return "当缓存区大小存满，被观察者仍然继续发送下一个事件时，缓存区大小设置无限大, 即被观察者可无限发送事件，但实际上是存放在缓存区";
        }

        if (strategy == BackpressureStrategy.DROP) {
            return "当缓存区大小存满，被观察者仍然继续发送下一个事件时， 超过缓存区大小（128）的事件会被全部丢弃";
        }
        if (strategy == BackpressureStrategy.LATEST) {
            return "当缓存区大小存满，被观察者仍然继续发送下一个事件时，只保存最新/最后发送的事件， 其他超过缓存区大小（128）的事件会被全部丢弃";
        }

        return "设置的被压异常类型不存在";
    }


}
