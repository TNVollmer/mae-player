package thkoeln.dungeon.monte.core.domainprimitives.location;

import lombok.*;
import thkoeln.dungeon.monte.core.domainprimitives.DomainPrimitiveException;

import javax.persistence.Embeddable;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
public class MovementDifficulty {
    private Integer difficulty = 1;

    public static MovementDifficulty fromInteger( Integer difficulty ) {
        if ( difficulty == null ) throw new DomainPrimitiveException( "Difficulty cannot be null!" );
        if ( difficulty < 1 ) throw new DomainPrimitiveException( "Difficulty must be >= 1!" );
        if ( difficulty > 3 ) throw new DomainPrimitiveException( "Difficulty must be <= 3!" );
        return new MovementDifficulty( difficulty );
    }
}
