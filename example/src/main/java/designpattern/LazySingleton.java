package designpattern;

/**
 * 懒汉式单例模式，首次调用 getInstance() 时才创建实例，支持多线程安全。
 */

public class LazySingleton {
    // 单例对象，初始为 null
    // 正确的方式：加上 volatile 关键字，禁止指令重排
    // 对象创建过程主要有三步：分配内存空间，初始化对象，将对象引用赋值给变量
    // JVM 和 CPU 为了优化性能，可能会把“将对象引用赋值给变量”提前到“初始化对象”之前。
    // 这样，另一个线程可能会在 instance 已经被赋值但对象还没初始化完成时拿到 instance，导致使用到一个未初始化的对象，出现异常或错误。
    private static volatile LazySingleton instance = null;

    // 私有构造方法，防止外部实例化
    private LazySingleton() {}

    // 双重检查锁定，保证线程安全和性能
    public static LazySingleton getInstance() {
        // 提高性能，只有在 instance 还没创建时才进入同步块，减少不必要的加锁。
        if (instance == null) { // 第一次检查
            synchronized (LazySingleton.class) {
                // 当多个线程同时通过第一次检查时，只有一个线程进入同步块创建单例对象，其他线程阻塞，
                // 如果没有第二次检查，通过第一次检查的线程就会直接进入同步块创建对象，所以第二次检查是为了防止第一个线程创建实例后，后续线程重复创建实例，确保单例安全。
                if (instance == null) { // 第二次检查
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }
}
