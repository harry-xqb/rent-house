package com.harry.renthouse.teach;

/**
 * @author Harry Xu
 * @date 2020/7/24 17:12
 */
public class Test {

    public static void main(String[] args) {
        // 创建一个猫对象
        Cat cat = new Cat();
        // 将当前对象作为参数传入show方法并执行
        show(cat);
    }

    public static void show(Animal animal){
        // 判断当前动物的具体类型
        if(animal instanceof Cat){ // 如果当前动物对象是猫的实例
            // 将当前动物强制类型转换成猫
            Cat cat = (Cat)animal;
            // 调用猫的work方法
            cat.work();
            return;
        }
        if(animal instanceof Dog){ // 如果当前动物对象是狗的实例
            // 将当前动物强制类型转换成狗
            Dog dog = (Dog)animal;
            // 调用狗的work方法
            dog.work();
        }
    }
}




abstract class Animal{
    abstract void work();
}

class Cat extends Animal{

    @Override
    void work() {

    }
}
class Dog extends Animal{

    @Override
    void work() {

    }
}
