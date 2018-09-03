### Requesting an Order Confirmation ###

In the normal flow of a transaction, you will receive an order confirmation from the Kin Server through the purchase API's callback function. This indicates that the transaction was completed. But if you missed this notification for any reason, for example, because the user closed the app before it arrived, or the app closed due to some error, you can request confirmation for an order according to its ID.

*To request an order confirmation:*

Call `Kin.getOrderConfirmation(…)`, while passing the order’s ID and implementing the appropriate callback functions. (See [ServiceException](COMMON_ERRORS.md#serviceexception---represents-an-error-communicating-with-kin-server-error-code-might-be) for possible errors.)

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