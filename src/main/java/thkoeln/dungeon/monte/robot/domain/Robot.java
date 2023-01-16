package thkoeln.dungeon.monte.robot.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.strategy.AccountInformation;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.printer.printables.RobotPrintable;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

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

    @Enumerated( EnumType.STRING )
    private RobotType type;
    @Embedded
    private Command recentCommand;

    boolean alive = true;

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

    public static Robot of( UUID robotId ) {
        return of( robotId, null, null, null );
    }


    public void verifyAndIfNeededUpdate( Planet updatedLocation, Energy updatedEnergy ) {
        logger.debug( "Verify that robot " + this + " is really on planet " + updatedLocation +
                ", with energy level " + updatedEnergy + "..." );
        if ( updatedLocation == null || !updatedLocation.equals( location ) ) {
            logger.warn( "Robot " + this + " should be on planet " + updatedLocation +
                    ", but actually is on planet " + location + "!" );
            moveToPlanet( updatedLocation );
        }
        if ( updatedEnergy == null || !updatedEnergy.equals( updatedEnergy ) ) {
            logger.warn( "Robot " + this + " should have " + updatedEnergy + ", but actually has " + energy + "!" );
            setEnergy( updatedEnergy );
        }
    }

    public void moveToPlanet( Planet newPlanet ) {
        if ( newPlanet == null ) throw new RobotException( "newPlanet == null" );
        setLocation( newPlanet );
        newPlanet.setVisited( true );
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
        return null;
    }


    @Override
    public Command move() {
        if ( location == null ) {
            logger.error( "Robot wants to createMove, but planet is null ???" );
            return null;
        }
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
        String printString = toString();
        if ( location != null ) printString += " on " + location;
        return printString;
    }


    @Override
    public String mapName() {
        String printString = ( type != null ) ? type.toString() : "Robot";
        printString = printString.substring( 0, 1 );
        printString += String.valueOf( robotId ).substring( 0, 3 );
        return printString;
    }

    @Override
    public String toString() {
        return mapName();
    }
}
