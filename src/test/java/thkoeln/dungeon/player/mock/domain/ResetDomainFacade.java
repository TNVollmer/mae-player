package thkoeln.dungeon.player.mock.domain;

public interface ResetDomainFacade {

    /**
     * Bring the application into a clean state. Remove all entities in the database. Reset all state variables to their
     * original state (if there are any). Basically, perform a cleanup.
     */
    public void resetEverything();

    /**
     * The same as the above, just without cleaning up the player entity / entities inside the player table
     * of the database.
     */
    public void resetEverythingExceptPlayer();

}
