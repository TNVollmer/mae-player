package thkoeln.dungeon.monte.robot.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.strategy.AccountInformation;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.printer.printables.RobotPrintable;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static thkoeln.dungeon.monte.core.domainprimitives.location.MineableResourceType.COAL;

@Entity
@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Robot implements ActionableRobot, RobotPrintable {
    @Transient
    private Logger logger = LoggerFactory.getLogger( Robot.class );

    @Id
    private final UUID id = UUID.randomUUID();

    // The ID assigned by the Robot service!
    private UUID robotId;

    // Game- and playerId is stored for convenience - you need this for creating commands.
    @Column( name = "convenience_game_id" )
    private UUID gameId;
    @Column( name = "convenience_player_id" )
    private UUID playerId;

    @Embedded
    @AttributeOverride(name = "energyAmount", column = @Column(name = "energy_amount"))
    private Energy energy;
    @Embedded
    @AttributeOverride(name = "energyAmount", column = @Column(name = "max_energy_amount"))
    private Energy maxEnergy;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "load_amount"))
    @AttributeOverride(name = "type", column = @Column(name = "load_type"))
    private MineableResource load;

    @Enumerated( EnumType.STRING )
    private RobotType type;
    @Embedded
    private Command recentCommand;

    private boolean alive = true;
    private Character enemyChar = null;

    @Transient
    AbstractRobotStrategy strategy;

    @ElementCollection( fetch = FetchType.EAGER )
    @Getter ( AccessLevel.PROTECTED )
    private final List<Capability> capabilities = Capability.allBaseCapabilities();

    @ManyToOne
    @Setter ( AccessLevel.PROTECTED )
    private Planet location;

    public static Robot of( UUID robotId, RobotType type, UUID gameId, UUID playerId ) {
        if ( robotId == null ) throw new RobotException( "robotId == null" );
        Robot robot = new Robot();
        robot.setRobotId( robotId );
        robot.type = type;
        robot.setGameId( gameId );
        robot.setPlayerId( playerId );
        robot.setEnergy( Energy.initialRobotEnergy() );
        robot.setMaxEnergy( Energy.initialRobotEnergy() );
        return robot;
    }

    public static Robot of( UUID robotId, UUID gameId ) {
        return of( robotId, null, gameId, null );
    }



    /**
     * @return true if this robot is an enemy robot, false if it is one of us
     */
    @Override
    public boolean isEnemy() {
        return ( enemyChar != null );
    }


    /**
     * Enemy players are identified by a capital char (A, B, ...), which is used for color coding in the client.
     * @return the char belonging to the robot's player, if it is an enemy robot. Null if it is our own robot.
     */
    public Character enemyChar() {
        return enemyChar;
    }


    public void verifyAndIfNeededUpdate( Planet updatedLocation, Energy updatedEnergy ) {
        logger.debug( "Verify that robot " + this + " is really on planet " + updatedLocation +
                ", with energy level " + updatedEnergy + "..." );
        if ( updatedLocation == null || !updatedLocation.equals( location ) ) {
            logger.warn( "Robot " + this + " should be on planet " + updatedLocation +
                    ", but actually is on planet " + location + "!" );
            moveToPlanet( updatedLocation );
        }
        if ( updatedEnergy != null && !updatedEnergy.equals( this.energy ) ) {
            logger.warn( "Robot " + this + " should have " + updatedEnergy + ", but actually has " + energy + "!" );
            setEnergy( updatedEnergy );
        }
    }

    public void moveToPlanet( Planet newPlanet ) {
        if ( newPlanet == null ) throw new RobotException( "newPlanet == null" );
        setLocation( newPlanet );
        newPlanet.setVisited( true );
    }


    public void updateEnergy( Energy newEnergy ) {
        // since this comes from an event, warn if inconsistent
        if ( !energy.equals( newEnergy ) )
            logger.warn( this + ": I thought I had " + energy + ", but actually event tells me " + newEnergy );
        setEnergy( newEnergy );
        logger.info( this + ": Set energy to " + newEnergy );
    }


    public void updateInventoryAfterMining( MineableResource minedResource, MineableResource updatedInventory ) {
        if ( minedResource == null || updatedInventory == null )
            throw new RobotException( "minedResource == null || updatedInventory == null" );
        if ( !minedResource.getType().equals( COAL ) ) throw new RobotException( "minedResource is not coal" );
        if ( load == null ) {
            setLoad( minedResource );
        }
        else {
            setLoad( load.add( minedResource ) );
        }
        if ( !load.equals( updatedInventory ) )
            logger.warn( this + ": I thought I had " + load + ", but actually event tells me " + updatedInventory );
        logger.info( this + ": Updated inventory after mining: " + load );
    }


    @Override
    public Command decideNextCommand( AccountInformation accountInformation ) {
        Command nextCommand = null;
        if ( strategy == null ) {
            logger.error( "No strategy set for robot " + this + ", can't decide on a command." );
        }
        else {
            nextCommand = strategy.findNextCommand( this, accountInformation );
            logger.info( "Decided on command " + nextCommand + " for robot " + this );
        }
        setRecentCommand( nextCommand );
        return nextCommand;
    }


    @Override
    public Command regenerateIfLowAndNotAttacked() {
        if ( energy.lowerThanPercentage( 20, maxEnergy ) ) {
            Command command = Command.createRegeneration( robotId, gameId, playerId );
            return command;
        }
        return null;
    }


    @Override
    public Command fleeIfAttacked() {
        return null;
    }


    @Override
    public Command mineIfNotMinedLastRound() {
        return null;
    }


    @Override
    public Command mine() {
        if ( location == null ) throw new RobotException( "mine: location == null" );
        MineableResource resource = location.getMineableResource();
        if ( resource != null && resource.getType() == COAL ) {
            Command command = Command.createMining( robotId, location.getPlanetId(), gameId, playerId );
            return command;
        }
        return null;
    }


    @Override
    public Command sellMineableResources() {
        if ( load != null && load.getAmount() >= 10 ) {
            Command command = Command.createSelling( robotId, gameId, playerId, load );
            setLoad( null );
            return command;
        }
        return null;
    }

    @Override
    public Command moveRandomlyToUnexploredPlanet() {
        if ( location == null ) throw new RobotException( "moveRandomlyToUnexploredPlanet: location == null" );
        if ( energy.greaterEqualThan( location.getMovementDifficulty() ) ) {
            Planet target = location.findUnvisitedNeighbourOrAnyIfAllVisited();
            if ( target == null ) return null;
            Command command = Command.createMove( robotId, target.getPlanetId(), gameId, playerId );
            setEnergy( energy.decreaseBy( location.getMovementDifficulty() ) );
            moveToPlanet( target );
            return command;
        }
        // not sufficient energy to createMove => no command
        return null;
    }


    @Override
    public Command moveRandomly() {
        // todo: implement, this is just a placeholder
        return moveRandomlyToUnexploredPlanet();
    }


    @Override
    public Command moveIfNotOnFittingResource() {
        if ( location == null ) throw new RobotException( "moveIfNotOnFittingResource: location == null" );
        MineableResource resource = location.getMineableResource();
        if ( resource == null || resource.getType() != COAL ) return moveRandomlyToUnexploredPlanet();
        return null;
    }


    @Override
    public Command moveIfOptimalResourceNearby() {
        // todo: implement, this is just a placeholder
        return moveRandomlyToUnexploredPlanet();
    }


    @Override
    public Command moveIfOpponentNearby() {
        // todo: implement, this is just a placeholder
        return moveRandomlyToUnexploredPlanet();
    }


    @Override
    public Command upgrade(AccountInformation accountInformation) {
        return null;
    }


    @Override
    public Command attack() {
        return null;
    }


    @Override
    public Command regenerate() {
        Command command = Command.createRegeneration( robotId, gameId, playerId );
        return command;
    }


    @Override
    public String detailedDescription() {
        StringBuffer stringBuffer = new StringBuffer( mapName() );
        stringBuffer.append( " on " ).append( location );
        stringBuffer.append( " (" ).append( energy );
        if ( load != null ) stringBuffer.append( ", " ).append( load );
        stringBuffer.append( ")" );
        if ( recentCommand != null ) stringBuffer.append( ". Last command: " ).append( recentCommand );
        return stringBuffer.toString();
    }


    @Override
    public String mapName() {
        String printString = firstLetter();
        printString += String.valueOf( robotId ).substring( 0, 3 );
        return printString;
    }

    private String firstLetter() {
        if ( isEnemy() ) return String.valueOf( enemyChar );
        String printString = ( type != null ) ? type.toString() : "Robot";
        return printString.substring( 0, 1 );
    }


    @Override
    public String toString() {
        return mapName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Robot robot)) return false;
        return id.equals(robot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
