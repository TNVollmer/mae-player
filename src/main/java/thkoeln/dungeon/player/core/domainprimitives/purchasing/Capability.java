package thkoeln.dungeon.player.core.domainprimitives.purchasing;

import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class Capability {
    private CapabilityType type;
    private Integer level;
    public static final Integer MIN_LEVEL = 0;
    public static final Integer MAX_LEVEL = 5;


    /**
     * @param type
     * @return Base capability for the given type
     */
    public static Capability baseForType(CapabilityType type) {
        return forTypeAndLevel(type, MIN_LEVEL);
    }

    /**
     * @param type
     * @param level
     * @return Capability for given type and level
     */
    public static Capability forTypeAndLevel(CapabilityType type, Integer level) {
        if (type == null || level == null) throw new DomainPrimitiveException("type == null || level == null");
        if (level < MIN_LEVEL || level > MAX_LEVEL)
            throw new DomainPrimitiveException("level < MIN_LEVEL || level > MAX_LEVEL");
        Capability capability = new Capability();
        capability.setLevel(level);
        capability.setType(type);
        return capability;
    }


    /**
     * @return Complete list of all base capabities a robot can have
     */
    public static List<Capability> allBaseCapabilities() {
        List<Capability> allBaseCapabilities = new ArrayList<>();
        for (CapabilityType capabilityType : CapabilityType.values()) {
            allBaseCapabilities.add(Capability.baseForType(capabilityType));
        }
        return allBaseCapabilities;
    }


    /**
     * @return Same capability, one level higher, in case the max is not yet reached. Otherwise null is returned.
     */
    public Capability nextLevel() {
        if (level < MAX_LEVEL) return forTypeAndLevel(this.type, this.level + 1);
        else return null;
    }


    public boolean isMinimumLevel() {
        return Objects.equals(level, MIN_LEVEL);
    }


    public boolean isMaximumLevel() {
        return Objects.equals(level, MAX_LEVEL);
    }


    @Override
    public String toString() {
        return type.toString() + "-" + level;
    }

    public String toStringForCommand() {
        return type.name() + "_" + level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capability that)) return false;
        return type == that.type && level.equals(that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, level);
    }
}
