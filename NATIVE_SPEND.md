## Create Native Spend Offer
In order to create a native spend offer in your app
1. Generate a JWT that represent a SpendOffer signed by you.
2. Call the method below:
```java
    Kin.purchase(offerJwt, callback);
```

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
        sub: "register"
        
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
### Example
```java
    String offerJwt = JwtUtil.generateSpendOfferExampleJWT(BuildConfig.SAMPLE_APP_ID);
        
        try {
            Kin.purchase(offerJwt, new Callback<String>() {
                @Override
                public void onResponse(String jwtConfirmation) {
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