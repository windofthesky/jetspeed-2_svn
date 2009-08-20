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
package org.apache.jetspeed.page;

import org.apache.jetspeed.page.document.Node;

/**
 * This interface describes the page manager event listener
 * that is notified when a managed node is updated or removed
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface PageManagerEventListener
{
    /**
     * newNode - invoked when the definition of a node is
     *           created by the page manager or when the
     *           node creation is otherwise detected
     *
     * @param node new managed node if known
     */
    void newNode(Node node);

    /**
     * updatedNode - invoked when the definition of a node is
     *               updated by the page manager or when the
     *               node modification is otherwise detected
     *
     * @param node updated managed node if known
     */
    void updatedNode(Node node);

    /**
     * removedNode - invoked when the definition of a node is
     *               removed by the page manager or when the
     *               node removal is otherwise detected
     *
     * @param node removed managed node if known
     */
    void removedNode(Node node);
    
    /**
     * reapNodes - periodically invoked by page manager to
     *             indicate lifetime of node references should
     *             be checked to see if they should be reaped
     *
     * @param interval reap nodes interval
     */
    void reapNodes(long interval);
}
