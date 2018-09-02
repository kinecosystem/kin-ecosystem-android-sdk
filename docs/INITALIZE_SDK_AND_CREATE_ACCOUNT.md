### Initialize the SDK and Creating a User’s Kin Account ###

If your app presents Kin Spend and Earn offers to your users, then each user needs a Kin wallet and account in order to take advantage of those offers. During initialization and before any other Kin sdk API calls, your app must call the SDK’s ```Kin.start(…)``` function while passing a unique ID for the current user. If that user already has a Kin account, the function only accesses the existing account. Otherwise, the function creates a new wallet and account for the user.

*To create or access a user’s Kin account:* 

Call ```Kin.start(…)```, passing the user’s unique ID and your chosen authentication credentials (either whitelist or JWT credentials).

**Whitelist mode:**
```java
whitelistData = new WhitelistData(<userID>, <appID>, <apiKey>); 
try {
   // As an example we are using PLAYGROUND environment
   Kin.start(getApplicationContext(), whitelistData, 
             Environment.getPlayground());
} 
catch (ClientException | BlockchainException e) {
   // Handle exception…   
}
```

**JWT mode:**

(See [Building the JWT Token](../README.md#generating-the-jwt-token) to learn how to build the JWT token.)

```java
try {
   // As an example we are using PRODUCTION environment
   Kin.start(getApplicationContext(), jwt, Environment.getProduction());
} 
catch (ClientException | BlockchainException e) {
   // Handle exception…
} 
```   