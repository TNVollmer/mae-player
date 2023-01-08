package thkoeln.dungeon.monte.core.util;

public interface Printer {
    public enum Color { YELLOW, RED, BLUE, GREEN, YELLOW_BRIGHT, RED_BRIGHT, BLUE_BRIGHT, GREEN_BRIGHT };

    public void initializeOutput();

    public void header( String string );

    public void startBulletList();
    public void endBulletList();
    public void writeBulletItem( String string );

    public void startLine();
    public void startLineIndent();
    public void write( String string );
    public void endLine();

    public void startColorFont( Color color );
    public void endColorFont();
    public void startColorBackground( Color color );
    public void endColorBackground();

    public void startTable( int numOfColumns );
    public void endTable();
    public void tableHeaderRow();
    public void startRow( int rowNumber, int numOfCompartments );
    public void writeCell ( String ... compartmentStrings );
    public void endRow();

    public void flush();
}
