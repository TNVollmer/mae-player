package thkoeln.dungeon.player.core.domainprimitives.status;

import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
@Getter
@EqualsAndHashCode
@Embeddable
public class Energy {
    private Integer energyAmount;

    public static Energy from(Integer amount ) {
        if ( amount == null ) throw new DomainPrimitiveException( "Amount cannot be null!" );
        if ( amount < 0 ) throw new DomainPrimitiveException( "Amount must be >= 0!" );
        return new Energy( amount );
    }

    public static Energy zero() {
        return from( 0 );
    }

    public static Energy defaultMovementDifficulty() {
        return new Energy( 1 );
    }

    public static Energy initialRobotEnergy() {
        return new Energy( 20 );
    }

    public Energy decreaseBy( Energy energy ) {
        if ( energy == null ) throw new DomainPrimitiveException( "decrease by null" );
        if ( energy.greaterThan( this ) ) throw new DomainPrimitiveException( "negative energy not allowed" );
        return Energy.from( energyAmount - energy.energyAmount);
    }


    public Energy increaseBy( Energy energy ) {
        if ( energy == null ) throw new DomainPrimitiveException( "increase by null" );
        return Energy.from( energyAmount + energy.energyAmount);
    }

    public boolean greaterThan( Energy energy ) {
        if ( energy == null ) throw new DomainPrimitiveException( "> null not defined" );
        return ( energyAmount > energy.energyAmount);
    }


    public boolean greaterEqualThan( Energy energy ) {
        if ( energy == null ) throw new DomainPrimitiveException( ">= null not defined" );
        return ( energyAmount >= energy.energyAmount);
    }


    public boolean lowerThanPercentage(int percentage, Energy comparisonEnergy ) {
        if ( percentage < 0 || percentage > 100 || comparisonEnergy == null )
            throw new DomainPrimitiveException( "percentage < 0 || percentage > 100 || comparisonEnergy == null" );
        float fraction = (float)comparisonEnergy.energyAmount * (float)percentage / 100f;
        return ( (float)energyAmount < fraction );
    }

    @Override
    public String toString() {
        return energyAmount + "E";
    }
}
