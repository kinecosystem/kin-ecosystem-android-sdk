# Kin Ecosystem Android SDK #

## What is the Kin Ecosystem SDK? ##

The Kin Ecosystem SDK allows you to quickly and easily integrate with the Kin platform. This enables you to provide your users with new opportunities to earn and spend the Kin digital currency from inside your app or from the Kin Marketplace offer wall. For each user, the SDK will create wallet and an account on Kin blockchain. By calling the appropriate SDK functions, your application can performs earn and spend transactions. Your users can also view their account balance and their transaction history.

## Playground and Production Environments ##

The Kin Ecosystem provides two working environments:

- **Beta** – a staging and testing environment using test servers and a blockchain test network.
- **Production** – uses production servers and the main blockchain network.

Use the Playground environment to develop, integrate and test your app. Transition to the Production environment when you’re ready to go live with your Kin-integrated app.

Add environment meta data to you manifest in application level, specifying which one you want to work with.
For Beta: ("beta")
```xml
<application>

    <meta-data android:name="com.kin.ecosystem.sdk.EnvironmentName" android:value="@string/kinecosystem_environment_beta"/>
    
</application>
```
And for Production use: `@string/kinecosystem_environment_production` as value. 


>**NOTES:**
>* When working with the Playground environment, you can only register up to 1000 users. An attempt to register additional users will result in an error.
>* In order to switch between environments, you’ll need to clear the application cache.

## Initialize The SDK ##
Kin Ecosystem SDK must be initialized before any interaction with the SDK, in order to do that you should call ```Kin.initialize(getApplicationContext())``` first.


   >**NOTE** `initialize` method does not perform any network calls and it's a synchronous method, so you will get an exception if something went wrong.

## Obtaining Authentication Credentials ##

To access the Kin Ecosystem, you’ll need to obtain authentication credentials, which you then use to register your users.

There are 2 types of authentication:

* **Whitelist authentication** – to be used for a quick first-time integration or sanity test. The authentication credentials are provided as simple appID and apiKey values. (For development and testing, you can use the default values provided in the Sample App.)

    >**NOTE:** You can only use whitelist authentication for the Playground environment. The Production environment requires that you use JWT authentication.
* **JWT authentication** – a secure authentication method to be used in production. This method uses a JSON Web Token (JWT) signed by the Kin Server to authenticate the client request. You provide the Kin team with one or more public signature keys and its corresponding keyID, and you receive a JWT issuer identifier (ISS key). (See [https://jwt.io](https://jwt.io) to learn more about JWT tokens.)

For both types of authentication, you supply your credentials when calling the SDK’s ```Kin.login(…)``` function for a specific user. See [Creating a User’s Kin Account](docs/CREATE_ACCOUNT.md) to learn how.

## Generating the JWT Token ##

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
```groovy
 repositories {
     ...
     maven {
         url 'https://jitpack.io'
     }
 }
```
2.	Add the following lines to the app module's ```build.gradle``` file.
```groovy
 dependencies {
     ...
     implementation 'com.github.kinfoundation.kin-ecosystem-android-sdk:sdk:0.3.1'

 }
```
>**NOTE:** The kin-ecosystem-android-sdk arr is tested for Android OS version 4.4 (API level 19) and above. 
>* Some functionalities such as observing balance update will not be supported for lower OS version.
>* If your app support lower OS versions (minSdkVersion < 19) we recommend to only enable Kin integration for users with version 4.4 and above.

## Primary APIs ##

The following sections show how to implement some primary APIs using the Kin Ecosystem SDK.

* [Creating a User’s Kin Account](docs/CREATE_ACCOUNT.md)
  
* [Getting an Account’s Balance](docs/BALANCE.md)

* [Requesting Payment for a Custom Earn Offer](docs/NATIVE_EARN.md)

* [Creating a Custom Spend Offer](docs/NATIVE_SPEND.md)

* [Creating a Pay To User Offer](docs/PEER_TO_PEER.md)

* [Displaying the Kin Marketplace Offer Wall](docs/DISPLAY_EXPERIENCE.md)

* [Adding Native Offer to Marketplace Offer Wall](docs/ADD_NATIVE_OFFER_TO_MARKETPLACE.md)

* [Requesting an Order Confirmation](docs/ORDER_CONFIRMATION.md)

* [Misc](docs/MISC.md)


## Common Errors ##
The Ecosystem APIs can response with few types of error, [learn more here](docs/COMMON_ERRORS.md)

## License ##

The ```kin-ecosystem-android-sdk``` library is licensed under the MIT license.

