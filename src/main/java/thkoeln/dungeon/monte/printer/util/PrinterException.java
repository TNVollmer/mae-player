package thkoeln.dungeon.monte.printer.util;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class PrinterException extends DungeonPlayerRuntimeException {
    public PrinterException(String message ) {
        super( message );
    }
    public PrinterException(Exception e ) {
        super( e.getMessage() );
    }
}
