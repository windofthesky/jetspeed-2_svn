/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.jetspeed.services.registry.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.io.FileFilter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Monitors a Registry directory and notifies the associated Registry
 * of file updates.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class RegistryWatcher extends Thread
{
    private final static Log log = LogFactory.getLog(RegistryWatcher.class);

    /** Minimum scan rate for evaluating file refresh */
    public static final int SCAN_RATE = 10;

    /**
    The files monitored by this watcher
    */
    private Hashtable files = new Hashtable();

    /**
    the refresh rate, in milliseconds, to use for monitoring this file
    */
    private long refreshRate = 0;

    /**
    The object that relies on this RegsitryWatcher
    */
    private FileRegistry subscriber = null;

    /**
    The filter to use for filtering registry files
    */
    private FileFilter filter = null;

    /**
     * This object marks that we are done
    */
    private boolean done = false;

    /**
     * Creates a default RegistryWatcher
     */
    public RegistryWatcher()
    {
        setDaemon(true);
        setPriority(Thread.MIN_PRIORITY);
    }

    /** Modifies the subscriber to this Watcher
     *
     * @param registry the new registry subscriber
     */
    public void setSubscriber(FileRegistry registry)
    {
        synchronized (this)
        {
            if (subscriber!=null)
            {
                Enumeration en = files.keys();
                while(en.hasMoreElements())
                {
                    try
                    {
                        subscriber.removeFragment(((File)en.nextElement()).getCanonicalPath());
                    }
                    catch (Exception e)
                    {
                        log.error("RegistryWatcher: Can't remove fragment", e);
                    }
                }
            }

            this.subscriber = registry;

            if (subscriber!=null)
            {
                Enumeration en = files.keys();
                while(en.hasMoreElements())
                {
                    try
                    {
                        subscriber.loadFragment(((File)en.nextElement()).getCanonicalPath());
                    }
                    catch (Exception e)
                    {
                        log.error("RegistryWatcher: Can't load fragment", e);
                    }
                }
            }
        }
    }

    /** @return the subscriber to this watcher */
    public FileRegistry getSubscriber()
    {
        return this.subscriber;
    }

    /** Sets the refresh rate for this watcher
     *  @param refresh the refresh rate in seconds
     */
    public void setRefreshRate(long refresh)
    {
        this.refreshRate = (( refresh > SCAN_RATE ) ? refresh : SCAN_RATE) * 1000;
    }

    /** @return the refresh rate, in seconds, of this watcher */
    public long getRefreshRate()
    {
        return refreshRate / 1000;
    }

    /** Sets the file filter for selecting the registry files
     *  @param filter the file filter to use
     */
    public void setFilter(FileFilter filter)
    {
        this.filter = filter;
    }

    /** @return the file filter used by this watcher instance */
    public FileFilter getFilter()
    {
        return filter;
    }

    /** Change the base file or directory to be monitored by this watcher
     *
     * @param f the file or directory to monitor
     */
    public void changeBase(File f)
    {
        synchronized (this)
        {
            if (this.subscriber!=null)
            {
                Enumeration en = files.keys();
                while (en.hasMoreElements())
                {
                    try
                    {
                        subscriber.removeFragment(((File)en.nextElement()).getCanonicalPath());
                    }
                    catch (Exception e)
                    {
                        log.error("RegistryWatcher: Can't remove fragment", e);
                    }
                }
            }
            files.clear();
            findFiles(f);
        }
    }

    /**
     * Refresh the monitored file list
     *
     * @param f the file or directory to monitor
     */
    private void findFiles(File f)
    {
        File[] contents = null;

        if (f.exists() && f.canRead())
        {
            this.files.put(f,new Long(f.lastModified()));

            if (f.isDirectory())
            {

                if (filter != null)
                    contents = f.listFiles(filter);
                else
                    contents = f.listFiles();

                if (contents!=null)
                {
                    for (int i=0; i< contents.length; i++)
                    {
                        files.put(contents[i],new Long(contents[i].lastModified()));

                        if (subscriber!=null)
                        {
                            try
                            {
                                subscriber.loadFragment(contents[i].getCanonicalPath());
                            }
                            catch (Exception e)
                            {
                                log.error("RegistryWatcher: Can't load fragment", e);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * <p>Main routine for the monitor which periodically checks whether
     * the filex have been modified.</p>
     * The algorithm used does not guarantee a constant refresh rate
     * between invocations.
     */
    public void run()
    {
        try
        {
            while(!done)
            {
                boolean needRefresh = false;

                synchronized (this)
                {
                    Map fragments = subscriber.getFragmentMap();

                    if (log.isDebugEnabled())
                    {
                        log.debug( "RegistryWatcher: Saving dirty fragments.");
                    }

                    Iterator i = fragments.keySet().iterator();
                    while(i.hasNext())
                    {
                        try
                        {
                            String filename = (String)i.next();
                            RegistryFragment fragment = (RegistryFragment)subscriber.getFragmentMap().get(filename);

                            // if fragment has some uncommitted changes
                            if (fragment.isDirty())
                            {
                                //save it to disk
                                subscriber.saveFragment(filename);

                                if (log.isDebugEnabled())
                                {
                                    log.debug( "RegistryWatcher: Saved " + filename);
                                }

                                //and update the stored timestamp
                                Enumeration en = files.keys();
                                while(en.hasMoreElements())
                                {
                                    File f = (File)en.nextElement();
                                    if (filename.equals(f.getCanonicalPath()))
                                    {
                                        files.put(f,new Long(f.lastModified()));
                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("RegistryWatcher: exception during update",e);
                        }
                    }

                    if (log.isDebugEnabled())
                    {
                        log.debug( "RegistryWatcher: Checking for updated files.");
                    }

                    Enumeration en = files.keys();
                    while(en.hasMoreElements())
                    {
                        try
                        {
                            File f = (File)en.nextElement();
                            long modified = ((Long)files.get(f)).longValue();

                            if (!f.exists())
                            {
                                files.remove(f);
                            }
                            else
                            {
                                if (f.lastModified() > modified)
                                {
                                    files.put(f,new Long(f.lastModified()));

                                    if (f.isDirectory())
                                    {
                                        File[] contents = null;

                                        if (filter != null)
                                        {
                                            contents = f.listFiles(filter);
                                        }
                                        else
                                        {
                                            contents = f.listFiles();
                                        }

                                        if (contents!=null)
                                        {
                                            for (int idx=0; idx< contents.length; idx++)
                                            {
                                                if (files.get(contents[idx])==null)
                                                {
                                                    files.put(contents[idx],new Long(contents[idx].lastModified()));

                                                    if (subscriber!=null)
                                                    {
                                                        subscriber.loadFragment(contents[idx].getCanonicalPath());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        subscriber.loadFragment(f.getCanonicalPath());
                                    }

                                    if (log.isDebugEnabled())
                                    {
                                        log.debug("RegistryWatcher: Refreshing because "
                                                    + f.getCanonicalPath()
                                                    + " was modified.("
                                                    + f.lastModified()
                                                    + " "
                                                    + modified
                                                    + ")");
                                    }

                                    RegistryFragment frag = (RegistryFragment)fragments.get(f.getCanonicalPath());

                                    if (frag!=null)
                                    {
                                        frag.setChanged(true);
                                    }

                                    needRefresh = true;
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("RegistryWatcher: exception during update",e);
                        }
                    }

                    if (needRefresh)
                    {
                        subscriber.refresh();
                        needRefresh = false;
                    }

                    // make sure to reset the state of all fragments
                    i = fragments.keySet().iterator();
                    while(i.hasNext())
                    {
                        RegistryFragment frag = (RegistryFragment)fragments.get((String)i.next());
                        frag.setDirty(false);
                        frag.setChanged(false);
                    }
                }

                sleep( refreshRate );
            }
        }
        catch  (InterruptedException e)
        {
            log.error("RegistryWatcher: Stopping monitor: ");
            log.error(e);
            return;
        }
    }

    /**
     * Mark that the watching thread should be stopped
     */
    public void setDone()
    {
        done = true;
        log.info("RegistryWatcher: Watching thread stop requested");
    }

}
