# kin-ecosystem-android-sdk

## Disclaimer
The [Sample App](app/) under module `app` is for presentation purposes only - it should not be used for any other purpose.<br/>
The backend service supporting the ecosystem sdk is in test mode and SLA is not guarantee.<br/>
All blockchain transactions are currently running on Stellar test net and not on main net.<br/>



## Intro
The ecosystem "5 minute SDK" supports rich user experience and seamless blockchain integration. <br/>

Once the ecosystem SDK is integrated within a digital service, you’ll have the ability to provide your users with a range of new experiences as part of the Kin ecosystem including, earn and spend marketplace opportunities, and the ability to view their account balance and order history<br/>
A stellar wallet and account will be created behind the scene for the user. <br/>

![DEMO](https://user-images.githubusercontent.com/3635216/38100813-0f2c7bc2-3387-11e8-930d-03175842e81e.gif)

## Registration for Ecosystem backend service

Digital service application needs to initiate the Ecosystem sdk, which interacts with the ecosystem backend service. <br/>
<br/>
The Ecosystem backend is required for
1. Creating users accounts on the Stellar blockchain
1. Funds these account with initial XLM balance.
1. Serving KIN earn and spend offers for the SDK marketplace.
1. Mange and store user's earn and spend order history.

Therefore the ecosystem backend will block unauthorized requests.
Digital services will have to authorised client request using one of the following methods:
1. "whitelist" registration - used for quick first time integration or small internal testing. 
    1. Whitelist registration requires a unique appID and apiKey.
    1. Please contact us to receive your unique appId and apiKey.
1. "JWT" registration - A secure register method for production ready application,
    1. "JWT" registration" use a Server side signed JWT token to authenticated client request.
    1. You can learn more [here](https://jwt.io)
    1. Please contact us to receive your JWT issuer identifier (iss key) and provide us with your public signature key and its corresponding 'keyid'
### JWT Registration specs
1. We will support `ES256` signature algorithm later on, right now you should use `RS512`.
2. The header will follow this template
    ```aidl
    {
        "alg": "RS512", // We will support ES256 signature algorithem 
        "typ": "JWT",
        "kid": string" // identifier of the keypair that was used to sign the JWT. identifiers and public keys will be provided by signer authority. This enables using multiple private/public key pairs (a list of public keys and their ids need to be provided by signer authority to verifier in advanced)
    }
    ```

3. Here is the registration payload template
    ```aidl
    {
        // common/ standard fields
        iat: number;  // issued at - seconds from epoc
        iss: string; // issuer - please contact us to recive your issuer
        exp: number; // expiration
        sub: "register"
    
        // application fields
        user_id: string; // id of the user - or a deterministic unique id for the user (hash)
    }
    ```

## Setup sample app
 
The Sample app is configured with default appId = 'test' and apiKey = 'A2XEJTdN8hGiuUvg9VSHZ'.<br/>
These appID and apiKey can be used for integration testing in any app but will not run on real production environment.<br/>
To setup and run the sample app an authorized apiKey and appId needs to be provided.<br/>
Contact us to receive your unique appId and apiKey.<br/>
 
To override the default setting, create or edit a local `credential.properties` in the `app` module directory. <br/>
Add the lines below to your local `credential.properties` file of the sample app once you receive your appId and apiKey.<br/>
```
   APP_ID="YOUR_APP_ID" // will be used for Whitelisted registartion and also as the issuer (iss). Set by defualt to 'test' 
   API_KEY="YOUR_API_KEY" // only requreid for whitelist registrartion. Set by default to 'A2XEJTdN8hGiuUvg9VSHZ'.
   RS512_PRIVATE_KEY="YOUR_RS512_PRIVATE_KEY // (optional) only required when testing JWT on sample app in real use case JWT is created by server side with ES256 signature
   IS_JWT_REGISTRATION = false// (optional)to test sample app JWT registartion set this property to true, if not specified defualt is set to false 
   
```
The sample app Gradle build loads `credential.properties` setting and uses it to create the 'SignInData' object used for the registration.



## Integrating 5 minutes SDK within digital service
As can be seen in the sample app, there are just few step required to integrate the SDK.

1. Add this to your project module's `build.gradle` file.
      ```gradle
       repositories {
           ...
           maven {
               url 'https://jitpack.io'
           }
       }
   ```
1. Add this to the app module's `build.gradle` file.
      ```gradle
       dependencies {
           ...
           implementation 'com.github.kinfoundation:kin-ecosystem-android-sdk:0.0.6

       }
    
       ```
1. Create WhitelistData object and set userID, appID, apiKey or just pass a registration JWT String on `Kin.start`.

    1. Option 1 - Should be use only for first time rapid integration and internal testing.
    
          ```java
               whitelistData = new WhitelistData("userID", "appID", "apiKey");           
         ```
    1. Option 2 - recommended integration using JWT token signed by digital service server side.
          ```java
               try {
                   Kin.start(getApplicationContext(), jwt);
               } catch (InitializeException e) {
                   //
               }
          ```
         JWT spec can be found at [ecosystem-api repository](https://github.com/kinfoundation/ecosystem-api)
   
1. Initiate the SDK when the application starts calling Kin. The first start will begin the blocakchain wallet and account creation process.
      ```java
               try {
                   Kin.start(getApplicationContext(), whitelistData);
               } catch (InitializeException e) {
                   //
               }
      ```
1. Launch the marketplace experience.
      ```java
           try {
               Kin.launchMarketplace(MainActivity.this);
                System.out.println("Public address : " + Kin.getPublicAddress());
                } catch (TaskFailedException e) {
                  //
            }
      ```
## Setup Environment
By default, Kin Ecosystem provides two working environments:
1. PRODUCTION - Production ecosystem servers and the main private blockchain network.
2. PLAYGROUND - A staging and testing environment running on test ecosystem servers and a private blockchain test network.<br>

> Default environment is PLAYGROUND. If you wish to run on a different environment (e.g, before deploying your app), you should call `Kin.setEnvironment(KinEnvironment)` before calling `Kin.start(...)`.
      
## [Balance](docs/BALANCE.md)
The balance API lets you get the last known balance (cached balance), blockchain confirmed balance and observe on balance updates.[(see more)](docs/BALANCE.md) 

## [Create Native Spend Offer](docs/NATIVE_SPEND.md)
A native spend is a mechanism allowing your users to buy virtual goods you define, using Kin on Kin Ecosystem API's. [(see more)](docs/NATIVE_SPEND.md) 

## [Create Native EARN Offer](docs/NATIVE_EARN.md)
A native earn is a mechanism allowing your users to earn Kin as a reward for native tasks you define, such as setting a profile picture or adding info. [(see more)](docs/NATIVE_EARN.md) 
   
## License
The kin-ecosystem-android-sdk library is licensed under [MIT license](LICENSE.md).
