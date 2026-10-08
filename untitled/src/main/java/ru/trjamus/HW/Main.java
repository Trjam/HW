package ru.trjamus.HW;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        MegaHashMap<Integer, String> mhm= new MegaHashMap<>();
        mhm.put(222,"444");
        mhm.put(22,"44");
        mhm.put(2,"4");

        System.out.println(mhm.get(222) + " " + mhm.get(22) + " " + mhm.get(2));
        System.out.println(mhm.get(22));

        mhm.remove(222);

        System.out.println(mhm.get(222) + " " + mhm.get(22) + " " + mhm.get(2));

    }
}


