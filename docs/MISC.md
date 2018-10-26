### User Has a Kin Account ###

This API will help you determine whether the recipient user has a Kin Account, so if user has account you can [pay Kin to this user](PEER_TO_PEER.md)

Call `Kin.hasAccount(…)`, while passing the userId (your app userId) and a callback function that will receive a boolean value.

```
try {
    Kin.hasAccount(userId, new KinCallback<Boolean>() {
        @Override
        public void onResponse(Boolean hasAccount) {
            if (hasAccount != null && hasAccount){
                createPayToUserOffer(userId);
            } else {
                showSnackbar("Account not found", true)
            }
        }

        @Override
        public void onFailure(KinEcosystemException exception) {
            showSnackbar("Failed - " + exception.getMessage(), true);
        }
    });
} catch (ClientException e) {
    e.printStackTrace();
}
```

### User's Order History Stats ###

This API provides user's stats which include information such number of Earn/Spend orders completed by the user or last earn/spend dates.
UserStats information could be used for re-engaging users, provide specific experience for users who never earn before etc.

```
try {
    Kin.userStats(new KinCallback<UserStats>() {
        @Override
        public void onResponse(UserStats response) {
            if (response.getEarnCount() == 0) {
                //show first time user UI
            }
        }

        @Override
        public void onFailure(KinEcosystemException exception) {
            //handle onFailure
        }
    });
} catch (ClientException e) {
    //handle ClientException
}
```