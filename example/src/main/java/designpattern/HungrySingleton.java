package designpattern;

/**
 * 饿汉式单例模式，类加载时即创建实例，线程安全，推荐用于单例对象不占用太多资源的场景。
 */

public class HungrySingleton {
    // 在类加载时就创建单例对象
    private static final HungrySingleton INSTANCE = new HungrySingleton();

    // 私有构造方法，防止外部实例化
    private HungrySingleton() {}

    // 提供全局访问点
    public static HungrySingleton getInstance() {
        return INSTANCE;
    }
}
