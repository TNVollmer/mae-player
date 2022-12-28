package thkoeln.dungeon.monte.domainprimitives;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MineableResourceType {
    @JsonProperty("coal")
    COAL,
    @JsonProperty("iron")
    IRON,
    @JsonProperty("gem")
    GEM,
    @JsonProperty("gold")
    GOLD,
    @JsonProperty("platin")
    PLATIN
}
