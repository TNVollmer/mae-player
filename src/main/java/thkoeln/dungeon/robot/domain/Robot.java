package thkoeln.dungeon.robot.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.domainprimitives.Capability;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Robot {
    @Id
    private final UUID id = UUID.randomUUID();

    @ElementCollection( fetch = FetchType.EAGER )
    private final List<Capability> capabilities = Capability.allBaseCapabilities();

    private static Robot withBaseCapabilities() {
        return new Robot();
    }

}
