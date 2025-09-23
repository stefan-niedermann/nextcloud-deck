package it.niedermann.nextcloud.deck.feature_import;

public record ImportProgress(
        int done, int wip, int total
) {
    public boolean indeterminate() {
        return done == 0 && wip == 0 && total == 0;
    }
}