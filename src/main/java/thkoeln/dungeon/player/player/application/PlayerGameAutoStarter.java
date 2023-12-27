package thkoeln.dungeon.player.player.application;

/**
 * This interface is used to start the game in Dev Mode. For the production environment, the game is started
 * externally by the game admin - there we'll just have an empty implementation of this interface. In Dev Mode,
 * we'll have a real implementation of this interface that starts the game automatically.
 */
public interface PlayerGameAutoStarter {
    void startGame();
}
