### Adding a Custom Earn or Spend Offers to the Kin Marketplace Offer Wall ###

The Kin Marketplace offer wall displays built-in offers, which are served by the Kin Ecosystem Server. Their purpose is to provide users with opportunities to earn initial Kin funding, which they can later spend on spend offers provided by hosting apps.

You can also choose to display a banner for your custom offer in the Kin Marketplace offer wall. This serves as additional "real estate" in which to let the user know about custom offers within your app. When the user clicks on your custom Spend or Earn offer in the Kin Marketplace, your app will be notified, and then you can continues to manage the offer with your own logig and UX/UI flow.

>**NOTE:** You will need to actively launch the Kin Marketplace offer wall so your user can see the offers you added to it. See [Displaying the Kin Marketplace Offer Wall](DISPLAY_EXPERIENCE.md) for more details.

*To add a custom Spend or Earn offer to the Kin Marketplace:*


1.	Create a `NativeOfferObserver` object to be notified when the user clicks on your offer in the Kin Marketplace.

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
            Observer<NativeOfferClickEvent> nativeOfferClickedObserver = new Observer<NativeOfferClickEvent>() {
                @Override
                public void onChanged(NativeOfferClickEvent nativeOfferClickEvent) {
                    NativeOffer nativedOffer = nativeOfferClickEvent.getNativeOffer();
                    if(nativeOfferClickEvent.isDismissOnTap()){
                        new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Native Offer (" + nativeOffer.getTitle() +")")
                            .setMessage("You tapped a native offer and the observer was notified.")
                            .show();
                    } else {
                        Intent nativeOfferIntent = NativeOfferActivity.createIntent(MainActivity.this, nativeOffer.getTitle());
                        startActivity(nativeOfferIntent);
                    }
                }
            };
        return nativeOfferClickedObserver;
}
```

2. Create a `NativeOffer` object as in the examples below.

#### Spend Offer:
```java
NativeOffer nativeOffer =
        new NativeSpendOfferBuilder("The offerID") // OfferId must be a UUID
            .title("Offer Title") // Title to display with offer
            .description("Offer Description") // Desc. to display with offer
            .amount(150) // Purchase amount in Kin
            .image("Image URL")// Image to display with offer
            ,build(); 
```

#### Earn Offer:
```java
NativeOffer nativeOffer =
        new NativeEarnOfferBuilder("The offerID") // OfferId must be a UUID
            .title("Offer Title") // Title to display with offer
            .description("Offer Description") // Desc. to display with offer
            .amount(100) // The Kin amount the user can earn by completing the earn offer 
            .image("Image URL") // Image to display with offer
            ,build(); 
```



3.	Call `Kin.addNativeOffer(nativeOffer, dismissOnTap)`.

>**NOTE:** Each new offer is added as the first offer in Spend/Earn Offers list the Marketplace displays.
Parameter dismissOnTap determine if the Marketplace need to be dismissed on tap.
Adding the same offer twice will update the existing one.

```java
try {
    boolean dismissOnTap = true
    if (Kin.addNativeOffer(nativeOffer, dismissOnTap)) {
        // Native offer added
    } else {
        // Could not add the offer to Kin Marketplace
    }
} catch (ClientException error) {
    ...
}
```

### Removing a Custom Spend or Earn Offer from Kin Marketplace ###

*To remove a custom Spend or Earn offer from the Kin Marketplace:*

Call `Kin.removeNativeOffer(…)`, passing the offer you want to remove.

```java
try {
        if (Kin.removeNativeOffer(nativeOffer)) {
             // Native offer removed
        } else {
            // Native offer isn't in Kin Marketplace
        }
    } catch (ClientException e) {
        ...
}
```