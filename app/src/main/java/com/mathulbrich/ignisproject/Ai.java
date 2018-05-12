package com.mathulbrich.ignisproject;

/**
 * Created by Mathaus Ulbrich on 27/04/2018.
 */

public class Ai {

    static final short PASSING_AI = 0;
    static final short AI_DONT_PRAY = 1;

    public static Response[] getAiResponses() {
        Response[] responses = new Response[2];

        responses[PASSING_AI] = new Response(R.drawable.ignis_stand, R.string.passing_ai, R.raw.passing_ai,
                new int[] {
                R.string.ai
        });
        responses[AI_DONT_PRAY] = new Response(R.drawable.ignis_stand, R.string.ai_dont_pray, R.raw.ai_dont_pray,
                new int[] {
                R.string.pray,
                R.string.god
        });

        return responses;
    }

}
