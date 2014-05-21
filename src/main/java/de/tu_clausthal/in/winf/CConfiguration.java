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

package de.tu_clausthal.in.winf;

import com.google.gson.Gson;
import org.jxmapviewer.viewer.GeoPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 * singleton class for configuration with a Json file *
 */
public class CConfiguration {

    /**
     * name of the configuration file *
     */
    private static final String s_ConfigFilename = "config.json";
    /**
     * singleton instance variable *
     */
    private static CConfiguration s_instance = new CConfiguration();
    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * property that stores the configuration data *
     */
    private Data m_data = new Data();
    /**
     * directory of the configuration file *
     */
    private File m_dir = new File(System.getProperty("user.home") + File.separator + ".tucwinf");

    /**
     * private Ctor to avoid manual instantiation *
     */
    private CConfiguration() {
    }

    /**
     * singleton get instance method
     *
     * @return configuration instance
     */
    public static CConfiguration getInstance() {
        return s_instance;
    }

    /**
     * write method of the configuration *
     */
    public void write() {
        try {
            if (!m_dir.exists() && !m_dir.mkdirs())
                throw new IOException("unable to create " + m_dir.getAbsolutePath());

            Writer l_writer = new OutputStreamWriter(new FileOutputStream(m_dir + File.separator + s_ConfigFilename), "UTF-8");

            Gson l_gson = new Gson();
            l_gson.toJson(m_data, l_writer);

            l_writer.close();
        } catch (Exception l_exception) {
            m_Logger.error(l_exception.getMessage());
        }
    }

    /**
     * reads the configuration within the directory *
     */
    public void read() {
        Data l_tmp = null;
        try {
            String l_config = m_dir + File.separator + s_ConfigFilename;
            m_Logger.info("read configuration from [" + l_config + "]");

            Reader l_reader = new InputStreamReader(new FileInputStream(l_config), "UTF-8");

            Gson l_gson = new Gson();
            l_tmp = l_gson.fromJson(l_reader, Data.class);
        } catch (Exception l_exception) {
            m_Logger.error(l_exception.getMessage());
        }

        if (l_tmp == null)
            m_Logger.warn("configuration is null, use default configuration");
        else {
            if (l_tmp.ViewPoint == null) {
                m_Logger.warn("view point uses default value");
                l_tmp.ViewPoint = m_data.ViewPoint;
            }
            if (l_tmp.WindowHeight < 100) {
                m_Logger.warn("window height uses default value");
                l_tmp.WindowHeight = m_data.WindowHeight;
            }
            if (l_tmp.WindowWidth < 100) {
                m_Logger.warn("window width uses default value");
                l_tmp.WindowWidth = m_data.WindowWidth;
            }
            if ((l_tmp.RoutingAlgorithm == null) || (l_tmp.RoutingAlgorithm.isEmpty())) {
                m_Logger.warn("routing algorithm uses default value");
                l_tmp.RoutingAlgorithm = m_data.RoutingAlgorithm;
            }
            if (l_tmp.CellSampling < 1) {
                m_Logger.warn("cell sampling uses default value");
                l_tmp.CellSampling = m_data.CellSampling;
            }
            if (l_tmp.ThreadSleepTime < 0) {
                m_Logger.warn("thread sleep time uses default value");
                l_tmp.ThreadSleepTime = m_data.ThreadSleepTime;
            }

            m_data = l_tmp;
        }

        // set always static values (we need always 2 threads)
        m_data.MaxThreadNumber = Runtime.getRuntime().availableProcessors() + 1;
    }

    /**
     * returns the config dir
     *
     * @return path to config dir
     */
    public File getConfigDir() {
        return m_dir;
    }

    /**
     * sets the config dir
     *
     * @param p_dir directory
     */
    public void setConfigDir(File p_dir) {
        m_dir = p_dir;
    }

    /**
     * returns the configuration data
     *
     * @return returns the configuration data
     */
    public Data get() {
        return m_data;
    }

    /**
     * private class for storing the configuration *
     */
    public class Data {

        /**
         * geo position object of the start viewpoint *
         */
        public GeoPosition ViewPoint = new GeoPosition(51.8089, 10.3412);
        /**
         * zoom level of the viewpoint on the start point *
         */
        public int Zoom = 4;
        /**
         * number of thread which can run in parallel *
         */
        public int MaxThreadNumber = 0;
        /**
         * window width *
         */
        public int WindowWidth = 1024;
        /**
         * window height *
         */
        public int WindowHeight = 1024;
        /**
         * cell size for sampling *
         */
        public int CellSampling = 2;
        /**
         * thread sleep time in miliseconds *
         */
        public int ThreadSleepTime = 100;
        /**
         * geo map for graph *
         */
        public String RoutingMap = "europe/germany/niedersachsen";
        /**
         * graph algorithm: astar (A* algorithm, default), astarbi (bidirectional A*) dijkstra (Dijkstra), dijkstrabi and dijkstraNativebi (a bit faster bidirectional Dijkstra)  *
         */
        public String RoutingAlgorithm = "astarbi";

    }

}
