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
package org.apache.jetspeed.security;

/**
 * @version $Id$
 */
public interface JetspeedPrincipalAssociationType
{
    /**
     * build-in supported @{link {@link #getAssociationName() associationName} "isMemberOf" 
     */
    String IS_MEMBER_OF = "isMemberOf"; // FROM node is member of TO node 

    /**
     * build-in supported @{link {@link #getAssociationName() associationName} "isChildOf"
     */
    String IS_CHILD_OF = "isChildOf"; // FROM node is member of TO node

    /**
     * build-in supported @{link {@link #getAssociationName() associationName} "isA"
     * 
     * This association type (between two equal {@link JetspeedPrincipalType}s) represents
     * a Generalizing Hierarchical relationship where from <em>implies</em> to.
     */
    String IS_A = "isA"; // FROM node is a TO node: FROM node implies TO node

    /**
     * build-in supported @{link {@link #getAssociationName() associationName} "isPartOf"
     * 
     * This association type (between two equal {@link JetspeedPrincipalType}s) represents
     * a Aggregating Hierarchical relationship where to <em>implies</em> from.
     */
    String IS_PART_OF = "isPartOf"; // FROM node is part of TO node: TO node implies FROM node

    /**
     * build-in supported @{link {@link #getAssociationName() associationName} "belongsTo"
     * 
     * This association type represents a (possibly required) constraint where from may only belong to one to
     */
    String BELONGS_TO = "belongsTo"; // FROM node is member of TO node: FROM node may only belong to one TO node
    
    /**
     * The name which is used <em>together</em> with the {@link #getFromPrincipalType()} and the
     * {@link #getToPrincipalType()} to identify a specific association between two {@link JetspeedPrincipal}s.
     * <p>
     * Note: the asociation name <em>value</em> must conform to the Java Identifier requirements (e.g. no spaces, dots, etc.)
     * to support localization through resource bundles.
     * </p>
     */
    String getAssociationName();

    JetspeedPrincipalType getFromPrincipalType();

    JetspeedPrincipalType getToPrincipalType();

    /**
     * True if the from principal cannot be created without this association.
     * <p>
     * If {@link #isDependent()} the from principal will be deleted when the to principal is deleted,
     * otherwise deleting the to principal is not allowed without first transfering association to another.
     * </p>
     */
    boolean isRequired(); 

    /**
     * True if the from principal will be deleted when the to principal is deleted.
     * <p>
     * Deleting the to principal is not allowed when {@link #isRequired()} until the association is transferred to another. 
     * </p>
     */
    boolean isDependent();
    
    /**
     * True if the from principal can be associated at most once.
     */
    boolean isSingular();
    
    /**
     * True if the to principal can be associated to at most once. 
     */
    boolean isDominant();
}
