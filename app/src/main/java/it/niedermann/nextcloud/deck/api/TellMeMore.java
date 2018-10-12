package it.niedermann.nextcloud.deck.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TellMeMore {
    public static String bitch(InputStream is){
        return motherFucker(new InputStreamReader(is));
    }

    public static String motherFucker(InputStreamReader is){
        BufferedReader br = new BufferedReader(is);
        StringBuffer sb = new StringBuffer();
        try {
            String s = null;
            while ((s=br.readLine()) !=null){
                sb.append(s);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
