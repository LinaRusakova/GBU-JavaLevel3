package com.gmail.xlinaris.lesson1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/*
* 2. Написать метод, который преобразует массив в ArrayList;
* */
public class Task2<T> {
    T[] array;

    Task2(T[] o) {
        array = o;
    }

    String show(T[] array) {
        return Arrays.toString(array);
    }

    ArrayList<T> change2ArrayList(T[] array) {
        ArrayList<T> arrayList=new ArrayList<>(array.length);
        Collections.addAll(arrayList, array);
        return (arrayList);
    }


    ArrayList<T> newArrayList(T[] array) {
        return change2ArrayList(array);
    }
}

class DemoTask2 {
    public static void main(String[] args) {
        //Два тестовых массива различных типов
        Integer[] intArray = {1, 2, 3, 4, 5, 6};
        String[] strArray = {"один", "два", "три", "четыре", "пять", "шесть"};
        Task2<Integer> integerTask2= new Task2<>(intArray);
        Task2<String> stringTask2= new Task2<>(strArray);

        System.out.println("First test array: "+integerTask2.show(intArray));
        System.out.println("ArrayList from IntegerArray: "+integerTask2.newArrayList(intArray).toString());
        System.out.println();
        System.out.println("Second test array: "+stringTask2.show(strArray));
        System.out.println("ArrayList from StringArray: "+stringTask2.newArrayList(strArray).toString());
        System.out.println();
    }

}


