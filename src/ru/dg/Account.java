package ru.dg;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by � on 05.12.2015.
 */
public class Account {
    private int balance;
    private ReentrantLock lock;

    public Account (int initialBalance) {
        this.balance = initialBalance;
        this.lock =  new ReentrantLock();
    }

    public void withdraw(int amount) {
        this.balance -= amount;
    }

    public void deposit (int amount) {
        this.balance += amount;
    }

    public int getBalance () {
        return this.balance;
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
