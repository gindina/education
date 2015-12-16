package ru.dg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import javax.naming.InsufficientResourcesException;

/**
 * Created by К on 05.12.2015.
 */
public class Operations {
    final static long WAIT_SEC = 3;

   /* public static void main(String[] args) {
        final Account a = new Account(3000);
        final Account b = new Account(5000);

        try {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        System.out.println("Enter new Thread");
                        transfer(a, b, 500);
                        System.out.println("Transfer in new Thread is successfully ended");
                    } catch (InsufficientResourcesException e) {

                    }
                }
            }).start();

            transfer(b, a, 400);
            System.out.println("Transfer is ended");
        } catch (InsufficientResourcesException e) {
            System.out.println("Transfer is cancelled due to insufficient balance on account");
        }
    } */

    public static void main(String[] args) {
        Random rnd = new Random();
        ExecutorService service = Executors.newFixedThreadPool(3);
        final Account acc1 = new Account(1500);
        Account acc2 =  new Account(200);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("Schedule: "+String.valueOf(acc1.getFailCount()));
            }
        }, 3, 1, TimeUnit.SECONDS);

        List<Future<Boolean>> futureList = new ArrayList<>(10);
        for (int i=0; i<10; i++) {
            futureList.add(service.submit(
                    new Transfer(acc1, acc2, rnd.nextInt(350), i)
            ));
        }

    }

    static void transfer(Account acc1, Account acc2, int amount) throws InsufficientResourcesException {
        if (acc1.getBalance() < amount) throw new InsufficientResourcesException("Balance of account is too damn low!");
        try {
            if (acc1.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                try {
                    System.out.println("Get first object monitor by lock");
                    Thread.sleep(1000);
                    if (acc2.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                        try {
                            acc1.withdraw(amount);
                            System.out.println("Withdraw is ended");
                            acc2.deposit(amount);
                            System.out.println("Deposit ie ended");
                        } finally {
                            acc2.getLock().unlock();
                        }
                    }
                } finally {
                    acc1.getLock().unlock();
                }
            } else {
                System.out.println("Error waiting for unlock account");
            }
        } catch (InterruptedException interruptedException) {
            System.out.println("InterruptedException catched");
        }
    }

   /* static void transfer(Account acc1, Account acc2, int amount) throws InsufficientResourcesException {
        if (acc1.getBalance() < amount) throw new InsufficientResourcesException("Balance of account is too damn low!");
        try {
            synchronized (acc1) {
                System.out.println("Get first object monitor by synchronized");
                Thread.sleep(1000);
                synchronized (acc2) {
                    acc1.withdraw(amount);
                    System.out.println("Withdraw is ended");
                    acc2.deposit(amount);
                    System.out.println("Deposit ie ended");
                }
            }
        } catch (InterruptedException interruptedException) {
            System.out.println("InterruptedException catched");
        }
    }*/

}