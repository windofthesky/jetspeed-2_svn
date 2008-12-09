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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Node Manager 
 *
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version 
 */
public class NodeManagerImpl implements NodeManager,BeanFactoryAware
{
	protected final static Log log = LogFactory.getLog(NodeManagerImpl.class);

    /**
     * added support for bean factory to create profile rules
     */
    private BeanFactory beanFactory;

	private HashMap<String, NodeInformation> nodes = null; 
	private File rootIndexDir = null;
	    
    /** the default criterion bean name */
    private String nodeInformationBean = "NodeInformation";

	
	public NodeManagerImpl(String indexRoot, String nodeInformationBean)
    throws Exception
    {
    	
        //assume it's full path for now
        rootIndexDir = new File(indexRoot);
        this.nodeInformationBean = nodeInformationBean;

        if (!(rootIndexDir.exists()))
        	rootIndexDir.mkdirs();
        load();
    }

	
	protected void save()
	{
		try {
		      FileOutputStream fout = new FileOutputStream(rootIndexDir.getAbsolutePath()+ "/nodeInfo.ser");
		      ObjectOutputStream oos = new ObjectOutputStream(fout);
		      oos.writeObject(nodes);
		      oos.close();
		      }
		   catch (Exception e) 
		   {
	            log.error("Failed to write nodes data file to  " + rootIndexDir.getAbsolutePath()+ "/nodeInfo.ser" + " - error : " + e.getLocalizedMessage());
			   e.printStackTrace(); 
			}
	}
    
	@SuppressWarnings("unchecked")
    protected void load()
	{
			File data = new File( rootIndexDir.getAbsolutePath()+ "/nodeInfo.ser");
			if (data.exists())
			{
				try {
				    FileInputStream fin = new FileInputStream(data.getAbsolutePath());
				    ObjectInputStream ois = new ObjectInputStream(fin);
				    nodes = (HashMap<String,NodeInformation>) ois.readObject();
				    ois.close();
				    }
				   catch (Exception e) 
				   { 
			            log.error("Failed to read nodes data file from " + data.getAbsolutePath() + " - error : " + e.getLocalizedMessage());
					   nodes = new HashMap<String,NodeInformation>();
				   }
			}
			else
			{
				try
				{
					data.createNewFile();
				}
				catch (Exception e)
				{
		            log.error("Failed to create new nodes data file error : " + e.getLocalizedMessage());
					e.printStackTrace();
				}
				nodes = new HashMap<String,NodeInformation>();
			}
			
//			NodeInformationImpl temp = new NodeInformationImpl();
//			temp.setContextName("tttt");
	}
	public int checkNode(Long revision, String contextName)
	{
		if ((contextName == null) || (revision == null))
			return NodeManager.INVALID_NODE_REQUEST;
		NodeInformation info = (NodeInformation)nodes.get(contextName);
		if (info == null)
			return NodeManager.NODE_NEW;
		if (info.getRevision().longValue() < revision.longValue())
			return NodeManager.NODE_OUTDATED;
		return NodeManager.NODE_SAVED;
	}
	
	public void addNode(Long revision, String contextName) throws Exception
	{
		if ((contextName == null) || (revision == null))
			return;
		NodeInformation info = (NodeInformation)nodes.get(contextName);
		if (info == null)
		{
			info = createNodeInformation();
			info.setContextName(contextName);
		}
		info.setRevision(revision);
		nodes.put(contextName, info);
		save();
	}

	public void removeNode(String contextName) throws Exception
	{
		if (contextName == null)
			return;
		NodeInformation info = (NodeInformation)nodes.get(contextName);
		if (info == null)
			return;
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
        } catch (Exception e)
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

	public int getNumberOfNodes()
	{
		return nodes.size();
	}    
}
