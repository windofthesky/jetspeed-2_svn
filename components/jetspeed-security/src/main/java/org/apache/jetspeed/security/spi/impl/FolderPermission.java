/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.security.spi.impl;

import java.security.Permission;

import org.apache.jetspeed.security.PermissionFactory;
import org.apache.jetspeed.security.spi.PersistentJetspeedPermission;

/**
 * <p>Folder permission.</p>
 * <p>This code was partially inspired from:</p>
 * <ul>
 * <li>The article : <a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 * Extend JAAS for class instance-level authorization.</a></li>
 * <li>The FilePermission implementation from the JDK in order to support recursive permissions & wild card</li>
 * </ul>
 * <p/>
 * This class represents access to a portal content/folder or document.  A FolderPermission consists
 * of a pathname and a set of actions valid for that pathname.
 * <p/>
 * Pathname is the pathname of the folder or document granted the specified
 * actions. A pathname that ends in "/*" (where "/" is
 * the  separator character) indicates all the folders and documents contained in that folder.
 * A pathname that ends with "/-" indicates (recursively) all documents
 * and subfolders contained in that directory. A pathname consisting of
 * the special token "&lt;&lt;ALL FILES&gt;&gt;" matches <b>any</b> folder or document.
 * <p/>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:christophe.lombart@sword-technologies.com">Christophe Lombart</a>
 * @version $Id$
 */
public class FolderPermission extends BaseJetspeedPermission
{
    private static final long serialVersionUID = -4005330590344182308L;
    public static final char RECURSIVE_CHAR = '-';
    public static final char WILD_CHAR = '*';
    public static final String WILD_CHAR_STR = new String(new char[]{WILD_CHAR});
    public static final char FOLDER_SEPARATOR = '/';
    public static final String FOLDER_SEPARATOR_STR = new String(new char[]{FOLDER_SEPARATOR});
    
    public static class Factory extends JetspeedPermissionFactory
    {
        public Factory()
        {
            super(PermissionFactory.FOLDER_PERMISSION);
        }

        public FolderPermission newPermission(String name, String actions)
        {
            return new FolderPermission(getType(), name, actions);
        }

        public FolderPermission newPermission(String name, int mask)
        {
            return new FolderPermission(getType(), name, mask);
        }

        public FolderPermission newPermission(PersistentJetspeedPermission permission)
        {
            if (permission.getType().equals(getType()))
            {
                return new FolderPermission(permission);
            }
            throw new IllegalArgumentException("Permission is not of type "+getType());
        }
    }

    // does path indicate a folder? (wildcard or recursive)
    private boolean folder;

    // is it a recursive directory specification?
    private boolean recursive;

    private String cpath;

    protected FolderPermission(PersistentJetspeedPermission permission)
    {
        super(permission);
        parsePath();
    }

    protected FolderPermission(String type, String name, int mask)
    {
        super(type, name, mask);
        parsePath();
    }

    protected FolderPermission(String type, String name, String actions)
    {
        super(type, name, actions);
        parsePath();
    }

    /**
     * <p>Parses the path.</p>
     */
    private void parsePath()
    {
        if ((cpath = getName()) == null)
            throw new NullPointerException("name can't be null");

        if (cpath.equals("<<ALL FILES>>"))
        {
            folder = true;
            recursive = true;
            cpath = "";
            return;
        }
        int len = cpath.length();

        if (len == 0)
        {
            throw new IllegalArgumentException("invalid folder reference");
        }

        char last = cpath.charAt(len - 1);

        if (last == RECURSIVE_CHAR && (len == 1 || cpath.charAt(len - 2) == FOLDER_SEPARATOR))
        {
            folder = true;
            recursive = true;
            cpath = cpath.substring(0, --len);
        }
        else if (last == WILD_CHAR && (len == 1 || cpath.charAt(len - 2) == FOLDER_SEPARATOR))
        {
            folder = true;
            //recursive = false;
            cpath = cpath.substring(0, --len);
        }
    }

    /**
     * Checks if this FolderPermission object "implies" the specified permission.
     * <p/>
     * More specifically, this method returns true if:<p>
     * <ul>
     * <li> <i>p</i> is an instanceof FolderPermission,<p>
     * <li> <i>p</i>'s actions are a proper subset of this
     * object's actions, and <p>
     * <li> <i>p</i>'s pathname is implied by this object's
     * pathname. For example, "/tmp/*" implies "/tmp/foo", since
     * "/tmp/*" encompasses the "/tmp" folder and all subfolders or documents in that
     * directory, including the one named "foo".
     * </ul>
     *
     * @param p the permission to check against.
     * @return true if the specified permission is implied by this object,
     *         false if not.
     */
    public boolean implies(Permission p)
    {
        if (!(p instanceof FolderPermission))
        {
            return false;
        }

        FolderPermission that = (FolderPermission) p;
        return ((this.mask & that.mask) == that.mask) && impliesIgnoreMask(that);
    }

    /**
     * Checks if the Permission's actions are a proper subset of the
     * this object's actions. Returns the effective mask iff the
     * this FolderPermission's path also implies that FolderPermission's path.
     *
     * @param that the FolderPermission to check against.
     * @return the effective mask
     */
    boolean impliesIgnoreMask(FolderPermission that)
    {
        if (this.folder)
        {
            if (this.recursive)
            {
                // make sure that.path is longer then path so
                // something like /foo/- does not imply /foo
                if (that.folder)
                {
                    return (that.cpath.length() >= this.cpath.length()) && that.cpath.startsWith(this.cpath);
                }
                else
                {
                    return ((that.cpath.length() >= this.cpath.length()) && that.cpath.startsWith(this.cpath));
                }
            }
            else
            {
                if (that.folder)
                {
                    // if the permission passed in is a folder
                    // specification, make sure that a non-recursive
                    // permission (i.e., this object) can't imply a recursive
                    // permission.
                    if (that.recursive)
                        return false;
                    else
                        return (this.cpath.equals(that.cpath));
                }
                else
                {
                    int last = that.cpath.lastIndexOf(FOLDER_SEPARATOR);
                    if (last == -1)
                        return false;
                    else
                    {
                        // this.cpath.equals(that.cpath.substring(0, last+1));
                        // Use regionMatches to avoid creating new string

                        return (this.cpath.length() == (last + 1)) && this.cpath.regionMatches(0, that.cpath, 0, last + 1);
                    }
                }
            }
        }
        else
        {
            return (this.cpath.equals(that.cpath));
        }
    }

    /**
     * Checks two FolderPermission objects for equality. Checks that <i>obj</i> is
     * a FolderPermission, and has the same pathname and actions as this object.
     * <p/>
     *
     * @param obj the object we are testing for equality with this object.
     * @return true if obj is a FolderPermission, and has the same pathname and
     *         actions as this FolderPermission object.
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof FolderPermission))
            return false;

        FolderPermission that = (FolderPermission) obj;

        return (this.mask == that.mask) && this.cpath.equals(that.cpath) && (this.folder == that.folder)
                && (this.recursive == that.recursive);
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return a hash code value for this object.
     */

    public int hashCode()
    {
        return this.cpath.hashCode();
    }


}