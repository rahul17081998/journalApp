package com.rahul.journal_app.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class TwitterUser {

    private long id;

    @JsonProperty("created_at")
    private String createdAt;

    private String status;

    private String text;

    @JsonProperty("display_text")
    private String displayText;

    private int likes;

    private int retweets;

    private int replies;

    private int bookmarks;

    private int quotes;

    private boolean sensitive;

    private String lang;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("in_reply_to_screen_name")
    private String inReplyToScreenName;

    @JsonProperty("in_reply_to_status_id_str")
    private String inReplyToStatusIdStr;

    @JsonProperty("in_reply_to_user_id_str")
    private String inReplyToUserIdStr;

    private String views;

    private Author author;

    private Entities entities;

    // Nested class for Author
    @Data
    @NoArgsConstructor
    public static class Author {
        @JsonProperty("rest_id")
        private String restId;

        private String name;

        @JsonProperty("screen_name")
        private String screenName;

        private String image;

        @JsonProperty("blue_verified")
        private boolean blueVerified;

        @JsonProperty("sub_count")
        private int subCount;
    }

    // Nested class for Entities
    @Data
    @NoArgsConstructor
    public static class Entities {
        private List<String> hashtags;

        private List<String> symbols;

        private List<UserMention> userMentions;

        private List<Media> media;

        @Data
        @NoArgsConstructor
        public static class UserMention {
            @JsonProperty("id_str")
            private String idStr;

            private String name;

            @JsonProperty("screen_name")
            private String screenName;

            private List<Integer> indices;
        }

        @Data
        @NoArgsConstructor
        public static class Media {
            @JsonProperty("display_url")
            private String displayUrl;

            @JsonProperty("expanded_url")
            private String expandedUrl;

            @JsonProperty("id_str")
            private String idStr;

            private String type;

            private String url;

            @JsonProperty("media_url_https")
            private String mediaUrlHttps;

            @JsonProperty("media_key")
            private String mediaKey;

            @JsonProperty("video_info")
            private VideoInfo videoInfo;

            @JsonProperty("sizes")
            private Map<String, MediaSize> sizes;

            @Data
            @NoArgsConstructor
            public static class VideoInfo {
                @JsonProperty("aspect_ratio")
                private List<Integer> aspectRatio;

                @JsonProperty("duration_millis")
                private int durationMillis;

                private List<Variant> variants;

                @Data
                @NoArgsConstructor
                public static class Variant {
                    @JsonProperty("content_type")
                    private String contentType;

                    private String url;

                    private Integer bitrate; // Bitrate is nullable for some variants
                }
            }

            @Data
            @NoArgsConstructor
            public static class MediaSize {
                private int h;
                private int w;
                private String resize;
            }
        }
    }
}