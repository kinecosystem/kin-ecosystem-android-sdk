### Getting an Account’s Balance ###

A user’s balance is the number of Kin units in his or her account (can also contain a fraction). You may want to retrieve the balance in response to a user request or to check whether a user has enough funding to perform a Spend request. When you request a user’s balance, you receive a `Balance` object in response, which contains the amount as a `BigDecimal` object.

>**NOTE:** If no account was found for the user, you will receive a balance of 0 for that user.

There are 3 ways you can retrieve the user’s balance:

* Get the cached balance (the last balance that was received on the client side). The cached balance is updated upon SDK initialization and for every transaction. Usually, this will be the same balance as the one stored in the Kin blockchain. But in some situations it might not be up to date, for instance due to network connection issues.
* Get the balance from the Kin Server (the balance stored in the Kin blockchain). This is the definitive balance value. This is an asynchronous call that requires you to implement callback functions.
* Create an `Observer` object that receives notifications when the user’s balance changes.

*To get the cached balance:*

Call `Kin.getCachedBalance()`.

```java
try {
        Balance cachedBalance = Kin.getCachedBalance();
    } catch (ClientException e) {
        e.printStackTrace();
}
```

*To get the balance from the Kin Server (from the blockchain):*

Call `Kin.getBalance(…)`, and implement the 2 response callback functions.
(See [BlockchainException](COMMON_ERRORS.md#blockchainException--Represents-an-error-originated-with-kin-blockchain-error-code-might-be) and [ServiceException](COMMON_ERRORS.md#serviceexception---represents-an-error-communicating-with-kin-server-error-code-might-be) for possible errors.)

```java
Kin.getBalance(new KinCallback<Balance>() {
                    @Override
                    public void onResponse(Balance balance) {
                        // Got the balance from the network
                    }

                    @Override
                    public void onFailure(KinEcosystemException exception) {
                        // Got an error from the blockchain network
                    }
    });
```

*To listen continuously for balance updates:*

Create an `Observer` object and implements its `onChanged()` function.

>**NOTES:**
>* The `Observer` object sends a first update with the last known balance, and then opens a connection to the blockchain network to receive subsequent live updates.
>* Make sure to add balance observer only when required (for example when app UI need to show updated balance) and remove the observer as soon as possible to avoid keeping open network connection.

```java
// Add balance observer
balanceObserver = new Observer<Balance>() {
                @Override
                public void onChanged(Balance value) {
                    showToast("Balance - " +
                               value.getAmount().intValue());
                }
            };

try {
    Kin.addBalanceObserver(balanceObserver);
} catch (TaskFailedException e) {
    e.printStackTrace();
}

// Remove the balance observer
try {
    Kin.removeBalanceObserver(balanceObserver);
} catch (TaskFailedException e) {
    e.printStackTrace();
}
```
