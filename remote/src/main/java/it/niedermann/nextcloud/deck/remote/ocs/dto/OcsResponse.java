package it.niedermann.nextcloud.deck.remote.ocs.dto;

import com.google.gson.annotations.SerializedName;

public class OcsResponse<T> {
    public OcsWrapper<T> ocs;

    public static class OcsWrapper<T> {
        public OcsMeta meta;
        public T data;

        public static class OcsMeta {
            public String status;
            @SerializedName("statuscode")
            public int statusCode;
            public String message;
        }
    }
}