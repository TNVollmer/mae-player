package thkoeln.dungeon.monte.printer.printers;

import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.util.MapCoordinate;
import thkoeln.dungeon.monte.printer.util.MapDirection;
import thkoeln.dungeon.monte.printer.util.TwoDimDynamicArray;

import java.util.Map;

/**
 * Helper class to construct a local map around a planet
 */
public class PlanetMapConstructor {
    private PlanetPrintable planetPrintable;

    public PlanetMapConstructor( PlanetPrintable planetPrintable ) {
        this.planetPrintable = planetPrintable;
    }


    /**
     * Create a "local map" with all the planets in reach around this planet. Includes a recursive call to the
     * neighbours.
     */
    public TwoDimDynamicArray<PlanetPrintable> constructLocalClusterMap() {
        TwoDimDynamicArray<PlanetPrintable> localCluster = new TwoDimDynamicArray<>( this.planetPrintable );
        localCluster = addNeighboursToLocalClusterMap( localCluster );
        return localCluster;
    }


    /**
     * Add the neighbours to an existing 2d array of planets - grow the array if needed.
     * @param existingLocalCluster
     * @return
     */
    protected TwoDimDynamicArray<PlanetPrintable> addNeighboursToLocalClusterMap(
            TwoDimDynamicArray<PlanetPrintable> existingLocalCluster ) {
        TwoDimDynamicArray<PlanetPrintable> localCluster = existingLocalCluster;
        Map<MapDirection, PlanetPrintable> allNeighbours = planetPrintable.neighbourMap();
        for ( Map.Entry<MapDirection, PlanetPrintable> entry : allNeighbours.entrySet() ) {
            MapDirection mapDirection = entry.getKey();
            PlanetPrintable neighbour = entry.getValue();
            if ( neighbour != null && !localCluster.contains( neighbour ) ) {
                MapCoordinate myPosition = localCluster.find( planetPrintable );
                localCluster.putAndEnhance( myPosition, mapDirection, neighbour );
                PlanetMapConstructor neighbourConstructor = new PlanetMapConstructor( neighbour );
                localCluster = neighbourConstructor.addNeighboursToLocalClusterMap( localCluster );
            }
        }
        return localCluster;
    }
}
