package com.technophile.parsingsample;

/**
 * Created by Technophile on 2/5/2018.
 */

public class MyTools {

    public static String getGenre(String s){
        return s.replaceAll("[\\[\\]]","");
    }


}
