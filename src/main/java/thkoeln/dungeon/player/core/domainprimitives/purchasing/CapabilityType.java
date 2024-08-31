package thkoeln.dungeon.player.core.domainprimitives.purchasing;


public enum CapabilityType {
    DAMAGE("DA", "DAMAGE"),
    ENERGY_REGEN("ER", "ENERGY_REGEN"),
    HEALTH("H", "HEALTH"),
    MAX_ENERGY("ME", "MAX_ENERGY"),
    MINING("MI", "MINING"),
    MINING_SPEED("MS", "MINING_SPEED"),
    STORAGE("S", "STORAGE");

    private final String stringValue;
    private final String shopValue;

    CapabilityType(String stringValue, String shopValue) {
        this.stringValue = stringValue;
        this.shopValue = shopValue;
    }

    public String toString() {
        return stringValue;
    }

    public String forUpgrade() {
        return shopValue;
    }
}
