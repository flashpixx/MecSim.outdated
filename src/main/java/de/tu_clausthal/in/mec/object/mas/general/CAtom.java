package de.tu_clausthal.in.mec.object.mas.general;

/**
 * Created by marcel on 03.06.15.
 */
public class CAtom<T> implements IAtom<T>
{
    private final T m_value;

    private Class<T> m_type;

    public CAtom(final T p_value)
    {
        m_value = p_value;
    }

    @Override
    public T get()
    {
        return m_value;
    }

    @Override
    public boolean instanceOf(Class<?> p_class)
    {
        return m_type.isAssignableFrom(p_class);
    }

    @Override
    public final int hashCode()
    {
        return m_value.hashCode();
    }

    @Override
    public final boolean equals(final Object p_object)
    {
        return this.hashCode() == p_object.hashCode();
    }

}
