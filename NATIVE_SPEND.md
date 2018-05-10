# Create Native Spend Offer
In order to create a native spend offer in your app
1. Generate a JWT that represent a SpendOffer signed by you. (see [spend offer jwt specs](#spend-offer-jwt-specs))
2. Call the method: `Kin.purchase(offerJwt, callback);` (see [example below](#example-from-sample-app))

### Spend offer jwt specs
1. We will support `ES256` signature algorithm later on, right now you can use `RS512`.
2. Header will follow this template
```aidl
    {
        "alg": "RS512", // We will support ES256 signature algorithem 
        "typ": "JWT",
        "kid": string" // identifier of the keypair that was used to sign the JWT. identifiers and public keys will be provided by signer authority. This enables using multiple private/public key pairs (a list of public keys and their ids need to be provided by signer authority to verifier in advanced)
    }
```
3. SpendOffer payload template
```aidl
    {
        // common/ standard fields
        iat: number;  // issued at - seconds from epoc
        iss: string; // issuer - please contact us to recive your issuer
        exp: number; // expiration
        sub: "spend"
        
        // application fields
        offer: {
                id: string; // offer id is decided by you (internal)
                title: string; // offer title - appears in order history
                description: string; // offer description - appears in order history
                amount: number; // amount of kin for this offer - price
                wallet_address: string; // address the client should send kin to to acquire this offer
            }
    }
```
### Example from sample app
In the sample app the spend JWT is created and signed by the Android client side for presentation purpose only- do not use this approach on real production app.
JWT need to be signed by server side where private key is secure.
```java
    String offerJwt = JwtUtil.generateSpendOfferExampleJWT(BuildConfig.SAMPLE_APP_ID);
        
        try {
            Kin.purchase(offerJwt, new Callback<String>() {
                @Override
                public void onResponse(String jwtConfirmation) {
                    // Send confirmation JWT back to the server in order prove that the user
                    // completed the blockchain transaction and purchase can be unlocked for this user.
                    System.out.println("Succeed to create native spend.\n jwtConfirmation: " + jwtConfirmation);
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println("Failed - " + t.getMessage());
                }
            });
        } catch (TaskFailedException e) {
            e.printStackTrace();
        }
```

# Add spend offer opportunity to marketplace
1. Add a NativeOfferCallback to be triggered when the user clicks on your offer in Kin Marketplace.
```java
        try {
            Kin.start(getApplicationContext(), signInData)
            .addNativeOfferCallback(new Callback<NativeSpendOffer>() {
                    @Override
                    public void onResponse(NativeSpendOffer offer) {
                        // Do checks on the offer
                      
                    }
        
                    @Override
                    public void onFailure(Throwable t) {
        
                    }
                });
        } catch (InitializeException e) {
            e.printStackTrace();
        }
```
2. Create a [NativeSpendOffer](/kin-ecosystem-sdk/src/main/java/com/kin/ecosystem/marketplace/model/NativeSpendOffer.java) object:
```java
    NativeSpendOffer nativeOffer =
        new NativeSpendOffer("The offerID") // OfferId should be uniqe
            .title("Offer Title")
            .description("Offer Description")
            .amount(int_amount)
            .image("A URL to offer image");
```
3. Add the offer to Kin Marketplace, notice each offer you add will be added at index 0.
```java
    try {
        if (Kin.addNativeOffer(nativeSpendOffer)) {
            // Native offer added
        } else {
            // Native offer already in Kin Marketplace
        }
    } catch (TaskFailedException e) {
        ...
    }
```

4. Remove the offer, you can also remove the offer from Kin Marketplace
```java
    try {
        if (Kin.removeNativeOffer(nativeSpendOffer)) {
             // Native offer removed
        } else {
            // Native offer isn't in Kin Marketplace
        }
    } catch (TaskFailedException e) {
        e.printStackTrace();
    }
```