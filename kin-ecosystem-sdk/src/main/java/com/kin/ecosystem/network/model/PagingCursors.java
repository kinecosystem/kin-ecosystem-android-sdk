package com.kin.ecosystem.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * PagingCursors
 */
public class PagingCursors {
    @SerializedName("after")
    private String after = null;
    @SerializedName("before")
    private String before = null;

    public PagingCursors after(String after) {
        this.after = after;
        return this;
    }


    /**
     * Get after
     *
     * @return after
     **/
    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public PagingCursors before(String before) {
        this.before = before;
        return this;
    }


    /**
     * Get before
     *
     * @return before
     **/
    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PagingCursors pagingCursors = (PagingCursors) o;
        return Objects.equals(this.after, pagingCursors.after) &&
                Objects.equals(this.before, pagingCursors.before);
    }

    @Override
    public int hashCode() {
        return Objects.hash(after, before);
    }
}



