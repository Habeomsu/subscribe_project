package main.FcmWIthAuth.email.util;

import java.util.Random;

public class EmailUtils {

    public int makeRandomNum(){
        int authNumber;
        Random r = new Random();
        String randomNumber = "";
        for(int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber);

        return authNumber;
    }

}
