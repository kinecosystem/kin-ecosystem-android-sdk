# Create Native Earn Offer
In order to create a native earn offer in your app
1. Generate a JWT that represent a EarnOffer signed by you. (see [spend offer jwt specs](#earn-offer-jwt-specs))
2. Call the method: `Kin.requestPayment(offerJwt, callback);` (see [example below](#example-from-sample-app))
3. The Ecosystem backend service will validate offerJwt and will create a blockchain transactions on behalf of the digital service to the specified user.<br>
Ecosystem Service backend should hold sufficient Kin funds for this offer. While testing Ecosystem backend service will accept any payment request.<br>
On real use case digital service need to have an open account with sufficient funds - please contact us for more details.

### Earn offer jwt specs
1. We will support `ES256` signature algorithm later on, right now you can use `RS512`.
2. Header will follow this template
```aidl
    {
        "alg": "RS512", // We will support ES256 signature algorithem 
        "typ": "JWT",
        "kid": string" // identifier of the keypair that was used to sign the JWT. identifiers and public keys will be provided by signer authority. This enables using multiple private/public key pairs (a list of public keys and their ids need to be provided by signer authority to verifier in advanced)
    }
```
3. EarnOffer payload template
```aidl
    {
        // common/ standard fields
        iat: number;  // issued at - seconds from epoc
        iss: string; // issuer - please contact us to recive your issuer
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
              description: string; // order description - appears in order history
       }
    }
```
### Example from sample app
In the sample app the spend JWT is created and signed by the Android client side for presentation purpose only- do not use this approach on real production app.
JWT need to be signed by server side where private key is secure.
See [BlockchainException](../kin-ecosystem-sdk/src/main/java/com/kin/ecosystem/exception/BlockchainException.java) and [ServiceException](../kin-ecosystem-sdk/src/main/java/com/kin/ecosystem/exception/ServiceException.java) for possible errors.
```java
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
    } catch (ClientException exception) {
        exception.printStackTrace();
    }
```