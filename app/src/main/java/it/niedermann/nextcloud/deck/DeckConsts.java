package it.niedermann.nextcloud.deck;

public class DeckConsts {
    public static final String DEBUG_TAG = "deck";
    private static final String LAST_SYNC_KEY = "lastSync_";


    public static final String LAST_SYNC_KEY(long accountId){
        return LAST_SYNC_KEY+accountId;
    }
}
