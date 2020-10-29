package com.gmail.xlinaris.lesson1;

import java.io.PrintStream;
import java.util.*;

/*
 * 3. Большая задача:
 *
 *        a. Есть классы Fruit -> Apple, Orange;(больше фруктов не надо)
 *       b. Класс Box в который можно складывать фрукты, коробки условно сортируются по типу фрукта, поэтому в одну коробку нельзя сложить и яблоки, и апельсины;
 *
 *        c. Для хранения фруктов внутри коробки можете использовать ArrayList;
 *        d. Сделать метод getWeight() который высчитывает вес коробки, зная количество фруктов и вес одного фрукта(вес яблока - 1.0f, апельсина - 1.5f, не важно в каких это единицах);
 *        e. Внутри класса коробка сделать метод compare, который позволяет сравнить текущую коробку с той, которую подадут в compare в качестве параметра, true - если их веса равны, false в противном случае(коробки с яблоками мы можем сравнивать с коробками с апельсинами);
 *        f. Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую коробку(помним про сортировку фруктов, нельзя яблоки высыпать в коробку с апельсинами), соответственно в текущей коробке фруктов не остается, а в другую перекидываются объекты, которые были в этой коробке;
 *        g. Не забываем про метод добавления фрукта в коробку.
 *
 * Часть кода сейчас мне уже кажется  избыточной, но оставила так. :)
 **/
public class Task3 {

}

//создаем абстрактный класс фрукты
abstract class Fruit {

    public float getWeight() {
        return weight;
    }

    final float weight = 0f;
    String type;

    public String getType() {
        return type;
    }
}

//создаем наследуемый от класса фрукты класс яблоки
class Apple extends Fruit {
    final String type = "apple";
    final float weight = 1.0f;

    public String getType() {
        return type;
    }

    public float getWeight() {
        return weight;
    }
}

//создаем наследуемый от класса фрукты класс апельсин
class Orange extends Fruit {
    final String type = "orange";
    final float weight = 1.5f;

    public String getType() {
        return type;
    }

    public float getWeight() {
        return weight;
    }
}

class Box<T extends Fruit> {
    String nameBox = "Box"; // имя коробки "по-умолчанию"

    public String getTypeFruits() {
        return typeFruits;
    }

    public void setTypeFruits(String typeFruits) {
        this.typeFruits = typeFruits;
    }

    String typeFruits;
    ArrayList<T> fruitsInBox = new ArrayList<T>(); // Коллекция объекты в коробке
    float weight = 0f; //вес коробки, вес НЕТТО=0.

    public int getCountFruits() {
        return countFruits;
    }

    public void setCountFruits(int countFruits) {
        this.countFruits = countFruits;
    }

    int countFruits = fruitsInBox.size();//количество фруктов в коробке

    Box(String name) {
        nameBox = name;
        fruitsInBox = new ArrayList<T>(0); // Коллекция объекты в коробке
    }

    public void receiveFruits(Fruit fruit) {
        typeFruits = fruit.getType(); //получаем тип помещаемого в коробку фрукта
        float weightFruit = fruit.getWeight();  //получаем вес помещаемого в кробку фрукта
        fruitsInBox.add((T) fruit); // добавляем в коллекцию коробки полученный фрукт
        rpintInfoAddedfruits(weightFruit);
    }


    private void rpintInfoAddedfruits(float weightFruit) {
        countFruits = fruitsInBox.size(); //подсчитываем общее количество фруктов в корбке после добавления
        weight = getWeight(fruitsInBox, countFruits, weightFruit); //получаем вес коробки с фруктами
        printInfoAboutBox(typeFruits); //выводим информации о коробке
    }

    private PrintStream printInfoAboutBox(String typeFruits) {
        return System.out.printf("В коробке %s сложены %s,  - %d шт., общий вес коробки: %.1f. %n", nameBox, typeFruits, countFruits, weight);
    }


    public float getWeight(ArrayList<?> fruits, int countFruits, float weightFruit) { //простой метод подсчета веса коробки
        return countFruits * weightFruit;
    }

    public boolean compareBoxes(Box box) {
// это возможно, не очень корректная реализация метода, но исходя из условия задачи, мне кажется вполне допустимой :-)))
        boolean weightMax = (Float.compare(box.weight, weight) == 0) ? true : false;
        System.out.printf("%n>>>Сравнение веса коробок: равен ли вес коробки %s и вес коробки %s: %b.%n%n", this.nameBox, box.nameBox, weightMax);
        return weightMax;

    }


    //метод для пересыпки фруктов из данной коробки в другую - в коробку назначения.
    //пересыпка идет только если  типы фрутков в коробке совпадают или если в коробке назначения фруктов нет.
    public void shiftFruits(Box boxTarget) {

        String inMessage = ""; //инфо о состоянии коробок до пересыпания
        String outMessage = ""; // инфо о состоянии коробок после пересыпания
        inMessage = String.format("Попытка пересыпать фрукты из коробки %s (содержит %d %s) " +
                        "в коробку %s (содержит %d %s):",
                nameBox, countFruits, typeFruits,
                boxTarget.nameBox, boxTarget.countFruits, boxTarget.typeFruits);
        System.out.println(inMessage);
        if (((typeFruits == boxTarget.getTypeFruits() && countFruits != 0) || (boxTarget.countFruits == 0 && this.countFruits != 0)) && !this.equals(boxTarget)) { // проверка на соотвтствие типа фруктов, на наличие фруктов в коробке-источнике, на совпадине коробки-источника и коробки-цели

        //вариант складывания в новую коробку по одному яблоку
                    int numbers=this.fruitsInBox.size();
                    for (int i = 0; i < numbers; i++) {
                        boxTarget.receiveFruits(fruitsInBox.get(i));
                    }

            fruitsInBox.clear(); //очищаем коробку из которой переложили все яблоки
            countFruits = fruitsInBox.size(); // обновляем значение поля коробки

            //boxTarget.fruitsInBox.add(fruitsInBox.indexOf(0));
            outMessage = String.format("успешно завершена: в коробке %s теперь содержится %d  %s, " +
                            "в коробке %s теперь содержится %d  %s.%n",
                    nameBox, countFruits, typeFruits,
                    boxTarget.nameBox, boxTarget.countFruits, boxTarget.typeFruits);
        } else {
            outMessage = String.format("завершилась неудачей: фрукты из коробки %s (содержит %d %s) " +
                            "невозможно пересыпать в коробку %s (содержит %d %s). " +
                            "Выберите другую коробку.%n",
                    nameBox, countFruits, typeFruits, boxTarget.nameBox, boxTarget.countFruits, boxTarget.typeFruits);
        }
        System.out.println(outMessage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box<?> box = (Box<?>) o;
        return Float.compare(box.weight, weight) == 0 &&
                countFruits == box.countFruits &&
                Objects.equals(nameBox, box.nameBox) &&
                Objects.equals(typeFruits, box.typeFruits) &&
                Objects.equals(fruitsInBox, box.fruitsInBox);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameBox, typeFruits, fruitsInBox, weight, countFruits);
    }
}

class DemoTask3 {
    private static Apple apple;
    private static Orange orange;

    public static <orange> void main(String[] args) {
        apple = new Apple();
        //Два тестовых массива различных типов: яблок и апельсинов
        Apple[] apples = {apple, apple, apple, apple};
        orange = new Orange();
        Orange[] oranges = {orange, orange, orange, orange, orange};


        Box box1 = new Box("box1");
        Box box2 = new Box("box2");
        Box box3 = new Box("box3");
        //тестируем метод добавления фруктов в коробки
        box1.receiveFruits(apples[0]);
        box1.receiveFruits(apples[1]);
        box1.receiveFruits(apples[2]);
        System.out.println();
        box2.receiveFruits(oranges[0]);
        box2.receiveFruits(oranges[1]);
//тестируем метод сравнения веса коробок (если вес одинаковый)
        box1.compareBoxes(box2);

        box2.receiveFruits(oranges[2]);
        box2.receiveFruits(oranges[3]);

        box1.compareBoxes(box2);
        //тестируем метод сравнения веса коробок (если вес разный)
        System.out.println();

        System.out.println(">>>>Тестируем метод пересыпания фруктов из одной коробки в другую:\n");
        box3.shiftFruits(box1);
        box1.shiftFruits(box2);
        box1.shiftFruits(box3);
        box3.shiftFruits(box2);
        box3.shiftFruits(box3);
        System.out.println();
    }

}