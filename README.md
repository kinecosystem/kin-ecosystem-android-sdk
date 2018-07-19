# Kin Ecosystem Android SDK #

## What is the Kin Ecosystem SDK? ##

The Kin Ecosystem SDK allows you to quickly and easily integrate with the Kin platform. This enables you to provide your users with new opportunities to earn and spend the Kin digital currency from inside your app or from the Kin Marketplace offer wall. For each user, the SDK will create wallet and an account on Kin blockchain. By calling the appropriate SDK functions, your application can performs earn and spend transactions. Your users can also view their account balance and their transaction history.

## Playground and Production Environments ##

The Kin Ecosystem provides two working environments:

- **Playground** – a staging and testing environment using test servers and a blockchain test network.
- **Production** – uses production servers and the main blockchain network.

Use the Playground environment to develop, integrate and test your app. Transition to the Production environment when you’re ready to go live with your Kin-integrated app.

When your app calls ```Kin.start(…)```, you specify which environment to work with.

>**NOTES:**
>* When working with the Playground environment, you can only register up to 1000 users. An attempt to register additional users will result in an error.
>* In order to switch between environments, you’ll need to clear the application cache.

## Obtaining Authentication Credentials ##

To access the Kin Ecosystem, you’ll need to obtain authentication credentials, which you then use to register your users.

There are 2 types of authentication:

* **Whitelist authentication** – to be used for a quick first-time integration or sanity test. The authentication credentials are provided as simple appID and apiKey values. (For development and testing, you can use the default values provided in the Sample App.)

    >**NOTE:** You can only use whitelist authentication for the Playground environment. The Production environment requires that you use JWT authentication.
* **JWT authentication** – a secure authentication method to be used in production. This method uses a JSON Web Token (JWT) signed by the Kin Server to authenticate the client request. You provide the Kin team with one or more public signature keys and its corresponding keyID, and you receive a JWT issuer identifier (ISS key). (See [https://jwt.io](https://jwt.io) to learn more about JWT tokens.)

For both types of authentication, you supply your credentials when calling the SDK’s ```Kin.start(…)``` function for a specific user. See [Creating or Accessing a User’s Kin Account](#CreateAccount) to learn how.

<a name="BuildJWT"></a>
## Building the JWT Token ##

A JWT token is a string that is composed of 3 parts:

* **Header** – a JSON structure encoded in Base64Url
* **Payload** – a JSON structure encoded in Base64Url
* **Signature** – constructed with this formula: 

    ```ES256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)```
   
    -- where the secret value is the private key of your agreed-on public/private key pair.

The 3 parts are then concatenated, with the ‘.’ character between each 2 consecutive parts, as follows:

```<header> + “.” + <payload> + “.” + <signature>```

See https://jwt.io to learn more about how to build a JWT token, and to find libraries that you can use to do this.

This is the header structure:

```
{
    "alg": "ES256",
    "typ": "JWT",
    "kid": string" // ID of the keypair that was used to sign the JWT. 
    // IDs and public keys will be provided by the signing authority. 
    // This enables using multiple private/public key pairs. 
    // (The signing authority must provide the verifier with a list of public 
    // keys and their IDs in advance.)
}
```

This is the payload structure:

```
{
    // standard fields
    iat: number;  // the time this token was issued, in seconds from Epoch
    iss: string;  // issuer (Kin will provide this value)
    exp: number;  // the time until this token expires, in seconds from Epoch 
    sub: "register"

    // application fields
    user_id: string; // A unique ID of the end user (must only be unique among your app’s users; not globally unique)
}
```

## Setting Up the Sample App ##

The Kin Ecosystem SDK Sample App demonstrates how to perform common workflows such as creating a user account and creating Spend and Earn offers. You can build the Sample App from the ```app``` module in the Kin Ecosystem SDK Git repository. We recommend building and running the Sample App as a good way to get started with the Kin Ecosystem SDK and familiarize yourself with its functions.

>**NOTE:** The Sample App is for demonstration only, and should not be used for any other purpose.

The Sample App is pre-configured with the default whitelist credentials ```appId='test'``` and 
```apiKey='AyINT44OAKagkSav2vzMz'```. These credentials can be used for integration testing in any app, but authorization will fail if you attempt to use them in a production environment.

You can also request unique apiKey and appId values from Kin, and override the default settings, working either in whitelist or JWT authentication mode.

*To override the default credential settings:* 

Create or edit a local ```credential.properties``` file in the ```app``` module directory and add the lines below, using the ```appId``` and ```apiKey``` values you received.

```
APP_ID="YOUR_APP_ID" // For whitelist registration, and also as the issuer (iss). Default = 'test'.

API_KEY="YOUR_API_KEY" // For whitelist registration. Default = 'AyINT44OAKagkSav2vzMz'.

ES256_PRIVATE_KEY="YOUR_ES256_PRIVATE_KEY” // Optional. Only required when testing JWT on the sample app. For production, JWT is created by server side with ES256 signature.

IS_JWT_REGISTRATION = false // Optional. To test sample app JWT registration, set this property to true. If not specified, default=false.
```

The Sample App Gradle build loads the ```credential.properties``` setting and uses it to create the ```SignInData``` object used for registration.

## Integrating with the Kin SDK ##

*To integrate your project with the Kin Android SDK:*


1. Add the following lines to your project module's ```build.gradle``` file.
```
 repositories {
     ...
     maven {
         url 'https://jitpack.io'
     }
 }
```
2.	Add the following lines to the app module's ```build.gradle``` file.
```
 dependencies {
     ...
     implementation 'com.github.kinfoundation:kin-ecosystem-android-sdk:0.0.11

 }
```

## Common Workflows ##

The following sections show how to implement some common workflows using the Kin Ecosystem SDK.

<a name="CreateAccount"></a>
### Creating or Accessing a User’s Kin Account ###

If your app presents Kin Spend and Earn offers to your users, then each user needs a Kin wallet and account in order to take advantage of those offers. During initialization and before any other Kin sdk API calls, your app must call the SDK’s ```Kin.start(…)``` function while passing a unique ID for the current user. If that user already has a Kin account, the function only accesses the existing account. Otherwise, the function creates a new wallet and account for the user.

*To create or access a user’s Kin account:* 

Call ```Kin.start(…)```, passing the user’s unique ID and your chosen authentication credentials (either whitelist or JWT credentials).

**Whitelist mode:**
```
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

(See [Building the JWT Token](#BuildJWT) to learn how to build the JWT token.)

```
try {
   // As an example we are using PRODUCTION environment
   Kin.start(getApplicationContext(), jwt, Environment.getProduction());
} 
catch (ClientException | BlockchainException e) {
   // Handle exception…
} 
```   
 
### Getting an Account’s Balance ###

A user’s balance is the number of Kin units in his or her account (can also contain a fraction). You may want to retrieve the balance in response to a user request or to check whether a user has enough funding to perform a Spend request. When you request a user’s balance, you receive a ```Balance``` object in response, which contains the balance as a decimal-point number.

>**NOTE:** If no account was found for the user, you will receive a balance of 0 for that user.

There are 3 ways you can retrieve the user’s balance:

* Get the cached balance (the last balance that was received on the client side). The cached balance is updated upon SDK initialization and for every transation. Usually, this will be the same balance as the one stored in the Kin blockchain. But in some situations it might not be current, for instance due to network connection issues.
* Get the balance from the Kin Server (the balance stored in the Kin blockchain). This is the definitive balance value. This is an asynchronous call that requires you to implement callback functions.
* Create an ```Observer``` object that receives notifications when the user’s balance changes.

*To get the cached balance:*

Call ```Kin.getCachedBalance()```.

```
try {
        Balance cachedBalance = Kin.getCachedBalance();
    } catch (ClientException e) {
        e.printStackTrace();
}
```

*To get the balance from the Kin Server (from the blockchain):*

Call ```Kin.getBalance(…)```, and implement the 2 response callback functions.
(See [BlockchainException](ADDLINK) and [ServiceException](ADDLINK) for possible errors.)

```
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

Create an ```Observer``` object and implements its ```onChanged()``` function.

>**NOTES:** 
>* The ```Observer``` object sends a first update with the last known balance, and then opens a connection to the blockchain network to receive subsequent live updates. 
>* When performing cleanup upon app exit, don’t forget to remove the Observer object in order to close the network connection.

```
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

<a name="CreateCustomSpendOffer"></a>
### Creating a Custom Spend Offer ###

A custom Spend offer allows your users to unlock unique spend opportunities that you define within your app. (Custom offers are created by your app, as opposed to [built-in offers displayed in the Kin Marketplace offer wall](#AddingToMP).) Your app displays the offer, request user approval, and then [requests payment using the Kin purcash API](#purcasheRequest).

*To create a custom Spend offer:*


<a name="purcasheRequest"></a>
### Requesting purchase Payment for a Custom Spend Offer ###

*To request payment for a custom Spend offer:*

1.	Create a JWT that represents a Spend offer signed by you, using the header and payload templates below. (See [Building the JWT Token](#BuildJWT) for more details about JWT structure).

    **JWT header:**
```
   {
        "alg": "ES256", // Hash function
        "typ": "JWT",
        "kid": string" // identifier of the keypair that was used to sign the JWT. identifiers and public keys will be provided by signer authority. This enables using multiple private/public key pairs (a list of public keys and their ids need to be provided by signer authority to verifier in advanced)
    }
```

    **JWT payload:**
```
    {
        // common/ standard fields
        iat: number;  // issued at - seconds from epoc
        iss: string; // issuer 
        exp: number; // expiration
        sub: "spend"
        
       // application fields
       offer: {
               id: string; // offer id is decided by you (internal)
               amount: number; // amount of kin for this offer - price
       }
        
       sender: {
              user_id: string; // optional: ID of purchasing user
              title: string; // order title - appears in order history
              description: string; // order desc. (in order history)
       }
    }
```
2.	Call ```Kin.purchase(…)```, while passing the JWT you built and a callback function that will receive purchase confirmation.

    >**NOTES:** 
    >* The following snippet is taken from the SDK Sample App, in which the JWT is created and signed by the Android client side for presentation purposes only. Do not use this method in production! In production, the JWT must be signed by the server, with a secure private key. 
    >* See [BlockchainException](ADDLINK) and [ServiceException](ADDLINK) for possible errors.

    ```
    try {
      Kin.purchase(offerJwt, new KinCallback<OrderConfirmation>() {
      @Override public void onResponse(OrderConfirmation orderConfirmation) {
      // OrderConfirmation will be called once Ecosystem received the payment transaction from user.
      // OrderConfirmation can be kept on digital service side as a receipt proving user received his Kin.
                    
                    // Send confirmation JWT back to the server in order prove that the user
                    // completed the blockchain transaction and purchase can be unlocked for this user.
                    System.out.println("Succeed to create native spend.\n jwtConfirmation: " + orderConfirmation.getJwtConfirmation());
                }
    
                @Override
                public void onFailure(KinEcosystemException exception) {
                    System.out.println("Failed - " + error.getMessage());
                }
            });
        } catch (ClientException e) {
            e.printStackTrace();
      }
    ```

3.	Complete the purchase after you receive confirmation from the Kin Server that the funds were transferred successfully.

<a name="AddingToMP"></a>
### Adding a Custom Spend Offer to the Kin Marketplace Offer Wall ###

The Kin Marketplace offer wall displays built-in offers, which are served by the Kin Ecosystem Server. Their purpose is to provide users with opportunities to earn initial Kin funding, which they can later spend on spend offers provided by hosting apps.

You can also choose to display a banner for your custom offer in the Kin Marketplace offer wall. This serves as additional "real estate" in which to let the user know about custom offers within your app. When the user clicks on your custom Spend offer in the Kin Marketplace, your app is notified, and then it continues to manage the offer activity in its own UX flow.

>**NOTE:** You will need to actively launch the Kin Marketplace offer wall so your user can see the offers you added to it. See [Displaying the Kin Marketplace Offer Wall](#DisplayMPWindow) for more details.

*To add a custom Spend offer to the Kin Marketplace:*

1. Create a ```NativeSpendOffer``` object as in the example below.

```
NativeSpendOffer nativeOffer =
        new NativeSpendOffer("The offerID") // OfferId must be a UUID
            .title("Offer Title") // Title to display with offer
            .description("Offer Description") // Desc. to display with offer
            .amount(1000) // Purchase amount in Kin
            .image("Image URL"); // Image to display with offer
```
2.	Create a ```NativeOfferObserver``` object to be notified when the user clicks on your offer in the Kin Marketplace.

    >**NOTE:** You can remove the Observer by calling ```Kin.removeNativeOfferClickedObserver(…)```.

    ```
    private void addNativeOfferClickedObserver() {
    try {
                 
    Kin.addNativeOfferClickedObserver(getNativeOfferClickedObserver());
            } catch (TaskFailedException e) {
                showToast("Could not add native offer callback");
            }
        }
    
        private Observer<NativeSpendOffer> getNativeOfferClickedObserver() {
            if (nativeSpendOfferClickedObserver == null) {
                nativeSpendOfferClickedObserver = new Observer<NativeSpendOffer>() {
                    @Override
                    public void onChanged(NativeSpendOffer value) {
                        Intent nativeOfferIntent = NativeOfferActivity.createIntent(MainActivity.this, value.getTitle());
                        startActivity(nativeOfferIntent);
                    }
                };
            }
            return nativeSpendOfferClickedObserver;
    }
    ```

3.	```Call Kin.addNativeOffer(…)```. 

    >**NOTE:** Each new offer is added as the first offer in Spend Offers list the Marketplace displays.

    ```
    try {
        if (Kin.addNativeOffer(nativeSpendOffer)) {
            // Native offer added
        } else {
            // Native offer already in Kin Marketplace
        }
    } catch (ClientException error) {
        ...
    }
    ```

### Removing a Custom Spend Offer from Kin Marketplace ###

*To remove a custom Spend offer from the Kin Marketplace:*

Call ```Kin.removeNativeOffer(…)```, passing the offer you want to remove.

```
try {
        if (Kin.removeNativeOffer(nativeSpendOffer)) {
             // Native offer removed
        } else {
            // Native offer isn't in Kin Marketplace
        }
    } catch (ClientException e) {
        ...
}
```

<a name="DisplayMPWindow"></a>
### Displaying the Kin Marketplace Offer Wall ###

Optionally, your app can launch the Kin Marketplace offer wall. It displays Earn and Spend offers, which can be added to it by your app or by the Kin Ecosystem Server. When a user selects one of these offers, the Kin Marketplace notifies the app that created the offer. The app can then launch the Earn or Spend activity for the user to complete. 

You may choose to add your custom Earn and Spend offers to the Kin Marketplace so that there is a convenient, visible place where the user can access all offers. Some offers displayed in-app might require that the user choose to navigate to a specific page, and therefore might not be so readily visible.

>**NOTE:** The launchMarketplace function is not a one-time initialization function; you must call it each time you want to display the Kin Marketplace offer wall.

*To display the Kin Marketplace offer wall:*

Call ```Kin.launchMarketplace(…)```.

```
try {
         Kin.launchMarketplace(MainActivity.this);
          System.out.println("Public address : " + Kin.getPublicAddress());
          } catch (ClientException e) {
            //
      }
```

### Requesting an Order Confirmation ###

In the normal flow of a transaction, you will receive an order confirmation from the Kin Server through the purchae API's callback function. This indicates that the transaction was completed. But if you missed this notification for any reason, for example, because the user closed the app before it arrived, or the app closed due to some error, you can request confirmation for an order according to its ID.

*To request an order confirmation:*

Call ```Kin.getOrderConfirmation(…)```, while passing the order’s ID and implementing the appropriate callback functions. (See [ServiceException](ADDLINK) for possible errors.)

```
try {
        Kin.getOrderConfirmation("your_offer_id", new KinCallback<OrderConfirmation>() {
                @Override
                public void onResponse(OrderConfirmation orderConfirmation) {
                    if(orderConfirmation.getStatus() == Status.COMPLETED ){
                       String jwtConfirmation = orderConfirmation.getJwtConfirmation()
                    }
                }
    
                @Override
                public void onFailure(KinEcosystemException exception) {
                    ...
                }
        });
    } catch (ClientException exception) {
	    ...
    }
```

### Requesting Payment for a Custom Earn Offer ###

A custom Earn offer allows your users to earn Kin as a reward for performing tasks you want to incentivize, such as setting a profile picture or rating your app. (Custom offers are created by your app, as opposed to offers created by other platforms such as the Kin Ecosystem Server.)

>**NOTE:** For now, custom Earn offers must be displayed and managed by your app, and cannot be added to the Kin Marketplace (unlike custom Spend offers).

Once the user has completed the task associated with the Earn offer, you request Kin payment for the user.

*To request payment for a user who has completed an Earn offer:*

1.	Create a JWT that represents an Earn offer signed by you, using the header and payload templates below. (See [Building the JWT Token](#BuildJWT) for more details about JWT structure).

    **JWT header:**
    ```
    {
        "alg": "ES256", // Hash function
        "typ": "JWT",
        "kid": string" // identifier of the keypair that was used to sign the JWT. identifiers and public keys will be provided by signer authority. This enables using multiple private/public key pairs (a list of public keys and their ids need to be provided by signer authority to verifier in advanced)
    }
    ```

    **JWT payload:**
    ```
    {
        // common/ standard fields
        iat: number; // issued at - seconds from Epoch
        iss: string; // issuer
        exp: number; // expiration
        sub: "earn"
        
       // application fields
       offer: {
               id: string; // offer id is decided by you (internal)
               amount: number; // amount of kin for this offer - price
       }
       recipient: {
              user_id: string; // user_id who will perform the order
              title: string; // order title - appears in order history
              description: string; // order desc. (in order history)
       }
    }
    ```
2.	Call ```Kin.requestPayment``` (see code example below). The Ecosystem Server credits the user account (assuming the app’s account has sufficient funds).

    >**NOTES:** 
    >* The following snippet is taken from the SDK Sample App, in which the JWT is created and signed by the Android client side for presentation purposes only. Do not use this method in production! In production, the JWT must be signed by the server, with a secure private key. 
    >* See [BlockchainException](ADDLINK) and [ServiceException](ADDLINK) for possible errors.

    ```
    try {
        Kin.requestPayment(offerJwt, new KinCallback<OrderConfirmation>() {
            @Override
            public void onResponse(OrderConfirmation orderConfirmation) {
                // OrderConfirmation will be called once Ecosystem payment transaction to user completed successfully.
                // OrderConfirmation can be kept on digital service side as a receipt proving user received his Kin.
                System.out.println("Succeed to create native earn.\n jwtConfirmation: " + orderConfirmation.getJwtConfirmation());
            }

            @Override
            public void onFailure(KinEcosystemException exception) {
                System.out.println("Failed - " + exception.getMessage());
            }
        });
    } 
    catch (ClientException exception) {
        exception.printStackTrace();
    }
    ```

## License ##

The ```kin-ecosystem-android-sdk``` library is licensed under the MIT license.

