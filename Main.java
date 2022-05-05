package com.company;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    static int count = 5; // кількість філософів та виделок
    static Philosopher philosophers[] = new Philosopher[count]; // масив філософів
    static Fork fork[] = new Fork[count]; // масив виделок



    static class Fork {

        public Semaphore semaphore = new Semaphore(1); // семафор який відповідає за дозвіл брати виделку

        void takeFork() {
            try {
                semaphore.acquire(); // отримати дозвіл
            }
            catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        void release() {

            semaphore.release(); // збільшуємо семафор на 1, потрібний для подальшого дозволу
        }

        boolean isFree() {

            return semaphore.availablePermits() > 0; // повертає істину якщо дозвіл більше 1
        }

    }

    static class Philosopher extends Thread {

        int number;
        Fork leftfork;
        Fork rightfork;


        Philosopher(int num, Fork left, Fork right) {
            number = num;
            leftfork = left;
            rightfork = right;
        }

        public void run(){

            while (true) {

                leftfork.takeFork(); // взяти ліву виделку
                System.out.println("Філософ " + (number+1) + " обрав ліву виделку.");
                rightfork.takeFork(); // взяти праву виделку
                System.out.println("Філософ " + (number+1) + " обрав праву виделку.");
                eat(); // метод повертає час коли філософ їсть
                leftfork.release(); // звільнити ліву виделку
                System.out.println("Філософ " + (number+1) + " полкав ліву виделку.");
                rightfork.release(); // звільнити праву виделку
                System.out.println("Філософ " + (number+1) + " поклав праву виделку.");

            }
        }

        void eat() {
            try {
                int sleepTime = ThreadLocalRandom.current().nextInt(0, 1000); // рандомне число часу роботи філософа коли він їсть
                System.out.println("Філософ " + (number+1) + " обідав " + sleepTime + " секунд.");
                Thread.sleep(sleepTime);
            }
            catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

    }

    public static void main(String argv[]) {

        System.out.println("Обід розпочато!");

        for (int i = 0; i < count; i++) {
            fork[i] = new Fork();
        }

        for (int i = 0; i < count; i++) {
            philosophers[i] = new Philosopher(i, fork[i], fork[(i + 1) % count]);
            philosophers[i].start();
        }

        while (true) {
            try {
                // зупинка поток на 2 секунди
                Thread.sleep(2000);

                // перевірка тупикової ситуації
                boolean deadlock = true;
                for (Fork f : fork) {
                    if (f.isFree()) {
                        deadlock = false;
                        break;
                    }
                }
                if (deadlock) {
                    Thread.sleep(2000);
                    System.out.println("Обід завершено!");
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }


        System.exit(0);
    }

}
