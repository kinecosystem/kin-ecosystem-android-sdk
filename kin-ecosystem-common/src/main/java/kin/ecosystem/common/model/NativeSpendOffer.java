package kin.ecosystem.common.model;

public class NativeSpendOffer extends NativeOffer {

    public NativeSpendOffer(String id) {
        super(id, OfferType.SPEND);
    }

    public NativeSpendOffer title(String title) {
        this.setTitle(title);
        return this;
    }

    public NativeSpendOffer description(String description) {
        this.setDescription(description);
        return this;
    }

    public NativeSpendOffer amount(Integer amount) {
        this.setAmount(amount);
        return this;
    }

    public NativeSpendOffer image(String image) {
        this.setImage(image);
        return this;
    }
}
