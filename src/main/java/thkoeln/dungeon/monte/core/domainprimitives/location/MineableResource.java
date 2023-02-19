package thkoeln.dungeon.monte.core.domainprimitives.location;

import lombok.*;
import thkoeln.dungeon.monte.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.monte.printer.printables.MineableResourcePrintable;

import javax.persistence.Embeddable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static thkoeln.dungeon.monte.core.domainprimitives.location.MineableResourceType.GEM;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
@Getter
@EqualsAndHashCode
@Embeddable
public class MineableResource implements MineableResourcePrintable {
    private MineableResourceType type;
    private Integer amount;


    public static MineableResource fromTypeAndAmount( MineableResourceType mineableResourceType, Integer amount ) {
        if ( mineableResourceType == null ) throw new DomainPrimitiveException( "MineableResourceType cannot be null!" );
        if ( amount == null ) throw new DomainPrimitiveException( "Amount cannot be null!" );
        if ( amount <= 0 ) throw new DomainPrimitiveException( "Amount must be > 0!" );
        return new MineableResource( mineableResourceType, amount );
    }


    /**
     * @return The short name of a mineable resource located on this planet. Name is <= 4 chars, for layout reasons.
     */
    @Override
    public String mapName() {
        Character key = key();
        if ( amount >= 999000 ) return key + "999";
        if ( amount < 10 ) return key + ".01";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols( Locale.US );
        DecimalFormat df = new DecimalFormat(".00", symbols );
        if ( amount < 1000 ) return key + df.format( ((float) amount) / 1000.0 );

        df = new DecimalFormat("0.0", symbols );
        if ( amount < 10000 ) return key + df.format( ((float) amount) / 1000.0 );

        // >= 10000
        return key + String.format( "%1$3s", amount / 1000 );
    }


    public boolean isEmpty() {
        return ( amount == 0 );
    }

    /**
     * @return the key for this mineral: C=COAL, I=IRON, J=GEM, G=GOLD, P=PLATIN
     */
    public Character key() {
        if ( getType() == GEM ) return 'J'; // avoid confusion with G for gold
        return getType().toString().charAt( 0 );
    }


    /**
     * @return The relative value of a mineable resource, as an int value between 1 and 5
     */
    @Override
    public int relativeValue() {
        return type.ordinal() + 1;
    }


    /**
     * @return Detailed description of a printable entity - should fit in one line, but no constraints otherwise.
     */
    @Override
    public String detailedDescription() {
        // for simplicity reasons ...
        return mapName();
    }


    @Override
    public String toString() {
        return mapName();
    }
}
