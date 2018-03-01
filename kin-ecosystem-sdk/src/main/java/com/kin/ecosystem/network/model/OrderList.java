package com.kin.ecosystem.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * a list of submitted orders
 */
public class OrderList {
    @SerializedName("orders")
    private List<Order> orders = null;
    @SerializedName("paging")
    private Paging paging = null;

    public OrderList orders(List<Order> orders) {
        this.orders = orders;
        return this;
    }

    public OrderList addOrder(Order orderItem) {

        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }

        this.orders.add(orderItem);
        return this;
    }

    public OrderList addOrderAtIndex(int index, Order orderItem) {

        if (this.orders == null) {
            this.orders = new ArrayList<>();
            this.orders.add(orderItem);
        }
        else {
            this.orders.add(index, orderItem);
        }

        return this;
    }

    /**
     * Get orders
     *
     * @return orders
     **/
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public OrderList paging(Paging paging) {
        this.paging = paging;
        return this;
    }


    /**
     * Get paging
     *
     * @return paging
     **/
    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderList orderList = (OrderList) o;
        return Objects.equals(this.orders, orderList.orders) &&
                Objects.equals(this.paging, orderList.paging);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orders, paging);
    }
}



