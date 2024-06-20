package com.youtube.fizantofuzz.YouTube.Comments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Comment {

    public class AuthorChannelId {

        @SerializedName("value")
        @Expose
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public class Item {

        @SerializedName("kind")
        @Expose
        private String kind;
        @SerializedName("etag")
        @Expose
        private String etag;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("snippet")
        @Expose
        private Snippet snippet;

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Snippet getSnippet() {
            return snippet;
        }

        public void setSnippet(Snippet snippet) {
            this.snippet = snippet;
        }

    }

    public class Model {

        @SerializedName("kind")
        @Expose
        private String kind;
        @SerializedName("etag")
        @Expose
        private String etag;
        @SerializedName("pageInfo")
        @Expose
        private PageInfo pageInfo;
        @SerializedName("items")
        @Expose
        private List<Item> items = null;

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public PageInfo getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

    }

    public class PageInfo {

        @SerializedName("totalResults")
        @Expose
        private Integer totalResults;
        @SerializedName("resultsPerPage")
        @Expose
        private Integer resultsPerPage;

        public Integer getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
        }

        public Integer getResultsPerPage() {
            return resultsPerPage;
        }

        public void setResultsPerPage(Integer resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
        }

    }

    public class Replies {

        @SerializedName("comments")
        @Expose
        private List<TopLevelComment> comments = null;

        public List<TopLevelComment> getComments() {
            return comments;
        }

        public void setTopLevelComments(List<TopLevelComment> comments) {
            this.comments = comments;
        }

    }

    public class Snippet {

        @SerializedName("videoId")
        @Expose
        private String videoId;
        @SerializedName("textDisplay")
        @Expose
        private String textDisplay;
        @SerializedName("textOriginal")
        @Expose
        private String textOriginal;
        @SerializedName("parentId")
        @Expose
        private String parentId;
        @SerializedName("authorDisplayName")
        @Expose
        private String authorDisplayName;
        @SerializedName("authorProfileImageUrl")
        @Expose
        private String authorProfileImageUrl;
        @SerializedName("authorChannelUrl")
        @Expose
        private String authorChannelUrl;
        @SerializedName("authorChannelId")
        @Expose
        private AuthorChannelId authorChannelId;
        @SerializedName("canRate")
        @Expose
        private Boolean canRate;
        @SerializedName("viewerRating")
        @Expose
        private String viewerRating;
        @SerializedName("likeCount")
        @Expose
        private Integer likeCount;
        @SerializedName("topLevelComment")
        @Expose
        private TopLevelComment topLevelComment;
        @SerializedName("publishedAt")
        @Expose
        private String publishedAt;
        @SerializedName("updatedAt")
        @Expose
        private String updatedAt;

        public TopLevelComment getTopLevelComment() {
            return topLevelComment;
        }

        public void setTopLevelComment(TopLevelComment topLevelComment) {
            this.topLevelComment = topLevelComment;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getTextDisplay() {
            return textDisplay;
        }

        public void setTextDisplay(String textDisplay) {
            this.textDisplay = textDisplay;
        }

        public String getTextOriginal() {
            return textOriginal;
        }

        public void setTextOriginal(String textOriginal) {
            this.textOriginal = textOriginal;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getAuthorDisplayName() {
            return authorDisplayName;
        }

        public void setAuthorDisplayName(String authorDisplayName) {
            this.authorDisplayName = authorDisplayName;
        }

        public String getAuthorProfileImageUrl() {
            return authorProfileImageUrl;
        }

        public void setAuthorProfileImageUrl(String authorProfileImageUrl) {
            this.authorProfileImageUrl = authorProfileImageUrl;
        }

        public String getAuthorChannelUrl() {
            return authorChannelUrl;
        }

        public void setAuthorChannelUrl(String authorChannelUrl) {
            this.authorChannelUrl = authorChannelUrl;
        }

        public AuthorChannelId getAuthorChannelId() {
            return authorChannelId;
        }

        public void setAuthorChannelId(AuthorChannelId authorChannelId) {
            this.authorChannelId = authorChannelId;
        }

        public Boolean getCanRate() {
            return canRate;
        }

        public void setCanRate(Boolean canRate) {
            this.canRate = canRate;
        }

        public String getViewerRating() {
            return viewerRating;
        }

        public void setViewerRating(String viewerRating) {
            this.viewerRating = viewerRating;
        }

        public Integer getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(Integer likeCount) {
            this.likeCount = likeCount;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }

    public class TopLevelComment {

        @SerializedName("kind")
        @Expose
        private String kind;
        @SerializedName("etag")
        @Expose
        private String etag;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("snippet")
        @Expose
        private Snippet snippet;

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Snippet getSnippet() {
            return snippet;
        }

        public void setSnippet(Snippet snippet) {
            this.snippet = snippet;
        }

    }
}
