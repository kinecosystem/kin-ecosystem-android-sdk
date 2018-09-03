### Creating a Custom Spend Offer ###

A custom Spend offer allows your users to unlock unique spend opportunities that you define within your app. (Custom offers are created by your app, as opposed to [built-in offers displayed in the Kin Marketplace offer wall](#adding-a-custom-spend-offer-to-the-kin-marketplace-offer-wall).) Your app displays the offer, request user approval, and then [requests payment using the Kin purchase API](#requesting-purchase-payment-for-a-custom-spend-offer).

*To create a custom Spend offer:*


### Requesting purchase Payment for a Custom Spend Offer ###

*To request payment for a custom Spend offer:*

1.	Create a JWT that represents a Spend offer signed by you, using the header and payload templates below. (See [Generating the JWT Token](../README.md#generating-the-jwt-token) for more details about JWT structure).

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
2.	Call `Kin.purchase(…)`, while passing the JWT you built and a callback function that will receive purchase confirmation.

>**NOTES:**
>* The following snippet is taken from the SDK Sample App, in which the JWT is created and signed by the Android client side for presentation purposes only. Do not use this method in production! In production, the JWT must be signed by the server, with a secure private key.
> * See [BlockchainException](COMMON_ERRORS.md#blockchainException--Represents-an-error-originated-with-kin-blockchain-error-code-might-be) and [ServiceException](COMMON_ERRORS.md#serviceexception---represents-an-error-communicating-with-kin-server-error-code-might-be) for possible errors.

```java
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

### Adding a Custom Spend Offer to the Kin Marketplace Offer Wall ###

The Kin Marketplace offer wall displays built-in offers, which are served by the Kin Ecosystem Server. Their purpose is to provide users with opportunities to earn initial Kin funding, which they can later spend on spend offers provided by hosting apps.

You can also choose to display a banner for your custom offer in the Kin Marketplace offer wall. This serves as additional "real estate" in which to let the user know about custom offers within your app. When the user clicks on your custom Spend offer in the Kin Marketplace, your app is notified, and then it continues to manage the offer activity in its own UX flow.

>**NOTE:** You will need to actively launch the Kin Marketplace offer wall so your user can see the offers you added to it. See [Displaying the Kin Marketplace Offer Wall](DISPLAY_MARKETPLACE.md) for more details.

*To add a custom Spend offer to the Kin Marketplace:*

1. Create a `NativeSpendOffer` object as in the example below.

```java
NativeSpendOffer nativeOffer =
        new NativeSpendOffer("The offerID") // OfferId must be a UUID
            .title("Offer Title") // Title to display with offer
            .description("Offer Description") // Desc. to display with offer
            .amount(1000) // Purchase amount in Kin
            .image("Image URL"); // Image to display with offer
```
2.	Create a `NativeOfferObserver` object to be notified when the user clicks on your offer in the Kin Marketplace.

>**NOTE:** You can remove the Observer by calling `Kin.removeNativeOfferClickedObserver(…)`.

```java
private void addNativeOfferClickedObserver() {
try {

Kin.addNativeOfferClickedObserver(getNativeOfferClickedObserver());
        } catch (TaskFailedException e) {
            showToast("Could not add native offer callback");
        }
    }

    private Observer<NativeOfferClickEvent> getNativeOfferClickedObserver() {
        if (nativeSpendOfferClickedObserver == null) {
            nativeSpendOfferClickedObserver = new Observer<NativeOfferClickEvent>() {
                @Override
                public void onChanged(NativeOfferClickEvent nativeOfferClickEvent) {
                    NativeSpendOffer nativeSpendOffer = (NativeSpendOffer) nativeOfferClickEvent.getNativeOffer();
                    if(nativeOfferClickEvent.isDismissOnTap()){
                        new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Native Offer (" + nativeSpendOffer.getTitle() +")")
                            .setMessage("You tapped a native offer and the observer was notified.")
                            .show();
                    } else {
                        Intent nativeOfferIntent = NativeOfferActivity.createIntent(MainActivity.this, nativeSpendOffer.getTitle());
                        startActivity(nativeOfferIntent);
                    }
                }
            };
        }
        return nativeSpendOfferClickedObserver;
}
```

3.	Call `Kin.addNativeOffer(nativeSpendOffer, dismissOnTap)`.

>**NOTE:** Each new offer is added as the first offer in Spend Offers list the Marketplace displays.
Parameter dismissOnTap determine if the Marketplace need to be dismissed on tap.
Adding the same offer twice will update the existing one.

```java
try {
    boolean dismissOnTap = true
    if (Kin.addNativeOffer(nativeSpendOffer, dismissOnTap)) {
        // Native offer added
    } else {
        // Could not add the offer to Kin Marketplace
    }
} catch (ClientException error) {
    ...
}
```

### Removing a Custom Spend Offer from Kin Marketplace ###

*To remove a custom Spend offer from the Kin Marketplace:*

Call `Kin.removeNativeOffer(…)`, passing the offer you want to remove.

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