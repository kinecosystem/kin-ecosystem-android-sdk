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
               amount: number; // amount of kin for this offer - price
       }
        
       sender: {
              user_id: string; // optional: user_id who will perform the order
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
            Kin.purchase(offerJwt, new KinCallback<OrderConfirmation>() {
                @Override
                public void onResponse(OrderConfirmation orderConfirmation) {
                    // OrderConfirmation will be called once Ecosystem recieved the payment transaction from user.
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

# Add spend offer opportunity to Kin marketplace
1. Add a NativeOfferObserver to be triggered when the user clicks on your offer in Kin Marketplace.
```java
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
You can remove the observer also by `Kin.removeNativeOfferClickedObserver(nativeSpendOfferClickedObserver)`

2. Create a [NativeSpendOffer](/com/kin/ecosystem/marketplace/model/NativeSpendOffer.java) object:
```java
    NativeSpendOffer nativeOffer =
        new NativeSpendOffer("The offerID") // OfferId should be uniqe
            .title("Offer Title")
            .description("Offer Description")
            .amount(1000)
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
    } catch (ClientException error) {
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
    } catch (ClientException e) {
        ...
    }
```

# Get Order Confirmation
if you wish to get the order status of a certain offer, which already completed or<br>
the user closed the app and you want to get the order status or jwtConfirmation<br>
just follow the example below.
See [ServiceException](../kin-ecosystem-sdk/src/main/java/com/kin/ecosystem/exception/ServiceException.java) for possible errors.
```java
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