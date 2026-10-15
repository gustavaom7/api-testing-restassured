package com.gustavaom7.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model for Wikipedia Search API response
 * Represents the JSON structure returned by search endpoint.
 * RestAssured maps with Jackson when it is on the classpath, so every field carries
 * @JsonProperty (Jackson) next to @SerializedName (Gson): the JSON keys are lowercase.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {

    @JsonProperty("batchcomplete")

    @SerializedName("batchcomplete")
    private Object batchComplete;

    @JsonProperty("query")

    @SerializedName("query")
    private Query query;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Query {
        @JsonProperty("search")
        @SerializedName("search")
        private List<SearchResult> search;

        public List<SearchResult> getSearch() {
            return search;
        }

        public void setSearch(List<SearchResult> search) {
            this.search = search;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResult {
        @JsonProperty("ns")
        @SerializedName("ns")
        private int namespace;

        @JsonProperty("title")

        @SerializedName("title")
        private String title;

        @JsonProperty("pageid")

        @SerializedName("pageid")
        private int pageId;

        @JsonProperty("size")

        @SerializedName("size")
        private int size;

        @JsonProperty("wordcount")

        @SerializedName("wordcount")
        private int wordCount;

        @JsonProperty("snippet")

        @SerializedName("snippet")
        private String snippet;

        @JsonProperty("timestamp")

        @SerializedName("timestamp")
        private String timestamp;

        public String getTitle() {
            return title;
        }

        public String getSnippet() {
            return snippet;
        }

        public int getPageId() {
            return pageId;
        }

        public int getSize() {
            return size;
        }

        public int getWordCount() {
            return wordCount;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    public boolean isBatchComplete() {
        return batchComplete != null;
    }

    public Query getQuery() {
        return query;
    }

    public List<SearchResult> getSearchResults() {
        return query != null ? query.getSearch() : null;
    }
}
