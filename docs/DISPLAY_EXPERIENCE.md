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


#### Launch Backup And Restore Flows ###
Optionally, your app can launch the Kin Backup or Restore flows and receive callbacks when user either completed successfully or failure.

* Step 1 - Create `backupAndRestore` to handle responses:
Call `Kin.getBackupAndRestoreManager(activity)`
```java
    try {
        backupAndRestore = Kin.getBackupAndRestoreManager(MainActivity.this);
    } catch (ClientException e) {
        // sdk not initialized
    }
```

* Step 2 - Add callbacks for backup or restore flow as you wish:
Call `backupAndRestore.registerBackupCallback(callback)` or `backupAndRestore.registerRestoreCallback`
```java
    // For Backup Flow
    backupAndRestore.registerBackupCallback(new BackupAndRestoreCallback() {
        @Override
        public void onSuccess() {
            showSnackbar("Backup Succeed", false);
        }

        @Override
        public void onCancel() {
            showSnackbar("Backup Canceled", false);
        }

        @Override
        public void onFailure(Throwable throwable) {
            showSnackbar("Backup Failed " + throwable.getMessage(), true);
        }
    });

    // For Restore Flow
    backupAndRestore.registerRestoreCallback(new BackupAndRestoreCallback() {
        @Override
        public void onSuccess() {
            showSnackbar("Restore Succeed", false);
        }

        @Override
        public void onCancel() {
            showSnackbar("Restore Canceled", false);
        }

        @Override
        public void onFailure(Throwable throwable) {
            showSnackbar("Restore Failed " + throwable.getMessage(), true);
        }
    });
```

* Step 3 - In your `onActivityResult` method: Call `backupAndRestore.onActivityResult(...)` to pass the results.
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        backupAndRestore.onActivityResult(requestCode, resultCode, data);
    }
```

* Step 4 - Finally, launch Backup or Restore simply by calling: `backupAndRestore.backupFlow()` or `backupAndRestore.restoreFlow()`.

* Step 5 - After you received the callback, release the backupAndRestore by calling: `backupAndRestore.release()`