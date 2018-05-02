package com.kin.ecosystem.data.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.kin.ecosystem.network.model.CouponCodeResult;
import java.io.IOException;

public class Coupon {

    private CouponInfo couponInfo;

    private CouponCodeResult couponCode;

    public Coupon(CouponInfo couponInfo, CouponCodeResult couponCode) {
        this.couponInfo = couponInfo;
        this.couponCode = couponCode;
    }

    public CouponInfo getCouponInfo() {
        return couponInfo;
    }

    public CouponCodeResult getCouponCode() {
        return couponCode;
    }

    public class CouponInfo {
        @SerializedName("image")
        private String image;

        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("link")
        private String link;

        public String getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getLink() {
            return link;
        }
    }

    @JsonAdapter(CouponType.Adapter.class)
    public enum CouponType {

        COUPON("coupon");

        private String value;

        CouponType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static CouponType fromValue(String text) {
            for (CouponType b : CouponType.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<CouponType> {
            @Override
            public void write(final JsonWriter jsonWriter, final CouponType enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public CouponType read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return CouponType.fromValue(String.valueOf(value));
            }
        }
    }
}
