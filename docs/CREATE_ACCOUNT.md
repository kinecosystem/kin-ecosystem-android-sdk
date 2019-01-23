### Creating a User’s Kin Account ###

If your app presents Kin Spend and Earn offers to your users, then each user needs a Kin wallet and account in order to take advantage of those offers.

>**NOTE:** Kin Ecosystem SDK must be initialized before any interaction with the SDK, in order to do that you should call `Kin.initialize(…)` first.


#### Login
*To create or access a user’s Kin account:*

Call `Kin.login(…)`, passing a JWT credentials and a `KinCallback` to get a response when the user is logged in, and then it is possible to send transactions, get information regarding the account balance, status etc.
If that user already has a Kin account, the function only accesses the existing account. Otherwise, the function creates a new wallet and account for the user.

**JWT mode:**

(See [Building the JWT Token](../README.md#generating-the-jwt-token) to learn how to build the JWT token.)

```java
    try {
        Kin.login(jwt, new KinCallback<Void>() {
            @Override
            public void onResponse(Void response) {
                Log.d(TAG, "JWT login succeed");
            }
    
            @Override
            public void onFailure(KinEcosystemException exception) {
                Log.e(TAG, "JWT login failed: " + exception.getMessage());
            }
        });
    } catch (BlockchainException e) {
        // Handle exception…
    }
```

#### Logout
*To release access from a user’s Kin account or switch account:*

Call `Kin.logout()`, this is a synchronous call means that you can call `Kin.login(…)` immediately (for switching between users).
After calling `logout`, you should login again in order to interact with the SDK API's, as Kin account is required.

```java
    try {
        Kin.logout();
        // the rest app logout logic
    } catch (ClientException e) {
        // Handle exception…
    }
```
