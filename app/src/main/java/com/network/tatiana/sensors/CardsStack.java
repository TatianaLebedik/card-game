package com.network.tatiana.sensors;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardsStack {

    private int level;
    private List<Card> cardList;
    private Context context;
    private Random randomGenerator;

    CardsStack(Context context, int level){
        this.level = level;
        this.context = context;
        cardList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            cardList.add(new Card(context, "level"+ level + "_left_", i));
        }
        for(int i = 0; i < 5; i++){
            cardList.add(new Card(context, "level"+ level + "_right_", i));
        }
        randomGenerator = new Random();
    }


    Card getCard(){
        Card card;
        if(!cardList.isEmpty()) {
            int index = randomGenerator.nextInt(cardList.size());
            card = cardList.get(index);
            cardList.remove(index);
        }
        else{
            card = null;
        }
        return card;
    }







}
