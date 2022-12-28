package thkoeln.dungeon.monte.domainprimitives;

import lombok.*;

import javax.persistence.Embeddable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static thkoeln.dungeon.monte.domainprimitives.MineableResourceType.GEM;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
@Getter
@EqualsAndHashCode
@Embeddable
public class MineableResource {
    private MineableResourceType type;
    private Integer amount;

    public static MineableResource fromTypeAndAmount( MineableResourceType mineableResourceType, Integer amount ) {
        if ( mineableResourceType == null ) throw new DomainPrimitiveException( "MineableResourceType cannot be null!" );
        if ( amount == null ) throw new DomainPrimitiveException( "Amount cannot be null!" );
        if ( amount <= 0 ) throw new DomainPrimitiveException( "Amount must be > 0!" );
        return new MineableResource( mineableResourceType, amount );
    }

    @Override
    public String toString() {
        String keyString = getType().toString().substring( 0, 1 );
        if ( getType() == GEM ) keyString = "J"; // avoid confusion with G for gold
        if ( amount >= 999000 ) return keyString + "999";
        if ( amount < 10 ) return keyString + ".01";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols( Locale.US );
        DecimalFormat df = new DecimalFormat(".00", symbols );
        if ( amount < 1000 ) return keyString + df.format( ((float) amount) / 1000.0 );

        df = new DecimalFormat("0.0", symbols );
        if ( amount < 10000 ) return keyString + df.format( ((float) amount) / 1000.0 );

        // >= 10000
        return keyString + String.format( "%1$3s", amount / 1000 );
    }
}
