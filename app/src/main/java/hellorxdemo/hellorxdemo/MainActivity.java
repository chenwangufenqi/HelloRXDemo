package hellorxdemo.hellorxdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {


    private View iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_logo = findViewById(R.id.iv_logo);
    }

    public void btn0(View view) {

        Subscriber<String> sub = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e("TAG", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG", e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e("TAG", s);
            }
        };

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello world 0");
                subscriber.onNext("hello world 00");
                subscriber.onNext("hello world 000");
                //subscriber.onError(new Throwable("出错了。。"));
                subscriber.onCompleted();
                subscriber.onError(new Throwable("Error0"));
            }
        });


        observable.subscribe(sub);

    }

    public void btn1(View view) {
        Subscriber<String> sub = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e("TAG", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG", e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e("TAG", s);
            }
        };

        Observable<String> observable = Observable.just("hello world1", "hello world11", "hello world111");
        observable.subscribe(sub);
    }

    public void btn2(View view) {
        String[] strs = {"hello world2", "hello world22", "hello world222"};
        Observable.from(strs).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e("TAG", s);
            }
        });
    }

    public void btn3(View view) {

        Action0 onCompleted = new Action0() {
            @Override
            public void call() {
                Log.e("TAG", "onCompleted");
            }
        };

        Action1<String> onNext = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e("TAG", s);
            }
        };

        Action1<Throwable> onError = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("TAG", "onError");
            }
        };

        Observable<String> ob = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello worlda");
                subscriber.onNext("hello worldaa");
                subscriber.onNext("hello worldaaa");
                subscriber.onCompleted();
                subscriber.onError(new Throwable());
            }
        });

        ob.subscribe(onNext);
        SystemClock.sleep(2000);
        ob.subscribe(onNext, onError);
        SystemClock.sleep(2000);
        ob.subscribe(onNext, onError, onCompleted);


    }

    public void btn4(View view) {
        Subscriber<String> sub = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e("TAG", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG", e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e("TAG", s);
            }
        };

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello world e");
                subscriber.onNext("hello world ee" + (4 / 0));
                subscriber.onNext("hello world eee");
                subscriber.onCompleted();
                subscriber.onError(new Throwable());
            }
        });


        observable.subscribe(sub);
    }

    public void btn5(View view) {
        final int drawableRes = R.drawable.img;
        final ImageView imageView = (ImageView) MainActivity.this.findViewById(R.id.img);

        Observable.create(new Observable.OnSubscribe<Drawable>() {
            //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getTheme().getDrawable(drawableRes);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<Drawable>() {
            @Override
            public void onNext(Drawable drawable) {
                imageView.setImageDrawable(drawable);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 到这里
     * 创建出 Observable 和 Subscriber ，再用 subscribe() 将它们串起来，一次 RxJava 的基本使用就完成了。非常简单。
     * 然而，
     *
     * 这并没有什么diao用
     *
     *
     * 在 RxJava 的默认规则中，事件的发出和消费都是在同一个线程的。
     * 也就是说，如果只用上面的方法，实现出来的只是一个同步的观察者模式。
     * 观察者模式本身的目的就是『后台处理，前台回调』的异步机制，
     * 因此异步对于 RxJava 是至关重要的。而要实现异步，则需要用到 RxJava 的另一个概念： Scheduler 。
     */


    /**
     * Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
     * <p/>
     * Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
     * <p/>
     * Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。
     * 行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，
     * 可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
     * <p/>
     * Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，
     * 即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，
     * 大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
     * <p/>
     * 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。
     *
     * @param view
     */
    public void btn6(View view) {
        final int drawableRes = R.drawable.img;
        final ImageView imageView = (ImageView) MainActivity.this.findViewById(R.id.img);

        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                SystemClock.sleep(4000);
                Drawable drawable = getTheme().getDrawable(drawableRes);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Drawable>() {
                    @Override
                    public void onNext(Drawable drawable) {
                        imageView.setImageDrawable(drawable);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    public void btn7(View view) {

        Observable.just(R.drawable.img) // 输入类型 String
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Integer, Bitmap>() {
                    @Override
                    public Bitmap call(Integer res) { // 参数类型 String
                        return getBitmapFromPath(res); // 返回类型 Bitmap
                    }
                })
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) { // 参数类型 Bitmap
                        showBitmap(bitmap);
                    }
                });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showBitmap(Bitmap bitmap) {
        iv_logo.setBackground(new BitmapDrawable(bitmap));
    }

    private Bitmap getBitmapFromPath(int res) {
        return BitmapFactory.decodeResource(new Resources(MainActivity.this.getAssets(),null,null),res);
    }

}


