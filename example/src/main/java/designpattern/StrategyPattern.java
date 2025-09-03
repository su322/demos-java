package designpattern;

// 策略接口
interface Strategy {
    void execute();
}

// 具体策略A
class ConcreteStrategyA implements Strategy {
    public void execute() {
        System.out.println("执行策略A");
    }
}

// 具体策略B
class ConcreteStrategyB implements Strategy {
    public void execute() {
        System.out.println("执行策略B");
    }
}

// 环境类，持有策略对象
class Context {
    private Strategy strategy;

    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void performAction() {
        strategy.execute();
    }
}

public class StrategyPattern {
    public static void main(String[] args) {
        Context context = new Context(new ConcreteStrategyA());
        context.performAction(); // 输出：执行策略A

        context.setStrategy(new ConcreteStrategyB());
        context.performAction(); // 输出：执行策略B
    }
}

// 策略接口
/*
interface SM4Engine {

    /**
     * Process a single 16-byte block.
     *
     * @param input input data array
     * @param inputOffset offset in input array
     * @param output output data array
     * @param outputOffset offset in output array
     * /
    void processBlock(byte[] input, int inputOffset, byte[] output, int outputOffset);
}
*/

// 具体策略A
/*
final class SM4EngineImpl implements SM4Engine {...}
 */

// 具体策略B
/*
final class SM4EngineNative implements SM4Engine {...}
 */

// 环境类，持有策略对象
// createEngine相当于上面的setStrategy，有一点不同，上面是在main方法里创建具体对象，这个相当于直接在环境类里创建具体策略对象
/*
final class SM4Crypt extends SymmetricCipher {

    private SM4Engine engine;

    @Override
    int getBlockSize() {
        return 16;
    }

    /**
     * Creates the appropriate SM4 engine implementation based on availability.
     * Prefers native implementation when available, falls back to Java implementation.
     * /
    // 第二，在确定使用策略模式的基础上，其他地方不判断的话，这里就必须要先判断，只有一处判断总比多处判断好，然后下面的 engine = createEngine(key, !decrypting); 代码，
    // 原来是 engine = new SM4Engine(key, !decrypting);，为了最小化改动前人写的代码，用了这样 return new 的方式，不然还要在别人写的代码上写if-else，不过主要还是导师要求，我现在确实觉得这样挺好看的。
    // createEngine相当于上面的setStrategy
    private static SM4Engine createEngine(byte[] key, boolean encrypt) {
        if (SM4EngineNative.isAvailable()) {
            return new SM4EngineNative(key, encrypt);
        } else {
            return new SM4EngineImpl(key, encrypt);
        }
    }

    @Override
    void init(boolean decrypting, String algorithm, byte[] key)
            throws InvalidKeyException {
        if (!algorithm.equalsIgnoreCase("SM4")) {
            throw new InvalidKeyException("The algorithm must be SM4");
        }

        // Create the appropriate engine implementation
        engine = createEngine(key, !decrypting);
    }

    // 第一，如果不用策略模式统一SM4Engine，上面的创建engine就要判断native是否可用，写if-else，下面的两个方法也要判断，写if-else，改动有点大
    @Override
    void encryptBlock(byte[] plain, int plainOffset,
                      byte[] cipher, int cipherOffset) {
        engine.processBlock(plain, plainOffset, cipher, cipherOffset);
    }

    @Override
    void decryptBlock(byte[] cipher, int cipherOffset,
                      byte[] plain, int plainOffset) {
        engine.processBlock(cipher, cipherOffset, plain, plainOffset);
    }
}
*/
