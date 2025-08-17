package concurrency.thread;

class RunOrStart extends Thread {
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }
    public static void main(String[] args) throws InterruptedException {
        RunOrStart t1 = new RunOrStart();
        t1.start(); // 正确的⽅式，创建⼀个新线程，并在新线程中执⾏ run()
//        sleep(1000); // 会影响打印的顺序，不写的话就先打印main，写的话就先打印Thread-0
        t1.run(); // 仅在主线程中执⾏ run()，没有创建新线程
    }
}