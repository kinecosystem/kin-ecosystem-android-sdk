package com.kin.ecosystem.network.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * the result of a completed html poll
 */
public class HTMLPollAnswer {

    /**
     * Gets or Sets contentType
     */
    @JsonAdapter(ContentTypeEnum.Adapter.class)
    public enum ContentTypeEnum {

        HTMLPOLLANSWER("HTMLPollAnswer");

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
    @SerializedName("answers")
    private Map<String, String> answers = null;

    public HTMLPollAnswer contentType(ContentTypeEnum contentType) {
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

    public HTMLPollAnswer answers(Map<String, String> answers) {
        this.answers = answers;
        return this;
    }

    public HTMLPollAnswer putAnswersItem(String key, String answersItem) {

        if (this.answers == null) {
            this.answers = null;
        }

        this.answers.put(key, answersItem);
        return this;
    }

    /**
     * Get answers
     *
     * @return answers
     **/
    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HTMLPollAnswer htMLPollAnswer = (HTMLPollAnswer) o;
        return Objects.equals(this.contentType, htMLPollAnswer.contentType) &&
                Objects.equals(this.answers, htMLPollAnswer.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentType, answers);
    }
}



