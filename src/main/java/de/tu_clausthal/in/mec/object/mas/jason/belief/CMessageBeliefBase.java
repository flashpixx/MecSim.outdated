/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package de.tu_clausthal.in.mec.object.mas.jason.belief;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.jason.CAgent;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import de.tu_clausthal.in.mec.object.mas.jason.CMessage;
import de.tu_clausthal.in.mec.object.mas.jason.general.CBeliefBase;
import de.tu_clausthal.in.mec.runtime.message.IMessage;
import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Trigger;
import jason.bb.BeliefBase;

import java.util.HashSet;
import java.util.Set;

/**
 * beliefbase structure for message beliefs
 */
public class CMessageBeliefBase extends CBeliefBase
{
    /**
     * set with received messages
     */
    private final Set<IMessage> m_receivedmessages = new HashSet<>();
    /**
     * path seperator
     */
    private final static String c_seperator = "::";

    /**
     * method for registering incoming messages
     *
     * @param p_messages current incoming messages
     */
    public final void receiveMessage(final Set<IMessage> p_messages)
    {
        m_receivedmessages.clear();
        m_receivedmessages.addAll(p_messages);
    }

    /**
     * update literals by receiving new messages
     *
     * @todo integrate transition system
     */
    @Override
    public void update()
    {
        for (final IMessage l_msg : m_receivedmessages)
            try
            {
                // if message is a message from Jason internal message system
                if (l_msg instanceof CMessage)
                {
                    final Message l_jmsg = ((Message) l_msg.getData());
                    final Literal l_literal = (Literal) l_jmsg.getPropCont();
                    l_literal.addAnnot(ASSyntax.createLiteral("source", ASSyntax.createAtom(new CPath(l_jmsg.getSender()).getPath(c_seperator))));

                    if (l_jmsg.isTell())
                        addLiteral(l_literal);
                    if (l_jmsg.isUnTell())
                        removeLiteral( l_literal );
                    if (l_jmsg.isKnownPerformative())
                    {
                        l_literal.addAnnot(BeliefBase.TPercept);
                        /*
                        this.getTS().getC().addEvent(
                                new Event(
                                        new Trigger(
                                                Trigger.TEOperator.add, Trigger.TEType.belief, l_literal
                                        ), Intention.EmptyInt
                                )
                        );
                        */
                    }

                    continue;
                }

                // otherwise message will direct converted
                final Literal l_literal = CCommon.getLiteral(l_msg.getTitle(), l_msg.getData());
                l_literal.addAnnot( ASSyntax.createLiteral("source", ASSyntax.createAtom(new CPath(l_msg.getSource()).getPath(c_seperator))));
                addLiteral(l_literal);

            } catch (final Exception l_exception)
            {
            }
    }
}
