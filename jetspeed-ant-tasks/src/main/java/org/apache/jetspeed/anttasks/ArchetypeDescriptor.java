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
package org.apache.jetspeed.anttasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.Reference;

/**
 * ArchetypeDescriptor
 * 
 * This Ant task is used to generate a Maven2 Archetype Descriptor file.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class ArchetypeDescriptor extends Task
{
    private String header;
    private String artifactId;
    private Boolean allowPartial = new Boolean(true);
    private File baseDir;
    private File destFile;
    private Reference sourcesRefId;
    private Reference resourcesRefId;
    private Reference testSourcesRefId;
    private Reference testResourcesRefId;
    private Reference siteResourcesRefId;

    /**
     * Set archetype descriptor header text
     *
     * @param header archetype header text
     */
    public void addText(String header)
    {
        this.header = getProject().replaceProperties(header);
    }

    /**
     * Set archetype artifact id.
     *
     * @param artifactId archetype artifact id
     */
    public void setArtifactid(String artifactId)
    {
        this.artifactId = artifactId;
    }

    /**
     * Set archetype partial rules to support execution in existing projects.
     *
     * @param allowPartial archetype allow partial setting 
     */
    public void setAllowpartial(Boolean allowPartial)
    {
        this.allowPartial = allowPartial;
    }

    /**
     * Set archetype base directory for all sources/resources.
     *
     * @param baseDir archtype source/resource base directory
     */
    public void setBasedir(File baseDir)
    {
        this.baseDir = baseDir;
    }

    /**
     * Set archetype descriptor destination file.
     *
     * @param destFile archetype descriptor file to generate
     */
    public void setDestfile(File destFile)
    {
        this.destFile = destFile;
    }

    /**
     * Set archetype sources refid.
     *
     * @param sourcesRefId archetype sources
     */
    public void setSourcesrefid(Reference sourcesRefId)
    {
        this.sourcesRefId = sourcesRefId;
    }

    /**
     * Set archetype resources fileset refid.
     *
     * @param resourcesRefId archetype resources
     */
    public void setResourcesrefid(Reference resourcesRefId)
    {
        this.resourcesRefId = resourcesRefId;
    }

    /**
     * Set archetype test sources fileset refid.
     *
     * @param testSourcesRefId archetype test sources
     */
    public void setTestsourcesrefid(Reference testSourcesRefId)
    {
        this.testSourcesRefId = testSourcesRefId;
    }

    /**
     * Set archetype test resources fileset refid.
     *
     * @param testResourcesRefId archetype test resources
     */
    public void setTestresourcesrefid(Reference testResourcesRefId)
    {
        this.testResourcesRefId = testResourcesRefId;
    }

    /**
     * Set archetype site resources fileset refid.
     *
     * @param siteResourcesRefId archetype site resources
     */
    public void setSiteresourcesrefid(Reference siteResourcesRefId)
    {
        this.siteResourcesRefId = siteResourcesRefId;
    }

    /**
     * Executes task to generate desciptor file.
     *
     * @throws BuildException
     */
    public void execute() throws BuildException
    {
        // basic validation
        if ((artifactId == null) || (baseDir == null) || (destFile == null))
        {
            throw new BuildException("required artifactid, basedir, or destfile attribute missing", getLocation());
        }

        // reference sources/resources
        List sources = archetypeFiles(sourcesRefId, "sourcesrefid");
        List resources = archetypeFiles(resourcesRefId, "resourcesrefid");
        List testSources = archetypeFiles(testSourcesRefId, "testsourcesrefid");
        List testResources = archetypeFiles(testResourcesRefId, "testresourcesrefid");
        List siteResources = archetypeFiles(siteResourcesRefId, "siteresourcesrefid");

        // write archetype descriptor file
        PrintWriter writer = null;
        try
        {
            // make sure destination ddirectory exists
            File destDir = destFile.getParentFile();
            if (!destDir.exists())
            {
                destDir.mkdirs();
            }

            // write descriptor to destination file
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8"));
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            if (header != null)
            {
                writer.println(header);
            }
            writer.println("<archetype>");
            writer.println("    <id>" + artifactId + "</id>");
            writer.println("    <allowPartial>" + allowPartial + "</allowPartial>");
            writeFiles(writer, "sources", "source", sources);
            writeFiles(writer, "resources", "resource", resources);
            writeFiles(writer, "testSources", "source", testSources);
            writeFiles(writer, "testResources", "resource", testResources);
            writeFiles(writer, "siteResources", "resource", siteResources);
            writer.println("</archetype>");
        }
        catch (IOException ioe)
        {
            throw new BuildException("unable to write decriptor", ioe, getLocation());
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }

        // log task success
        int archetypeSourcesCount = sources.size() + resources.size() + testSources.size() + testResources.size() + siteResources.size();
        log("generated " + destFile + " including " + archetypeSourcesCount + " source/resource files");
    }

    /**
     * Utility to read fileset refid attributes.
     *
     * @param refId attribute refid
     * @param name attribute name
     * @return list of String file paths
     * @throws BuildException
     */
    private List archetypeFiles(Reference refId, String name) throws BuildException
    {
        List archetypeFiles = new ArrayList();
        if (refId != null)
        {
            // access fileset from refid
            if (!(refId.getReferencedObject(getProject()) instanceof AbstractFileSet))
            {
                throw new BuildException(name + " attribute must reference a fileset", getLocation());
            }
            AbstractFileSet fileSet = (AbstractFileSet)refId.getReferencedObject(getProject());
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
            String [] files = directoryScanner.getIncludedFiles();
            if ((files != null) && (files.length > 0))
            {
                String basePath = canonicalPath(baseDir);
                for (int i = 0; (i < files.length); i++)
                {
                    // convert file relative to fileset to file relative
                    // to archetype base dir
                    String archetypePath = files[i];
                    if (basePath != null)
                    {
                        File file = new File(fileSet.getDir(getProject()), files[i]);
                        String filePath = canonicalPath(file);
                        if ((filePath != null) && filePath.startsWith(basePath))
                        {
                            // path relative to baseDir
                            archetypePath = filePath.substring(basePath.length());
                            if (archetypePath.startsWith("/"))
                            {
                                archetypePath = archetypePath.substring(1);
                            }
                        }
                    }
                    // add archetype files
                    archetypeFiles.add(archetypePath);
                }
            }
        }
        return archetypeFiles;
    }

    /**
     * Utility to get canonical file path
     *
     * @param file file to convert to canonical path
     * @return canonical path
     */
    private String canonicalPath(File file)
    {
        try
        {
            String path = file.getCanonicalPath();
            return path.replace('\\', '/');
        }
        catch (IOException ioe)
        {
            return null;
        }
    }

    /**
     * Utility to write archetype descriptor file lists.
     *
     * @param writer descriptor writer
     * @param collectionElementName collection element name
     * @param elementName file element name
     * @param files list of String file paths
     * @throws IOException
     */
    private void writeFiles(PrintWriter writer, String collectionElementName, String elementName, List files) throws IOException
    {
        // write file list to descriptor
        if (!files.isEmpty())
        {
            writer.println("    <" + collectionElementName + ">");
            Iterator fileIter = files.iterator();
            while (fileIter.hasNext())
            {
                writer.println("        <" + elementName + ">" + (String)fileIter.next() + "</" + elementName + ">");
            }
            writer.println("    </" + collectionElementName + ">");
        }
    }
}
