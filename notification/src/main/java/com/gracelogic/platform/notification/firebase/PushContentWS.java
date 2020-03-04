package com.gracelogic.platform.notification.firebase;

import java.util.HashMap;

/**
 * Push notification content.
 * Should not exceed certain length in serialized form due to platform limitations.
 * (E.g. 255 bytes for iOS)
 */

public class PushContentWS {

    /**
     * Type of the push notification notification according to
     * https://docs.google.com/document/d/1Gg0H8Y36dQZH5ZOEQuupO9Gs5X8iXRN02rIzMdgppH8/edit#
     */
    public enum PushType {

        /**
         * General type
         */
        UNIVERSAL,

        /**
         * Populates PIN code automatically
         */
        PIN_CODE,

        /**
         * Navigate to product
         */
        PRODUCT,

        /**
         * Navigate to payment application
         */
        FORM,

        /**
         * Opens map with at specified location
         */
        SHOW_MAP,

        /**
         * Navigates to messages screen
         */
        MESSAGE,

        /**
         * Opens URL in WebView
         */
        URL,

        /**
         * Allows to load content from remote server
         */
        REMOTE_CONTENT
    }


    /**
     * Message to be shown.
     * Shouldn't contains any IDs or technical info
     */
    private String text;

    /**
     * Message title.
     */

    private String title;

    /**
     * Type of the push notification
     */
    private PushType type;

    /**
     * Number to be shown near application's icon
     */

    private Integer badge;

    /**
     * Filename of a sound resource bundled with an app, played upon receiving
     */

    private String sound;

    /**
     * Image URL
     */

    private String imageURL;

    /**
     * Arbitrary notification details depending on the type
     */

    private HashMap<String, String> details;

    public PushContentWS(String text, PushType type) {
        this.text = text;
        this.type = type;
    }

    public PushContentWS() {
    }

    public PushContentWS(String text, String title, PushType type, Integer badge, String sound, String imageURL, HashMap<String, String> details) {
        this.text = text;
        this.title = title;
        this.type = type;
        this.badge = badge;
        this.sound = sound;
        this.imageURL = imageURL;
        this.details = details;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PushType getType() {
        return type;
    }

    public void setType(PushType type) {
        this.type = type;
    }

    public Integer getBadge() {
        return badge;
    }

    public void setBadge(Integer badge) {
        this.badge = badge;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public HashMap<String, String> getDetails() {
        return details;
    }

    public void setDetails(HashMap<String, String> details) {
        this.details = details;
    }
}