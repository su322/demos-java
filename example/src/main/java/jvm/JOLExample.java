package jvm; /**
 * 这个示例展示了如何使用 JOL 来查看 Java 对象的内存布局。
 * <p>
 * 需要添加 JOL 依赖到项目中，例如通过 Maven:
 * <dependency>
 *     <groupId>org.openjdk.jol</groupId>
 *     <artifactId>jol-core</artifactId>
 *     <version>0.9</version>
 * </dependency>
 */

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

public class JOLExample {
    public static void main(String[] args) {
        // 打印 JVM 详细信息
        System.out.println(VM.current().details());

        // 创建一个对象
        Object obj = new Object();

        // 打印对象的内存布局 java.lang.Object object internals
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());

        // 查看对象引用大小 ReferernceHolder.ref
        System.out.println(ClassLayout.parseClass(ReferernceHolder.class).toPrintable());
    }

    private static class ReferernceHolder {
        Object ref;
    }

    /*
      # WARNING: Unable to attach Serviceability Agent. You can try again with escalated privileges. Two options: a) use -Djol.tryWithSudo=true to try with sudo; b) echo 0 | sudo tee /proc/sys/kernel/yama/ptrace_scope
      # Running 64-bit HotSpot VM.
      # Using compressed oop with 3-bit shift.
      # Using compressed klass with 3-bit shift.
      # WARNING | Compressed references base/shifts are guessed by the experiment!
      # WARNING | Therefore, computed addresses are just guesses, and ARE NOT RELIABLE.
      # WARNING | Make sure to attach Serviceability Agent to get the reliable addresses.
      # Objects are 8 bytes aligned.
      # Field sizes by type: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
      # Array element sizes: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]

      java.lang.Object object internals:
       OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
            0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
            4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
            8     4        (object header)                           28 0f 00 00 (00101000 00001111 00000000 00000000) (3880)
           12     4        (loss due to the next object alignment)
      Instance size: 16 bytes
      Space losses: 0 bytes internal + 4 bytes external = 4 bytes total

      jvm.JOLExample$ReferernceHolder object internals:
       OFFSET  SIZE               TYPE DESCRIPTION                               VALUE
            0    12                    (object header)                           N/A
           12     4   java.lang.Object ReferernceHolder.ref                      N/A
      Instance size: 16 bytes
      Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
     */
}
