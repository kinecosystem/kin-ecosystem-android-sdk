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