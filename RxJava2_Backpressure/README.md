### [测试rxjava的背压问题](https://blog.csdn.net/lyabc123456/article/details/90610890)

#### 什么是被压

* 就是生产者的速度大于消费者的速度
* 翻译成观察者模式
    * 被观察者发射的速度大于观察者消费的速度，从而导致事件丢失，OOM 等异常；

#### 背压策略的选择

* BackpressureStrategy.ERROR
    * 当缓存区大小存满（默认缓存区大小128），被观察者仍然继续发送下一个事件时，直接抛出异常MissingBackpressureException
* BackpressureStrategy.MISSING
    * 当缓存区大小存满，被观察者仍然继续发送下一个事件时，抛出异常MissingBackpressureException , 提示缓存区满了
* BackpressureStrategy.BUFFER
    * 当缓存区大小存满，被观察者仍然继续发送下一个事件时，缓存区大小设置无限大, 即被观察者可无限发送事件，但实际上是存放在缓存区
* BackpressureStrategy.DROP
    * 当缓存区大小存满，被观察者仍然继续发送下一个事件时， 超过缓存区大小（128）的事件会被全部丢弃
* BackpressureStrategy.LATEST
    * 当缓存区大小存满，被观察者仍然继续发送下一个事件时，只保存最新/最后发送的事件， 其他超过缓存区大小（128）的事件会被全部丢弃

#### 背压策略模式的操作符

* onBackpressureBuffer
* onBackpressureDrop
* onBackpressureLatest

#### 可以避免背压问题的操作符
* sample()
* throttle : /'θrɑtl/ 节流，减速；窒息
* throttleLast()
* throttleFirst(): 是把该时间段第一个item发送出去：
* throttleWithTimeout() :只发送两个在规定间隔内的时间发送的序列的最后一个：
* debounce():选择在数据暴增期间将items收集到缓冲区中，并在每个数据暴增周期的结尾时，通过使用debounce()操作符向buffer()操作符发送一个缓冲区关闭指示来发送缓冲区收集的items：
* buffer():可以定期关闭并释放突发性的 Observable 缓冲区;


  