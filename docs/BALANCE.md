## Balance
We support 3 different way to get balance. You will get a `Balance` object containing amount as BigDecimal.
In case of no account was created you will get `Balance.getAmount() == 0`.

1. Get last known balance / cached balance
```java
    try {
        Balance cachedBalance = Kin.getCachedBalance();
    } catch (ClientException e) {
        e.printStackTrace();
    }
```

2. Get confirmed balance from the blockchain network using async call.
See [BlockchainException](../kin-ecosystem-sdk/src/main/java/com/kin/ecosystem/exception/BlockchainException.java) and [ServiceException](../kin-ecosystem-sdk/src/main/java/com/kin/ecosystem/exception/ServiceException.java) for possible errors.
```java
    Kin.getBalance(new KinCallback<Balance>() {
                    @Override
                    public void onResponse(Balance balance) {
                        // Got the balance from the network
                    }
    
                    @Override
                    public void onFailure(KinEcosystemException error) {
                        // Got an error from the blockchain network
                    }
                });
```

3. Add an observer to get a continues balance updates.<br>
Note: the observer first fire the last known balance, and open a live connection to the blockchain network.
It's the developer responsibility to remove this observer in order to close the live network connection.
```java

    // Add balance observer
    balanceObserver = new Observer<Balance>() {
                    @Override
                    public void onChanged(Balance value) {
                        showToast("Balance - " + value.getAmount().intValue());
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