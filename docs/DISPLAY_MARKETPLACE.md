### Displaying the Kin Marketplace Offer Wall ###

Optionally, your app can launch the Kin Marketplace offer wall. It displays Earn and Spend offers, which can be added to it by your app or by the Kin Ecosystem Server. When a user selects one of these offers, the Kin Marketplace notifies the app that created the offer. The app can then launch the Earn or Spend activity for the user to complete.

You may choose to add your custom Earn and Spend offers to the Kin Marketplace so that there is a convenient, visible place where the user can access all offers. Some offers displayed in-app might require that the user choose to navigate to a specific page, and therefore might not be so readily visible.

>**NOTE:** The launchMarketplace function is not a one-time initialization function; you must call it each time you want to display the Kin Marketplace offer wall.

*To display the Kin Marketplace offer wall:*

Call `Kin.launchMarketplace(…)`.

```java
try {
         Kin.launchMarketplace(MainActivity.this);
          System.out.println("Public address : " + Kin.getPublicAddress());
          } catch (ClientException e) {
            //
      }
```