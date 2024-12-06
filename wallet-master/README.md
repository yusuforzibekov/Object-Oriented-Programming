# Concurrent Wallet
The purpose of this exercise is to give you some practice using advanced synchronizers to organize interactions between several threads to access a common resource.  

Duration: _1 hour 30 minutes_.  



## Description
In this task, you will create a wallet allows you to use multiple accounts and process payments using one of a list of available accounts.  

You are given a description of the following classes and interfaces:  
* The class `ShortageOfMoneyException` determines what exception is thrown when there is not enough money in the wallet and the payment fails to register.    

* The interface `Account` represents a named account with balance-checking and payment features and provides a lock method, which might be convenient in a concurrent environment.  

* The interface `PaymentLog` defines the method for logging successful payments.  

* The interface `Wallet` provides an interface for aggregating multiple accounts and a payment log. The wallet's job is to find an account with enough money to register a payment using the payment log.   

* The class `WalletFactory` provides only one method —`wallet(List<Account>, PaymentLog)`, which creates a new instance of the `Wallet` type and passes the list of accounts to it as well as the payment log.  

_Note_: You don't need to implement the `Account` and `PaymentLog` interfaces. They are presented just to provide an API. However, you may want to implement them during local testing. And the `ShortageOfMoneyException` class does not require any changes.  

First, proceed to the `Wallet` interface, explore its content, and describe the class that implements this interface. You only need to implement one `pay(String, long)` method. This method must find an account whose balance exceeds the requested amount, decrease the account balance by a specific amount, and log this operation by sending information about the recipient and the amount to the payment log. If no account can handle the payment, the  `ShortageOfMoneyException` should be thrown, including information about the recipient and the requested amount. Any other exception will be considered invalid. Also, be aware that this method must work correctly in a multithreaded environment because it will be called from multiple threads simultaneously.

Then, proceed to the `WalletFactory` class and change its `wallet()` method to return your implementation of the `Wallet` interface.

### Details
* The `ShortageOfMoneyException` must contain valid information about the payment, such as the recipient and the amount of the payment.

* By default, the accounts do not support overdrafts (their balances are always non-negative). A balance cannot be increased after it is created. An account's name and lock do not change after they are created.

* Be aware that the `pay()` method of the `Wallet` interface will be executed in a multithreaded environment. Therefore, you must use advanced synchronizers such as locks, semaphores, barriers, etc. 

