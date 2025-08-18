package designpattern;

/**
 * 懒汉式单例模式，首次调用 getInstance() 时才创建实例，支持多线程安全。
 */

public class LazySingleton {
    // 单例对象，初始为 null
    private static volatile LazySingleton instance = null;

    // 私有构造方法，防止外部实例化
    private LazySingleton() {}

    // 双重检查锁定，保证线程安全和性能
    public static LazySingleton getInstance() {
        // 提高性能，只有在 instance 还没创建时才进入同步块，减少不必要的加锁。
        if (instance == null) { // 第一次检查
            synchronized (LazySingleton.class) {
                // 当多个线程同时通过第一次检查进入同步块时，如果没有第二次检查，可能会有多个线程同时创建实例，导致单例失效。
                if (instance == null) { // 第二次检查
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }
}
