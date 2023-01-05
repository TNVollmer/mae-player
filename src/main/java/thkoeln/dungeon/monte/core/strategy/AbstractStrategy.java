package thkoeln.dungeon.monte.core.strategy;

import thkoeln.dungeon.monte.core.domainprimitives.command.Command;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Generic abstract helper class that allows to select a strategy out of a list of actions.
 */
public abstract class AbstractStrategy {
    public abstract String[] commandCreatorMethodNames();


    public static AccountInformation findNextCommandsForGroup(
            List<? extends Actionable> actionables, AccountInformation accountInformation ) {
        for ( Actionable actionable : actionables ) {
            Command command = actionable.decideNextCommand( accountInformation );
            accountInformation.payForCommand( command );
        }
        return accountInformation;
    }


    /**
     * Generic algorithm to walk through the given list of actions that are supposed to find a
     * command, and then stop when a command has been decided.
     * @param actionable
     * @param accountInformation
     * @return
     */
    public Command findNextCommand( Actionable actionable, AccountInformation accountInformation ) {
        // first try to find the method without a parameter, then (if that fails) assume that the
        // action requires a TradingAccount.
        Method method = null;
        Method methodWithTradingAccount = null;
        Command command = null;
        for ( String methodName : commandCreatorMethodNames() ) {
            try {
                method = actionable.getClass().getMethod( methodName );
            }
            catch ( NoSuchMethodException noSuchMethodException1 ) {
                methodWithTradingAccount =
                        findCommandCreatorMethodWithTradingAccount( actionable, methodName );
            }
            try {
                if ( method != null ) command = (Command) method.invoke( actionable );
                if ( command == null && methodWithTradingAccount != null )
                    command = (Command) methodWithTradingAccount.invoke( actionable, accountInformation );
                if ( command != null ) return command;
            }
            catch ( Exception whateverWentWrongShouldntHaveHappenedAnyway ) {
                throw new StrategyException( whateverWentWrongShouldntHaveHappenedAnyway );
            }
        }
        // when come to this place, it has become clear that no command was found.
        return null;
    };


    private Method findCommandCreatorMethodWithTradingAccount( Actionable actionable, String methodName ) {
        try {
            Method method = actionable.getClass().getMethod( methodName, AccountInformation.class );
            return method;
        }
        catch ( NoSuchMethodException noSuchMethodException ) {
            throw new StrategyException( "Cannot find method " + methodName + " on " +
                    actionable.getClass().getSimpleName() );
        }
    }
}
