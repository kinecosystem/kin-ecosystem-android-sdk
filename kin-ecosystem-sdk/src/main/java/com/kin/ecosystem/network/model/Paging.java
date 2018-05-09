package com.kin.ecosystem.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * object accompanying lists - used to fetch next/previous section
 */
public class Paging {

    @SerializedName("cursors")
    private PagingCursors cursors = null;
    @SerializedName("previous")
    private String previous = null;
    @SerializedName("next")
    private String next = null;

    public Paging cursors(PagingCursors cursors) {
        this.cursors = cursors;
        return this;
    }


    /**
     * Get cursors
     *
     * @return cursors
     **/
    public PagingCursors getCursors() {
        return cursors;
    }

    public void setCursors(PagingCursors cursors) {
        this.cursors = cursors;
    }

    public Paging previous(String previous) {
        this.previous = previous;
        return this;
    }


    /**
     * Get previous
     *
     * @return previous
     **/
    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public Paging next(String next) {
        this.next = next;
        return this;
    }


    /**
     * Get next
     *
     * @return next
     **/
    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Paging paging = (Paging) o;
        return this.cursors.equals(paging.cursors) &&
            this.previous.equals(paging.previous) &&
            this.next.equals(paging.next);
    }

    @Override
    public int hashCode() {
        return cursors.hashCode() + previous.hashCode() + next.hashCode();
    }
}



