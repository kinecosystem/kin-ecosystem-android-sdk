package com.kin.ecosystem.network.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

/**
 * a poll implemented as an HTML page
 */
public class HTMLPoll {

    /**
     * Gets or Sets contentType
     */
    @JsonAdapter(ContentTypeEnum.Adapter.class)
    public enum ContentTypeEnum {

        HTMLPOLL("HTMLPoll");

        private String value;

        ContentTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static ContentTypeEnum fromValue(String text) {
            for (ContentTypeEnum b : ContentTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<ContentTypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final ContentTypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public ContentTypeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return ContentTypeEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("content_type")
    private ContentTypeEnum contentType = null;
    @SerializedName("content")
    private String content = null;

    public HTMLPoll contentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
        return this;
    }


    /**
     * Get contentType
     *
     * @return contentType
     **/
    public ContentTypeEnum getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }

    public HTMLPoll content(String content) {
        this.content = content;
        return this;
    }


    /**
     * Get content
     *
     * @return content
     **/
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HTMLPoll htMLPoll = (HTMLPoll) o;
        return Objects.equals(this.contentType, htMLPoll.contentType) &&
                Objects.equals(this.content, htMLPoll.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentType, content);
    }
}



