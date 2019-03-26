package com.kin.ecosystem.core.data.offer;


import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import java.util.List;

public class OfferListUtil {
	public  static void splitOffersByType(List<Offer> list, List<Offer> earnList, List<Offer> spendList) {
		for (Offer offer : list) {
			if (offer.getOfferType() == OfferType.EARN) {
				earnList.add(offer);
			} else {
				spendList.add(offer);
			}
		}
	}
}
