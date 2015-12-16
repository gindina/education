package ru.dg;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.naming.InsufficientResourcesException;

/**
 * Created by � on 12.12.2015.
 */
public class Transfer implements Callable<Boolean> {
  private Account acc1;
  private Account acc2;
  final static long WAIT_SEC = 3;
  private int transferId;
  private int amount;

  public Transfer(Account acc1, Account acc2, int amount, int transferId) {
    this.acc1 = acc1;
    this.acc2 = acc2;
    this.amount = amount;
    this.transferId = transferId;
  }

  public Boolean call() throws Exception {
    printTransferMessage("-----start call method-----");
    if (acc1.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
      printTransferMessage("get acc1 lock");
      try {
        if (acc1.getBalance() < amount) {
          printTransferMessage("acc1 balance is lower than transfer amount");
          throw new InsufficientResourcesException();
        }
        if (acc2.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
          try {
            acc1.withdraw(amount);
            printTransferMessage("sucessfully withdraw amount from acc1");
            acc2.deposit(amount);
            printTransferMessage("~~~~~~sucessfully deposit amount to acc2~~~~");
            Thread.sleep(5000);
            return true;
          }
          finally {
            printTransferMessage("release acc2 lock");
            acc2.getLock().unlock();
          }
        }  else {
          acc2.incFailCount();
          printTransferMessage("couldn't get acc2 lock");
          return false;
        }

      }
      finally {
        printTransferMessage("release acc1 lock");
        acc1.getLock().unlock();
      }

    } else {
      acc1.incFailCount();
      printTransferMessage("couldn't get acc1 lock");
      return false;
    }
  }

   public void printTransferMessage(String meassage) {
     System.out.println( String.valueOf(transferId) + ": " + meassage);
   }

}
