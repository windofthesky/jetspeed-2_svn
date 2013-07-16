/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.cluster;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Node Manager
 *
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id$
 */
public class NodeManagerImpl implements NodeManager,BeanFactoryAware
{

    protected final static Logger log = LoggerFactory.getLogger(NodeManagerImpl.class);

    private static final String NODES_SERIALIZED_FILE_NAME = "nodeInfo.ser";

    /**
     * added support for bean factory to create profile rules
     */
    private BeanFactory beanFactory;

    private Map<String, NodeInformation> nodes = new HashMap<String, NodeInformation>();
    private File rootIndexDir = null;

    /** the default criterion bean name */
    private String nodeInformationBean = "NodeInformation";


    public NodeManagerImpl(String indexRoot, String nodeInformationBean) throws Exception
    {
        //assume it's full path for now
        rootIndexDir = new File(indexRoot);
        this.nodeInformationBean = nodeInformationBean;

        if (!rootIndexDir.isDirectory())
        {
            rootIndexDir.mkdirs();
        }

        load();
    }


    protected void save()
    {
        File nodesFile = new File(rootIndexDir, NODES_SERIALIZED_FILE_NAME);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ObjectOutputStream oos = null;

        try
        {
            fos = new FileOutputStream(nodesFile);
            bos = new BufferedOutputStream(fos);
            oos = new ObjectOutputStream(bos);

            oos.writeObject(nodes);
        }
        catch (Exception e)
        {
            log.error("Failed to write nodes data to " + nodesFile, e);
        }
        finally
        {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(fos);
        }
    }

    @SuppressWarnings("unchecked")
    protected void load()
    {
        File nodesFile = new File(rootIndexDir, NODES_SERIALIZED_FILE_NAME);

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        ObjectInputStream ois = null;

        if (nodesFile.isFile())
        {
            // In order to avoid EOFException, check whether or not the file is empty before reading.
            if (nodesFile.length() > 0) {
                try
                {
                    fis = new FileInputStream(nodesFile);
                    bis = new BufferedInputStream(fis);
                    ois = new ObjectInputStream(bis);

                    nodes = (HashMap<String, NodeInformation>) ois.readObject();
                }
                catch (Exception e)
                {
                    log.error("Failed to read nodes data from " + nodesFile, e);
                    nodes = new HashMap<String, NodeInformation>();
                }
                finally
                {
                    IOUtils.closeQuietly(ois);
                    IOUtils.closeQuietly(bis);
                    IOUtils.closeQuietly(fis);
                }
            } else {
                nodes = new HashMap<String, NodeInformation>();
            }
        }
        else
        {
            try
            {
                nodesFile.createNewFile();
            }
            catch (Exception e)
            {
                log.error("Failed to create new nodes data file.", e);
            }

            nodes = new HashMap<String, NodeInformation>();
        }
    }

    public synchronized int checkNode(Long revision, String contextName)
    {
        if ((contextName == null) || (revision == null))
        {
            return NodeManager.INVALID_NODE_REQUEST;
        }

        NodeInformation info = (NodeInformation) nodes.get(contextName);

        if (info == null)
        {
            return NodeManager.NODE_NEW;
        }

        if (info.getRevision().longValue() < revision.longValue())
        {
            return NodeManager.NODE_OUTDATED;
        }

        return NodeManager.NODE_SAVED;
    }

    public synchronized void addNode(Long revision, String contextName) throws Exception
    {
        if ((contextName == null) || (revision == null))
        {
            return;
        }

        NodeInformation info = (NodeInformation) nodes.get(contextName);

        if (info == null)
        {
            info = createNodeInformation();
            info.setContextName(contextName);
        }

        info.setRevision(revision);
        nodes.put(contextName, info);
        save();
    }

    public synchronized void removeNode(String contextName) throws Exception
    {
        if (contextName == null)
        {
            return;
        }

        NodeInformation info = (NodeInformation) nodes.get(contextName);

        if (info == null)
        {
            return;
        }

        nodes.remove(contextName);
        save();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.jetspeed.profiler.Profiler#createRuleCriterion()
     */
    protected NodeInformation createNodeInformation() throws ClassNotFoundException
    {
        try
        {
            NodeInformation nodeInformation = (NodeInformation) beanFactory.getBean(
                    this.nodeInformationBean, NodeInformation.class);
            return nodeInformation;
        }
        catch (Exception e)
        {
            log.error("Failed to create nodeInformation for " + nodeInformationBean
                    + " error : " + e.getLocalizedMessage());
            throw new ClassNotFoundException("Spring failed to create the "
                    + " nodeInformation bean.", e);
        }

    }

    /*
     * Method called automatically by Spring container upon initialization
     *
     * @param beanFactory automatically provided by framework @throws
     * BeansException
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }

    public synchronized int getNumberOfNodes()
    {
        return nodes.size();
    }
}
