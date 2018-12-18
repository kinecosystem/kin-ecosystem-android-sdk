### Displaying a specific experience in KinEcosystem ###

#### Launch Kin Marketplace
Optionally, your app can launch the Kin Marketplace offer wall. It displays Earn and Spend offers, which can be added to it by your app or by the Kin Ecosystem Server. When a user selects one of these offers, the Kin Marketplace notifies the app that created the offer. The app can then launch the Earn or Spend activity for the user to complete.
You may choose to add your custom Earn and Spend offers to the Kin Marketplace so that there is a convenient, visible place where the user can access all offers. Some offers displayed in-app might require that the user choose to navigate to a specific page, and therefore might not be so readily visible.

*To display the Kin Marketplace offer wall:*

Call `Kin.launchEcosystem(activity, EcosystemExperience.MARKETPLACE)`.

```java
try {
         Kin.launchEcosystem(MainActivity.this, EcosystemExperience.MARKETPLACE);
          } catch (ClientException e) {
            //
      }
```

#### Launch Kin Order History
Use this option to launch the Ecosystem experience right into the users’ orders history page, where they can view a list of all their spend and earn actions.

*To display the Order History Page:*

Call `Kin.launchEcosystem(activity, EcosystemExperience.ORDER_HISTORY)`.

```java
try {
         Kin.launchEcosystem(MainActivity.this, EcosystemExperience.ORDER_HISTORY);
          } catch (ClientException e) {
            //
      }
```

>**NOTE:** The launchEcosystem function is not a one-time initialization function, you must call it each time you want to display a feature in KinEcosystem side.

