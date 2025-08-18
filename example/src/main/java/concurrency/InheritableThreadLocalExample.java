package concurrency;

public class InheritableThreadLocalExample {
    // 定义一个 InheritableThreadLocal 实例，初始值为 "父线程值"
    private static final InheritableThreadLocal<String> inheritableThreadLocal =
            new InheritableThreadLocal<String>() {
                @Override
                protected String initialValue() {
                    return "父线程值";
                }
            };

    public static void main(String[] args) {
        // 主线程设置变量
        inheritableThreadLocal.set("主线程设置的值");
        System.out.println("主线程中的值: " + inheritableThreadLocal.get());

        // 启动子线程
        Thread childThread = new Thread(() -> {
            // 子线程读取继承的值
            System.out.println("子线程启动时继承的值: " + inheritableThreadLocal.get());
            // 子线程修改自己的副本
            inheritableThreadLocal.set("子线程修改后的值");
            System.out.println("子线程修改后的值: " + inheritableThreadLocal.get());
        });
        childThread.start();

        // 等待子线程结束
        try {
            childThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 主线程再次读取自己的值，验证互不影响
        System.out.println("主线程最终的值: " + inheritableThreadLocal.get());
    }
}
