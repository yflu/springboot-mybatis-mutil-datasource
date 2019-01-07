package com.eric;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountdownLatchTest {

    public static void main(String[] args) {
        final CountDownLatch orderCd = new CountDownLatch(1);
        final CountDownLatch answerCd = new CountDownLatch(3);
        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 0; i < 3; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println("线程" + Thread.currentThread().getName() + "正准备接受命令");
                    try {
                        orderCd.await();
                        System.out.println("线程" + Thread.currentThread().getName() + "已经接受命令");
                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println("线程" + Thread.currentThread().getName() + "回应命令");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        answerCd.countDown();
                    }
                }
            };
            service.execute(runnable);
        }

        try {
            Thread.sleep((long) (Math.random() * 10000));
            System.out.println("线程" + Thread.currentThread().getName() +
                    "即将发布命令");
            orderCd.countDown();
            System.out.println("线程" + Thread.currentThread().getName() +
                    "已发送命令，正在等待结果");
            answerCd.await(); //命令发送后指挥官处于等待状态，一旦cdAnswer为0时停止等待继续往下执行
            System.out.println("线程" + Thread.currentThread().getName() +
                    "已收到所有响应结果");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        service.shutdown();
    }
}
