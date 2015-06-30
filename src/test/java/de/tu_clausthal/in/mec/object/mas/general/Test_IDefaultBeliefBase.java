package de.tu_clausthal.in.mec.object.mas.general;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.jason.general.CLiteral;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


/**
 * test class for generic belief base
 */
public class Test_IDefaultBeliefBase
{
    /**
     * initializes a predefined beliefbase
     *
     * @return predefined beliefbase
     */
    private IBeliefBase<Literal> generateTestset()
    {
        final IBeliefBase<Literal> l_beliefbase = new CDefaultBeliefBase<Literal>(  ){};

        // add some inherited getBeliefbases
        l_beliefbase.add( new CPath( "aa1" ), new CDefaultBeliefBase() );
        l_beliefbase.add( new CPath( "aa2/bb1/cc1/dd1" ), new CDefaultBeliefBase<Literal>(  ){} );

        // add some literals
        l_beliefbase.add( new CPath( "aa1" ), new CLiteral( ASSyntax.createLiteral( "test1" ) ) );
        l_beliefbase.add( new CPath( "aa2/bb1/cc1/dd1" ), new CLiteral( ASSyntax.createLiteral( "test2" ) ) );

        return l_beliefbase;
    }

    @Test
    public void testGetMethods()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // non existing getBeliefbases should return null
        assertEquals( l_beliefbase.get( new CPath( "a" ) ), null );
        assertEquals(l_beliefbase.get(new CPath("xxx/ab")), null);

        final IBeliefBase<Literal> testbase1 = new CDefaultBeliefBase<Literal>(  ){};
        final IBeliefBase<Literal> testbase2 = new CDefaultBeliefBase<Literal>(  ){};

        l_beliefbase.add( new CPath( "x" ), testbase1);
        l_beliefbase.add( new CPath( "xxx/yyy" ), testbase2);

        // check for existing beliefbases
        assertEquals( l_beliefbase.get( new CPath("x") ), testbase1 );
        assertEquals( l_beliefbase.get( new CPath("xxx/yyy") ), testbase2 );

        // non existing literals should return empty set
        assertEquals(l_beliefbase.get(new CPath("x")).getLiterals(), Collections.EMPTY_SET);
        assertEquals( l_beliefbase.get( new CPath( "xxx/yyy" ) ).getLiterals(), Collections.EMPTY_SET );
    }

    @Test
    public void testAddGetBeliefbase()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // create new beliefbase to add
        final IBeliefBase<Literal> l_testBeliefbase = new CDefaultBeliefBase();

        // add to some existing paths
        l_beliefbase.add( new CPath( "aa3" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );

        // add to some non-existing paths
        l_beliefbase.add(new CPath("aa4/bb2/cc3/dd4"), l_testBeliefbase);

        // check for correct addition
        assertEquals( l_beliefbase.get( new CPath( "aa3" ) ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( new CPath( "aa1/bb1" ) ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( new CPath( "aa1/bb1/cc1/dd1" ) ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( new CPath( "aa4/bb2/cc3/dd4" ) ), l_testBeliefbase );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEmptyPath()
    {
        final IBeliefBase<Literal> l_beliefbase = new CDefaultBeliefBase<Literal>(  ){};
        l_beliefbase.add( CPath.EMPTY, new CDefaultBeliefBase() );
    }

    @Test
    public void testAddGetLiteral( )
    {
        // generate testset and literal
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();
        final CLiteral l_testLiteral = new CLiteral( ASSyntax.createLiteral( "test" ) );
        final CLiteral l_testLiteral2 = new CLiteral( ASSyntax.createLiteral( "test2" ) );
        final CLiteral l_testLiteral3 = new CLiteral( ASSyntax.createLiteral( "test3" ) );

        // push literal into top-level set
        l_beliefbase.add( CPath.EMPTY, l_testLiteral );
        l_beliefbase.add( CPath.EMPTY, l_testLiteral2 );

        // push literal into some existing inherited beliefbase
        l_beliefbase.add( new CPath( "aa1" ), l_testLiteral );
        l_beliefbase.add( new CPath( "aa2/bb1" ), l_testLiteral );
        l_beliefbase.add( CPath.EMPTY, new CLiteral( ASSyntax.createLiteral( "aa2/bb1/test2" ) ) );

        // push literal into some non-existing inherited beliefbase
        l_beliefbase.add( new CPath( "aa3" ), l_testLiteral );
        l_beliefbase.add( new CPath( "aa4/bb1/cc1" ), l_testLiteral );
        l_beliefbase.add(CPath.EMPTY, new CLiteral(ASSyntax.createLiteral("xxx/yyy/zzz/test3")));

        // check for correct addition
        assertTrue( l_beliefbase.getLiterals( CPath.EMPTY ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getLiterals( CPath.EMPTY ).contains( l_testLiteral2 ) );
        assertTrue( l_beliefbase.getLiterals( new CPath( "aa1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getLiterals( new CPath( "aa2/bb1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getLiterals( new CPath( "aa3" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getLiterals( new CPath( "aa4/bb1/cc1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getLiterals( new CPath( "aa2/bb1" ) ).contains( l_testLiteral2 ) );
        assertTrue( l_beliefbase.getLiterals( new CPath( "xxx/yyy/zzz" ) ).contains( l_testLiteral3 ) );
    }

    /**
     * @todo remove returns wrong boolean value
     */
    @Test
    public void testRemoveLiteral()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // add some test literals
        final CLiteral l_testLiteral = new CLiteral( ASSyntax.createLiteral( "test" ) );
        l_beliefbase.add( CPath.EMPTY, l_testLiteral);
        l_beliefbase.add(new CPath( "aa1/bb1/cc1/dd1"), l_testLiteral);

        // check if literal exists before removal
        assertTrue(l_beliefbase.get(CPath.EMPTY, l_testLiteral.getFunctor().toString(), ILiteral.class).contains(l_testLiteral));
        assertTrue(l_beliefbase.get(new CPath("aa1/bb1/cc1/dd1"), l_testLiteral.getFunctor().toString(), ILiteral.class ).contains( l_testLiteral )  );

        // remove the literal
        l_beliefbase.remove( CPath.EMPTY, l_testLiteral);
        l_beliefbase.remove(new CPath( "aa1/bb1/cc1/dd1" ), l_testLiteral );

        // check for correct removal
        assertFalse(l_beliefbase.get(CPath.EMPTY, l_testLiteral.getFunctor().toString(), ILiteral.class).contains(l_testLiteral));
        assertFalse(l_beliefbase.get(new CPath("aa1/bb1/cc1/dd1"), l_testLiteral.getFunctor().toString(), ILiteral.class ).contains( l_testLiteral ) );
    }

    @Test
    public void testRemoveBeliefbase()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();
        final IBeliefBase<Literal> l_testBeliefbase = new CDefaultBeliefBase();

        // add to some existing paths
        l_beliefbase.add( new CPath( "aa3" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );

        // add to some non-existing paths
        l_beliefbase.add(new CPath( "aa4/bb2/cc3/dd4" ), l_testBeliefbase);

        // check for correct addition
        assertTrue(l_beliefbase.get(CPath.EMPTY, "aa3", IBeliefBase.class).contains(l_testBeliefbase));
        assertTrue(l_beliefbase.get(new CPath("aa1"), "bb1", IBeliefBase.class).contains(l_testBeliefbase));
        assertTrue(l_beliefbase.get(new CPath("aa1/bb1/cc1"), "dd1", IBeliefBase.class).contains(l_testBeliefbase));
        assertTrue(l_beliefbase.get(new CPath("aa4/bb2/cc3"), "dd4", IBeliefBase.class).contains(l_testBeliefbase));

        l_beliefbase.remove(new CPath("aa3"), l_testBeliefbase);
        l_beliefbase.remove(new CPath("aa1/bb1/cc1/dd1"), l_testBeliefbase);
        l_beliefbase.remove(new CPath("aa1/bb1"), l_testBeliefbase);
        l_beliefbase.remove(new CPath("aa4/bb2/cc3/dd4"), l_testBeliefbase);

        // check for correct removal
        assertTrue(l_beliefbase.get(new CPath("aa3")) == null);
        assertTrue(l_beliefbase.get(new CPath("aa1/bb1/cc1/dd1")) == null);
        assertTrue(l_beliefbase.get(new CPath("aa1/bb1")) == null);
        assertTrue(l_beliefbase.get(new CPath("aa4/bb2/cc3/dd4")) == null);
        assertFalse(l_beliefbase.get(CPath.EMPTY, "aa3", IBeliefBase.class).contains(l_testBeliefbase));
        assertFalse(l_beliefbase.get(new CPath("aa1"), "bb1", IBeliefBase.class).contains(l_testBeliefbase));
        assertFalse(l_beliefbase.get(new CPath("aa1/bb1/cc1"), "dd1", IBeliefBase.class).contains(l_testBeliefbase));
        assertFalse(l_beliefbase.get(new CPath("aa4/bb2/cc3"), "dd4", IBeliefBase.class).contains(l_testBeliefbase));
    }

    @Test
    public void testIterator()
    {
        final IBeliefBase<Literal> l_beliefbase = new CDefaultBeliefBase<Literal>(  ){};
        final Set<ILiteral<Literal>> l_initial = new HashSet(){{
            add( new CLiteral( ASSyntax.createLiteral( "test1" ), new CPath("aa1") ) );
            add( new CLiteral(ASSyntax.createLiteral("test2"), new CPath("aa2/bb1/cc1/dd1") ) );
            add( new CLiteral(ASSyntax.createLiteral("test3"), CPath.EMPTY ) );
            add( new CLiteral(ASSyntax.createLiteral("test4"), CPath.EMPTY ) );
        }};

        l_beliefbase.add( CPath.EMPTY, new CLiteral( ASSyntax.createLiteral( "test3" ) ) );
        l_beliefbase.add( CPath.EMPTY, new CLiteral( ASSyntax.createLiteral( "test4" ) ) );
        l_beliefbase.add( new CPath("aa1"), new CLiteral( ASSyntax.createLiteral("test1")));
        l_beliefbase.add(new CPath("aa2/bb1/cc1/dd1"), new CLiteral( ASSyntax.createLiteral( "test2" ) ) );

        // build literal set with iterator
        final Set<ILiteral<Literal>> l_beliefbaseElements = new HashSet<ILiteral<Literal>>(){{
            for (final ILiteral<Literal> l_element : l_beliefbase)
                add( l_element );
        }};

        // both sets have to be equal
        assertEquals( l_beliefbaseElements, l_initial);
    }

    @Test
    public void testClear()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // check if there are getBeliefbases before removing
        assertTrue(l_beliefbase.get(new CPath( "aa2/bb1/cc1/dd1" ) ) != null);
        assertTrue(l_beliefbase.get( new CPath( "aa2/bb1/cc1" ) ) != null );
        assertTrue(l_beliefbase.get( new CPath( "aa2" ) ) != null );

        // check if there are non existing
        assertTrue(l_beliefbase.get( new CPath( "xxx" ) ) == null);
        assertEquals( l_beliefbase.get( new CPath( "xxx" ) ), null );

        // do clear
        l_beliefbase.clear( new CPath( "aa2/bb1" ) );
        assertFalse( l_beliefbase == null );

        // check if getBeliefbases were removed
        assertTrue( l_beliefbase.get( new CPath( "aa2/bb1/cc1/dd1" ) ) == null );
        assertTrue( l_beliefbase.get( new CPath( "aa2/bb1/cc1" ) ) == null );

        // this beliefbase must not be removed
        assertTrue(l_beliefbase.get( new CPath( "aa2/bb1" ) ) != null);
    }

    @Test
    public void testCollapse()
    {
/*
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        final Set<ILiteral<Literal>> l_collapsed = l_beliefbase.prefixedLiterals( CPath.EMPTY );

        assertTrue( l_collapsed.containsAll(
                            new HashSet<ILiteral<Literal>>(){{
                                add(new CLiteral( ASSyntax.createLiteral( "test1" ) ));
                                add(new CLiteral( ASSyntax.createLiteral( "test2" ) ));
                            }}
                    )
        );
        */
    }
}
