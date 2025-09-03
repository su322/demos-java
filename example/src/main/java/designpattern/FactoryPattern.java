package designpattern;

// 产品接口
interface Product {
    void use();
}

// 具体产品A 混凝土
class ConcreteProductA implements Product {
    public void use() {
        System.out.println("使用产品A");
    }
}

// 具体产品B
class ConcreteProductB implements Product {
    public void use() {
        System.out.println("使用产品B");
    }
}

// 工厂接口
interface Factory {
    Product createProduct();
}

// 具体工厂A
class ConcreteFactoryA implements Factory {
    public Product createProduct() {
        System.out.println("生产产品A");
        return new ConcreteProductA();
    }
}

// 具体工厂B
class ConcreteFactoryB implements Factory {
    public Product createProduct() {
        System.out.println("生产产品B");
        return new ConcreteProductB();
    }
}

public class FactoryPattern {
    public static void main(String[] args) {
        Factory factoryA = new ConcreteFactoryA();
        Product productA = factoryA.createProduct();
        productA.use();

        Factory factoryB = new ConcreteFactoryB();
        Product productB = factoryB.createProduct();
        productB.use();
    }
}
