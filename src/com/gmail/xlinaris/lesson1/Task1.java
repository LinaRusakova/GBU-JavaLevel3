package com.gmail.xlinaris.lesson1;
/*
 *  Написать метод, который меняет два элемента массива местами.(массив может быть любого ссылочного типа);
 * */

import java.util.Arrays;

public class Task1<T> {
    T[] array;

    Task1(T[] o) {
        array = o;
    }

    void change2Items(int firstEl, int secondEl) {
        T newFirstEl = array[secondEl];
        T newSecondEl = array[firstEl];
        for (int i = 0; i < array.length; i++) {
            if (i == firstEl) array[firstEl] = newFirstEl;
            if (i == secondEl) array[secondEl] = newSecondEl;
        }
    }

    String show(T[] array) {
        return Arrays.toString(array);
    }
}

class DemoTask1 {
    public static void main(String[] args) {
        //Два тестовых массива различных типов
        Integer[] intArray = {1, 2, 3, 4, 5, 6};
        String[] strArray = {"один", "два", "три", "четыре", "пять", "шесть"};
        Task1<Integer> integerTask1= new Task1<>(intArray);
        Task1<String> stringTask1= new Task1<>(strArray);

        System.out.println("First test array: "+integerTask1.show(intArray));
        integerTask1.change2Items(2,4);
        System.out.println("First test array after exchange 2 and 4 elements: "+integerTask1.show(intArray));
        System.out.println();
        System.out.println("Second test array: "+stringTask1.show(strArray));
        stringTask1.change2Items(2,4);
        System.out.println("Second test array after exchange 2 and 4 elements: "+stringTask1.show(strArray));
        System.out.println();
    }

}


