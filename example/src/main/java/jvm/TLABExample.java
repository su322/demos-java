package jvm;

/**
 * VM 参数（开启或关闭 TLAB）:
 * -XX:+UseTLAB -XX:+PrintGCDetails -XX:+PrintTLAB
 * -XX:-UseTLAB -XX:+PrintGCDetails -XX:+PrintTLAB
 */
public class TLABExample {
    public static void main(String[] args) {
        for (int i = 0; i < 10_000_000; i++) {
            allocate(); // 创建大量对象
        }
        System.gc(); // 强制触发垃圾回收
    }

    private static void allocate() {
        // 见 jvm.JOLExample
        Object obj = new Object();
    }

    // 我感觉这个日志看不太出对象分配的情况，不管了。。。
    /* 开启 TLAB
    TLAB: gc thread: 0x000000011f80a000 [id: 32515] desired_size: 1310KB slow allocs: 0  refill waste: 20968B alloc: 1.00000     6554KB refills: 1 waste 100.0% gc: 1342152B slow: 0B fast: 0B
    TLAB: gc thread: 0x0000000141818000 [id: 17411] desired_size: 1310KB slow allocs: 0  refill waste: 20968B alloc: 1.00000     6554KB refills: 1 waste 84.1% gc: 1128272B slow: 0B fast: 0B
    TLAB: gc thread: 0x000000013f80f800 [id: 4099] desired_size: 1310KB slow allocs: 0  refill waste: 20968B alloc: 1.00000     6554KB refills: 3 waste  5.5% gc: 221920B slow: 0B fast: 0B
    TLAB totals: thrds: 3  refills: 5 max: 3 slow allocs: 0 max 0 waste: 40.1% gc: 2692344B max: 1342152B slow: 0B max: 0B fast: 0B max: 0B
    [GC (System.gc()) [PSYoungGen: 6553K->560K(76288K)] 6553K->568K(251392K), 0.0004704 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
    [Full GC (System.gc()) [PSYoungGen: 560K->0K(76288K)] [ParOldGen: 8K->353K(175104K)] 568K->353K(251392K), [Metaspace: 3240K->3240K(1056768K)], 0.0016920 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
    Heap
     PSYoungGen      total 76288K, used 3277K [0x000000076ab00000, 0x0000000770000000, 0x00000007c0000000)
      eden space 65536K, 5% used [0x000000076ab00000,0x000000076ae334e0,0x000000076eb00000)
      from space 10752K, 0% used [0x000000076eb00000,0x000000076eb00000,0x000000076f580000)
      to   space 10752K, 0% used [0x000000076f580000,0x000000076f580000,0x0000000770000000)
     ParOldGen       total 175104K, used 353K [0x00000006c0000000, 0x00000006cab00000, 0x000000076ab00000)
      object space 175104K, 0% used [0x00000006c0000000,0x00000006c00587c0,0x00000006cab00000)
     Metaspace       used 3249K, capacity 4564K, committed 4864K, reserved 1056768K
      class space    used 349K, capacity 388K, committed 512K, reserved 1048576K
     */

    /* 关闭 TLAB
    [GC (System.gc()) [PSYoungGen: 3413K->528K(76288K)] 3413K->536K(251392K), 0.0004784 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
    [Full GC (System.gc()) [PSYoungGen: 528K->0K(76288K)] [ParOldGen: 8K->353K(175104K)] 536K->353K(251392K), [Metaspace: 3241K->3241K(1056768K)], 0.0015099 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
    Heap
     PSYoungGen      total 76288K, used 32K [0x000000076ab00000, 0x0000000770000000, 0x00000007c0000000)
      eden space 65536K, 0% used [0x000000076ab00000,0x000000076ab080c8,0x000000076eb00000)
      from space 10752K, 0% used [0x000000076eb00000,0x000000076eb00000,0x000000076f580000)
      to   space 10752K, 0% used [0x000000076f580000,0x000000076f580000,0x0000000770000000)
     ParOldGen       total 175104K, used 353K [0x00000006c0000000, 0x00000006cab00000, 0x000000076ab00000)
      object space 175104K, 0% used [0x00000006c0000000,0x00000006c00587c0,0x00000006cab00000)
     Metaspace       used 3253K, capacity 4564K, committed 4864K, reserved 1056768K
      class space    used 349K, capacity 388K, committed 512K, reserved 1048576K
     */
}