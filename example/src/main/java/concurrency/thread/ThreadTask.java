package concurrency.thread;

public class ThreadTask extends Thread{
    public static void main(String[] args) {
        ThreadTask task = new ThreadTask();
        task.start();
    }

    public void run() {
        System.out.println("ThreadTask");
    }
}
