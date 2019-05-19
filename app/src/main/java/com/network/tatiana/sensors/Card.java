package com.network.tatiana.sensors;

import android.content.Context;
public class Card {

    private  String true_way;
    private int cardResId;
    private int answResId;

    Card(Context context, String way, int id){
            this.true_way = way;

            cardResId = context.getResources().getIdentifier(
                     way + "card" + id, "drawable", context.getPackageName());
            System.out.println("CardID:" + cardResId);
            answResId = context.getResources().getIdentifier(
                    way + "answ" + id, "drawable", context.getPackageName());
            System.out.println("AnswID:" + answResId);
    }
    public String getTrue_way() {
        return true_way;
    }

    public int getCardResId() {
        return cardResId;
    }

    public int getAnswResId() {
        return answResId;
    }
}
