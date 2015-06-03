package de.tu_clausthal.in.mec.object.mas.jason.general;

import de.tu_clausthal.in.mec.object.mas.general.*;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;

import java.util.List;

/**
 * Created by marcel on 02.06.15.
 */
public class CLiteral implements ILiteral
{
    private final String m_functor;
    private final ITermList m_values = new CTermList();
    private final ITermSet m_annotations = new CTermSet();

    public CLiteral(final String p_functor )
    {
        m_functor = p_functor;
    }

    public void addValue( final ListTerm p_listTerm )
    {
        for( final Term l_term : p_listTerm )
            m_values.add(CCommon.convertGeneric(l_term));
    }

    public void addValue( final List<Term> p_listTerm )
    {
        m_values.addAll(CCommon.convertGeneric(p_listTerm));
    }

    public void addValue( final Term p_term )
    {
        m_values.add(CCommon.convertGeneric(p_term));
    }

    public void addAnnotation( final ListTerm p_listTerm )
    {
        for( final Term l_term : p_listTerm )
            m_annotations.add(CCommon.convertGeneric(l_term));
    }

    public void addAnnotation( final Term p_term )
    {
        m_annotations.add(CCommon.convertGeneric(p_term));
    }

    @Override
    public ITermSet getAnnotation()
    {
        return m_annotations;
    }

    @Override
    public IAtom<?> getFunctor()
    {
        return new IAtom<String>()
        {

            @Override
            public boolean instanceOf(Class<?> p_class)
            {
                return String.class.isAssignableFrom( p_class );
            }

            @Override
            public String get()
            {
                return m_functor;
            }
        };
    }

    @Override
    public ITermList getValues()
    {
        return null;
    }

    @Override
    public boolean instanceOf(Class<?> p_class)
    {
        return false;
    }
}
