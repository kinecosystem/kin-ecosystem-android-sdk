# kin-ecosystem-android-sdk

## Disclaimer
The [Sample App](app/) under module `app` is for presentation purposes only - it should not be used for any other purpose.<br/>
The backend service supporting the ecosystem sdk is in test mode and SLA is not guarantee.<br/>
All blockchain transactions are currently running on Stellar test net and not on main net.<br/>



## Intro
The ecosystem "5 minute SDK" supports rich user experience and seamless blockchain integration. <br/>
Once the ecosystem SDK is integrated within digital service, users will be be able to interact with rich earn and spend marketplace experiences, and view their account balance and order history.<br/>
A stellar wallet and account will be created behind the scene for the user. <br/>

![DEMO](https://user-images.githubusercontent.com/3635216/38100813-0f2c7bc2-3387-11e8-930d-03175842e81e.gif)


## Setup
The sample app (or digital service app) needs to initiate the ecosystem sdk, which interacts with the ecosystem backend service. <br/>
The ecosystem backend will block unauthorized requests, therefore digital services will have to provide a valid unique apiKey and appID.<br/>
To setup and run the sample app an authorized apiKey needs to be provided. In order to compile and run the sample app, developers need to add thee apiKey and appID as a string resource. <br/>
In order to receive a test apiKey please contact us.<br/>

Add this lines to `strings.xml` file of the sample app once you receive your appId and apiKey.<br/>
```xml
   <string name="sample_kin_ecosystem_api_key">YOUR_API_KEY</string>
   <string name="sample_app_id">YOUR_APP_ID</string> 
```

** This is an intermediate API and we are working to streamline the integration process **<br/>


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
2. Add this to the app module's `build.gradle` file.
  ```gradle
   dependencies {
       ...
       implementation 'com.github.kinfoundation:kin-ecosystem-android-sdk:dev1'
   }

   ```
3. Create SignInData object and set userID, appID, apiKey etc.
  ```java
   signInData = new SignInData()
               .signInType(SignInTypeEnum.WHITELIST)
               .appId(appID)
               .deviceId(deviceUUID)
               .userId(userID)
               .apiKey(apiKey);
   ```
4. Initiate SDK when the Application starts calling Kin. The first start will begin the blocakchain wallet and account creation process.
  ```java
           try {
               Kin.start(getApplicationContext(), signInData);
           } catch (InitializeException e) {
               //
           }
   ```
5. Launch the marketplace experience.
  ```java
       try {
           Kin.launchMarketplace(MainActivity.this);
            System.out.println("Public address : " + Kin.getPublicAddress());
            } catch (TaskFailedException e) {
              //
        }
   ```
   
## License
The kin-ecosystem-android-sdk library is licensed under [MIT license](LICENSE.md).