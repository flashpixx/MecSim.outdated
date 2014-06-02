/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.objects.norms;


import de.tu_clausthal.in.winf.mas.norm.INorm;
import de.tu_clausthal.in.winf.mas.norm.INormCheckResult;
import de.tu_clausthal.in.winf.mas.norm.INormCollection;

import java.util.*;

/**
 * default norm collection
 */
public class CDefaultNormCollection<T extends INorm> implements INormCollection<T> {

    private HashSet<T> m_norms = new HashSet();


    @Override
    public Map<T, INormCheckResult> match(T p_object) {
        HashMap<T, INormCheckResult> l_map = new HashMap();

        for (T l_norm : m_norms) {
            INormCheckResult l_check = l_norm.check(p_object);
            if ((l_check.getResult() instanceof Boolean) && ((Boolean) l_check.getResult()))
                l_map.put(l_norm, l_check);
        }

        return l_map;
    }

    @Override
    public int size() {
        return m_norms.size();
    }

    @Override
    public boolean isEmpty() {
        return m_norms.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return m_norms.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return m_norms.iterator();
    }

    @Override
    public Object[] toArray() {
        return m_norms.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return m_norms.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return m_norms.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return m_norms.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return m_norms.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return m_norms.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return m_norms.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return m_norms.retainAll(c);
    }

    @Override
    public void clear() {
        m_norms.clear();
    }
}
