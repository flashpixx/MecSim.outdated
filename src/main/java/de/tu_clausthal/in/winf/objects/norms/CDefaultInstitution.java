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

import de.tu_clausthal.in.winf.mas.norm.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * default institution
 */
public class CDefaultInstitution<T> implements IInstitution<T> {

    /**
     * superior institutions *
     */
    private IInstitutionCollection<T> m_superior = new CDefaultInstitutionCollection();

    /**
     * inferior instituions *
     */
    private IInstitutionCollection<T> m_inferior = new CDefaultInstitutionCollection();

    /**
     * range collection with a disjoint set *
     */
    private IRangeCollection<T> m_range = new CDisjointRangeCollection();

    /**
     * norms of the institution *
     */
    private Set<INorm<T>> m_norms = new HashSet();


    @Override
    public void check(T p_object) {
        if (!m_range.isWithin(p_object))
            return;

        // fuzzy arithmetic with max-norm
        double l_weight = 0;
        boolean l_result = true;
        for (INorm l_item : m_norms) {
            INormCheckResult l_check = l_item.check(p_object);
            l_weight = Math.max(l_weight, l_check.getWeight());

            if (l_check.getResult() instanceof Boolean)
                l_result = l_result && (Boolean) l_check.getResult();
        }

        if ((l_result) && (l_weight >= 0.5)) {

        }
    }

    @Override
    public IRangeCollection<T> getRange() {
        return m_range;
    }

    @Override
    public void update(INorm<T> p_norm) {
        m_norms.add(p_norm);
    }

    @Override
    public void receive(INormMessage<T> p_message) {
        if (p_message.getHops() < 0)
            return;

        p_message.decrementHop();
        switch (p_message.getType()) {
            case Create:
                m_norms.add(p_message.getNorm());
                break;
            case Update:
                m_norms.add(p_message.getNorm());
                break;
            case Delete:
                m_norms.remove(p_message.getNorm());
                break;
        }

        m_superior.send(p_message);
        m_inferior.send(p_message);
    }

    @Override
    public IInstitutionCollection getSuperior() {
        return m_superior;
    }

    @Override
    public void addSuperior(IInstitution<T> p_insitution) {
        m_superior.add(p_insitution);
    }

    @Override
    public void removeSuperior(IInstitution<T> p_insitution) {
        m_superior.remove(p_insitution);
    }

    @Override
    public IInstitutionCollection getInferior() {
        return m_inferior;
    }

    @Override
    public void addInferior(IInstitution<T> p_insitution) {
        m_inferior.add(p_insitution);
    }

    @Override
    public void removeInferior(IInstitution<T> p_insitution) {
        m_inferior.add(p_insitution);
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
    public Iterator<INorm> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return m_norms.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return m_norms.toArray(a);
    }

    @Override
    public boolean add(INorm iNorm) {
        return m_norms.add(iNorm);
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
    public boolean addAll(Collection<? extends INorm> c) {
        return false; //m_norms.addAll(c);
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
