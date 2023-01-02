package thkoeln.dungeon.monte.core.strategy;

import thkoeln.dungeon.monte.core.domainprimitives.command.Command;

import java.lang.reflect.Method;

public abstract class AbstractStrategy {
    public abstract String[] commandCreatorMethodNames();

    public Command decideNextCommand( Behavior behavior, AccountInformation accountInformation ) {
        // first try to find the method without a parameter, then (if that fails) assume that the
        // action requires a TradingAccount.
        Method method = null;
        Method methodWithTradingAccount = null;
        Command command = null;
        for ( String methodName : commandCreatorMethodNames() ) {
            try {
                method = behavior.getClass().getMethod( methodName );
            }
            catch ( NoSuchMethodException noSuchMethodException1 ) {
                methodWithTradingAccount =
                        findCommandCreatorMethodWithTradingAccount( behavior, methodName );
            }
            try {
                if ( method != null ) command = (Command) method.invoke( behavior );
                if ( command == null && methodWithTradingAccount != null )
                    command = (Command) methodWithTradingAccount.invoke( behavior, accountInformation );
                if ( command != null ) return command;
            }
            catch ( Exception whateverWentWrongShouldntHaveHappenedAnyway ) {
                throw new StrategyException( whateverWentWrongShouldntHaveHappenedAnyway );
            }
        }
        // when come to this place, it has become clear that no command was found.
        return null;
    };


    private Method findCommandCreatorMethodWithTradingAccount( Behavior behavior, String methodName ) {
        try {
            Method method = behavior.getClass().getMethod( methodName, AccountInformation.class );
            return method;
        }
        catch ( NoSuchMethodException noSuchMethodException ) {
            throw new StrategyException( "Cannot find method " + methodName + " on " + behavior.getClass().getSimpleName() );
        }
    }
}
