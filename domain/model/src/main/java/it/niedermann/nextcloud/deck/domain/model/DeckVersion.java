package it.niedermann.nextcloud.deck.domain.model;

public class DeckVersion extends Version {

    public static final DeckVersion V_0_5_0 = new DeckVersion("0.5.0", 0, 5, 0);
    public static final DeckVersion V_0_8_0 = new DeckVersion("0.8.0", 0, 8, 0);

    public DeckVersion(String version, int major, int minor, int patch) {
        super(version, major, minor, patch);
    }

    public static DeckVersion parse(String version) {
        return of(Version.parse(version));
    }

    public static DeckVersion of(Version version) {
        return new DeckVersion(version.getVersion(), version.getMajor(), version.getMinor(), version.getPatch());
    }

    public boolean isSupported() {
        return isGreaterThanOrEqual(V_0_8_0);
    }
}
