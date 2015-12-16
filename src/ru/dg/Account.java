package ru.dg;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ê on 05.12.2015.
 */
public class Account {
    private int balance;
    private ReentrantLock lock;
    private int failCount;

    public Account (int initialBalance) {
        this.balance = initialBalance;
        this.lock =  new ReentrantLock();
        failCount = 0;
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

    public void incFailCount() {
        failCount++;
    }
}
