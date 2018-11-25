### Initialize the SDK and Creating a User’s Kin Account ###

If your app presents Kin Spend and Earn offers to your users, then each user needs a Kin wallet and account in order to take advantage of those offers. During initialization and before any other Kin sdk API calls, your app must call the SDK’s `Kin.start(…)` function while passing a unique ID for the current user. If that user already has a Kin account, the function only accesses the existing account. Otherwise, the function creates a new wallet and account for the user.

*To create or access a user’s Kin account:* 

Call `Kin.login(…)`, passing the user’s unique ID and your chosen authentication credentials (either whitelist or JWT credentials).
You can add a `KinCallback` and get a response when the user is logged in, and you can start send transactions and get information regarding the account balance, status etc.

**Whitelist mode:**
```java
    try {
        Kin.login(whitelistData, new KinCallback<Void>() {
            @Override
            public void onResponse(Void response) {
                Log.d(TAG, "WhiteList login succeed");
            }
    
            @Override
            public void onFailure(KinEcosystemException exception) {
                Log.e(TAG, "WhiteList login failed: " + exception.getMessage());
            }
        });
    } catch (BlockchainException e) {
        // Handle exception…
    }
```

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