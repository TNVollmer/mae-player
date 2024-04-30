package thkoeln.dungeon.player.core.domainprimitives.robot;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@EqualsAndHashCode
public class Inventory {

    private Integer capacity;
    @ElementCollection
    private List<MineableResource> resources;

    protected Inventory() {}

    protected Inventory(Integer capacity, List<MineableResource> resources) {
        this.capacity = capacity;
        this.resources = resources;
    }

    public static Inventory fromCapacity(Integer capacity) {
        if (capacity == null || capacity < 0) throw new DomainPrimitiveException("Invalid Parameter for Inventory!");
        return new Inventory(capacity, new ArrayList<>());
    }

    public Inventory addMineableResource(MineableResource addedResource) {
        MineableResource oldResource = MineableResource.empty(addedResource.getType());
        for (MineableResource resource : resources) {
            if (resource.getType() == addedResource.getType()) {
                oldResource = resource;
                resources.remove(resource);
            }
        }
        resources.add(oldResource.add(addedResource));
        return this;
    }

}
