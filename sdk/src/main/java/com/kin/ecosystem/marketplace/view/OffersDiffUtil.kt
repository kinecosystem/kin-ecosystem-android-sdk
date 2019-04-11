package com.kin.ecosystem.marketplace.view

import android.support.v7.util.DiffUtil
import com.kin.ecosystem.core.network.model.Offer

class OffersDiffUtil(private val oldOffers: List<Offer>, private val newOffers: List<Offer>): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldOffers[oldItemPosition].id == newOffers[newItemPosition].id
    }

    override fun getOldListSize(): Int = oldOffers.size

    override fun getNewListSize(): Int = newOffers.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldOffers[oldItemPosition].equals(newOffers[newItemPosition])
    }
}