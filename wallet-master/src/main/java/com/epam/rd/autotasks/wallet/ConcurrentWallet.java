package com.epam.rd.autotasks.wallet;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.List;

public class ConcurrentWallet implements Wallet {
    private final List<Account> accounts;
    private final PaymentLog paymentLog;
    private final Lock lock;

    public ConcurrentWallet(List<Account> accounts, PaymentLog paymentLog) {
        this.accounts = accounts;
        this.paymentLog = paymentLog;
        this.lock = new ReentrantLock();
    }

    @Override
    public void pay(String recipient, long amount) throws ShortageOfMoneyException {
        lock.lock();
        try {
            Account selectedAccount = accounts.stream()
                    .filter(account -> account.balance() >= amount)
                    .findFirst()
                    .orElseThrow(() -> new ShortageOfMoneyException(recipient, amount));

            selectedAccount.pay(amount);
            paymentLog.add(selectedAccount, recipient, amount);

        } finally {
            lock.unlock();
        }
    }
}
