/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.ui.web;

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.MimeUtil2;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.io.FilenameUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;


/**
 * class for the HTTP server *
 */
public class CServer extends NanoHTTPD
{

    /**
     * markdown processor *
     */
    protected final PegDownProcessor m_markdown = new PegDownProcessor( Extensions.ALL );
    /**
     * virtual-locations
     */
    protected final CVirtualLocation m_virtuallocation;


    /**
     * ctor - starts the HTTP server
     *
     * @param p_host    bind hostname
     * @param p_port    bind port
     * @param p_default default location
     */
    public CServer( final String p_host, final int p_port, final CVirtualDirectory p_default )
    {
        super( p_host, p_port );
        m_virtuallocation = new CVirtualLocation( p_default );

        for ( String l_detector : new String[]{"eu.medsea.mimeutil.detector.MagicMimeMimeDetector", "eu.medsea.mimeutil.detector.ExtensionMimeDetector", "eu.medsea.mimeutil.detector.OpendesktopMimeDetector", "eu.medsea.mimeutil.detector.WindowsRegistryMimeDetector"} )
            MimeUtil.registerMimeDetector( l_detector );

        try
        {
            this.start();
        }
        catch ( IOException l_exception )
        {
            CLogger.error( l_exception );
        }

        CBootstrap.afterServerInit( this );
    }


    @Override
    public final Response serve( final IHTTPSession p_session )
    {
        Response l_response;
        try
        {
            // mime-type will be read by the file-extension
            final IVirtualLocation l_location = m_virtuallocation.get( p_session.getUri() );
            final URL l_physicalfile = l_location.getFile( p_session.getUri() );
            final String l_mimetype = this.getMimeType( l_physicalfile );

            switch ( FilenameUtils.getExtension( l_physicalfile.toString() ) )
            {
                case "htm":
                case "html":
                    l_response = new Response( Response.Status.OK, l_mimetype + "; charset=" + ( new InputStreamReader( l_physicalfile.openStream() ).getEncoding() ), l_physicalfile.openStream() );
                    break;

                case "md":
                    if ( l_location.getMarkDownRenderer() != null )
                        l_response = new Response( Response.Status.OK, "application/xhtml+xml; charset=utf-8", l_location.getMarkDownRenderer().getHTML( m_markdown, l_physicalfile ) );
                    else
                        l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
                    break;

                default:
                    l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
            }

        }
        catch ( IOException l_exception )
        {
            CLogger.error( l_exception );
            l_response = new Response( Response.Status.INTERNAL_ERROR, "text/plain", "ERROR 500\n" + l_exception );
        }

        l_response.addHeader( "Location", p_session.getUri() );
        l_response.addHeader( "Expires", "-1" );
        return l_response;
    }


    /**
     * returns the virtual-location object
     *
     * @return location
     */
    public CVirtualLocation getVirtualLocation()
    {
        return m_virtuallocation;
    }


    /**
     * reads the mime-type of an URL - first try to detect a mime-type which has no "application" prefix
     */
    private String getMimeType( final URL p_url )
    {
        for ( Object l_item : MimeUtil.getMimeTypes( p_url ) )
        {
            final MimeType l_type = (MimeType) l_item;
            if ( !l_type.toString().startsWith( "application/" ) )
                return l_type.toString();
        }

        return MimeUtil2.UNKNOWN_MIME_TYPE.toString();
    }


    /**
     * register an object for the UI
     *
     * param p_object object, all methods with the name "ui_" are registered
     */
    public void addActionObject( final Object p_object )
    {
        final Map<String, CReflection.CMethod> l_methods = CReflection.getClassMethods( p_object.getClass(), new CReflection.IMethodFilter()
        {
            @Override
            public boolean filter( final java.lang.reflect.Method p_method )
            {
                return p_method.getName().startsWith( "ui_static_" ) || p_method.getName().startsWith( "ui_dynamic_" );
            }
        } );
    }

}
