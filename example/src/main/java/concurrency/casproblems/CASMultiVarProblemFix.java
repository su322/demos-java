package concurrency.casproblems;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 演示：将多个变量封装为一个对象，使用AtomicReference进行CAS原子更新。
 * 这样可以保证多个变量一起原子性地被更新。
 */

public class CASMultiVarProblemFix {
    // 状态对象，包含多个变量，用于更新演示
    static class State {
        final int a, b, c, d;
        State(int a, int b, int c, int d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
        @Override
        public String toString() {
            return "State{a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + '}';
        }
    }

    // 用AtomicReference保存整个状态
    private static final AtomicReference<State> atomicState = new AtomicReference<>(new State(0, 0, 0, 0));

    public static void main(String[] args) throws InterruptedException {
        Runnable updateAll = () -> {
            for (int i = 0; i < 5; i++) {
                State oldState = atomicState.get();
                State newState = new State(oldState.a + 1, oldState.b + 1, oldState.c + 1, oldState.d + 1);
                boolean success = atomicState.compareAndSet(oldState, newState);
                System.out.println(Thread.currentThread().getName() + " 尝试第" + (i+1) + "次: " + (success ? "更新成功" : "更新失败") + ", 当前状态: " + atomicState.get());
                try { Thread.sleep(10); } catch (InterruptedException ignored) {}
            }
        };

        Thread t1 = new Thread(updateAll, "线程1");
        Thread t2 = new Thread(updateAll, "线程2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("最终状态: " + atomicState.get());
    }
}
