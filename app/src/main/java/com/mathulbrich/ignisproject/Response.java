package com.mathulbrich.ignisproject;

/**
 * Created by Mathaus Ulbrich on 27/04/2018.
 */

public class Response {

    private int image;
    private int message;
    private int voice;
    private int[] trigger;

    public Response(int image, int message, int voice, int[] trigger) {
        this.image = image;
        this.message = message;
        this.voice = voice;
        this.trigger = trigger;
    }

    public int getImage() {
        return image;
    }

    public int getMessage() {
        return message;
    }

    public int getVoice() {
        return voice;
    }

    public int[] getTrigger() {
        return trigger;
    }

}
