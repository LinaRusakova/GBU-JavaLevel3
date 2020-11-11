package com.gmail.xlinaris.lesson04;

public class Task1ABC {
    enum ABC {A, B, C} //создаем перечисление из необходимых для вывода элементов

    private volatile int j = 0; //переменная константа для последовательного вывода элементов из перечисления
    private final Object mon = new Object(); //объект монитора
    private int numberRepeat = 5; //переменная, определяет число повторов вывода каждого из элементов перечисления

    public static void main(String[] args) throws InterruptedException {
        Task1ABC abc = new Task1ABC();
        new Thread(() -> abc.print(ABC.A)).start(); //вызываем метод для вывода элемента в отдельном потоке
        new Thread(() -> abc.print(ABC.B)).start();
        new Thread(() -> abc.print(ABC.C)).start();
    }

    public void print(ABC letter) {
        synchronized (mon) {
            try {
                for (int i = 0; i < numberRepeat; i++) {
                    for (; j < ABC.values().length; ) { //цикл ограничен количеством элементов перечислении
                        if (j == letter.ordinal()) { //проверяем: совпадает ли переданный методом элемент с нашей последовательной переменной
                            System.out.print(letter); //если да, выводим переменную в консоль
                            j++;                    //инкрементим последовательную переменную
                            mon.notify();           //позволяем предыдущему методу проверить элемент
                        }
                        mon.wait();                 //если элемент не совпадает останавливаем поток и разблокируем монитор
                    }
                    if (i == numberRepeat - 1) break; //если это последний повтор, то выходим из цикла
                    j = 0;                          //иначе обнуляем цикл перебора элементов в перечислении
                    System.out.println();           //выводим пустую строку в консль
                    mon.notifyAll();                //разблокируем монитор для всех потоков
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


}

