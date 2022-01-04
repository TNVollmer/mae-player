package thkoeln.dungeon.player.domain;

public enum PlayerMode {
    SINGLE, MULTI;

    public boolean isSingle() {
        return this.equals( SINGLE );
    }
}
