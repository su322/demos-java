package concurrency.thread;

// 我感觉更喜欢这种
public class RunnableTask implements Runnable{
    public static void main(String[] args) {
        RunnableTask task = new RunnableTask();
        Thread thread = new Thread(task);
        thread.start();
    }

    // 直接调用run()只是一个普通的同步方法调用，所有代码都在当前线程中执行，不会创建新线程
    // 调用start()会创建一个新的线程，并异步执行run()方法中的代码
    // 见RunOrStart
    @Override
    public void run() {
        System.out.println("RunnableTask");
    }
}
