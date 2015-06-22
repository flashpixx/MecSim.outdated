package de.tu_clausthal.in.mec.object.mas.general;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.jason.general.CLiteral;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.junit.Test;


/**
 * Created by marcel on 22.06.15.
 */
public class Test_IDefaultBeliefBase
{
    @Test
    public void testBeliefbaseGetter( )
    {
        final IBeliefBase<Literal> l_beliefbase = new IDefaultBeliefBase<Literal>(  ){};

        l_beliefbase.add( CPath.EMPTY, new CLiteral(
                                  ASSyntax.createLiteral( "test1" )
                          ) );
        l_beliefbase.add( CPath.EMPTY, new CLiteral(
                                  ASSyntax.createLiteral( "test2" )
                          ) );
        l_beliefbase.add( CPath.EMPTY, new CLiteral(
                                  ASSyntax.createLiteral( "test3" )
                          ) );

        l_beliefbase.add(
                new CPath( "bb1" ), new IDefaultBeliefBase<Literal>()
                {
                }
        );

        l_beliefbase.add( "bb1", new CLiteral( ASSyntax.createLiteral( "test4" ) ) );

        l_beliefbase.getOrDefault( new CPath( "bb2" ), new IDefaultBeliefBase<Literal>() { } );

        l_beliefbase.getOrDefault( new CPath( "bb2/bb3/bb4/bb5" ), new IDefaultBeliefBase<Literal>() { } );

        l_beliefbase.add( new CPath( "bb2/bb3/bb4/bb5" ), new CLiteral( ASSyntax.createLiteral( "test5" ) ) );

        System.out.println( l_beliefbase.getLiterals( new CPath( "bb2/bb3/bb4/bb5" ) ) );

        System.out.println( l_beliefbase );
    }
}
