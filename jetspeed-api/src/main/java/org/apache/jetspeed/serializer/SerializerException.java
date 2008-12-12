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
package org.apache.jetspeed.serializer;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.i18n.KeyedMessage;

/**
 * <p>Exception throwns by members of the security service.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class SerializerException extends JetspeedException
{
    /** The serial version uid. */
    private static final long serialVersionUID = -8823877029853488431L;

    /** <p>Component Manager does not exist exception message.</p> */
    public static final KeyedMessage COMPONENTMANAGER_DOES_NOT_EXIST = new KeyedMessage("The component manager {0} does not exist.");

    /** <p>Creating the serilized Object failed </p> */
    public static final KeyedMessage GET_EXISTING_OBJECTS = new KeyedMessage("Get existing objects for {0} failed with message {1}");

    /** <p>Creating the serilized Object failed </p> */
    public static final KeyedMessage CREATE_SERIALIZED_OBJECT_FAILED = new KeyedMessage("Creating a serialized representation of {0} failed with message {1}");

    /** <p>Creating the serilized Object failed </p> */
    public static final KeyedMessage CREATE_OBJECT_FAILED = new KeyedMessage("Creating an instance from a serialized representation of {0} failed with message {1}");

    /** <p>Component Manager already initialized</p> */
    public static final KeyedMessage COMPONENT_MANAGER_EXISTS = new KeyedMessage("Component Manager already established");


    /** <p>Filename already exists</p> */
    public static final KeyedMessage FILE_ALREADY_EXISTS = new KeyedMessage("File {0} already exists");

    /** <p>Filename already exists</p> */
    public static final KeyedMessage FILE_BACKUP_FAILED = new KeyedMessage("File {0} backup failed. Could not create new backup file name.");

    /** <p>io error</p> */
    public static final KeyedMessage FILE_PROCESSING_ERROR = new KeyedMessage("Error processing File {0} : {1}");
    /** <p>writer error</p> */
    public static final KeyedMessage FILE_WRITER_ERROR = new KeyedMessage("Error creating Writer for {0} : {1}");
    /** <p>reader error</p> */
    public static final KeyedMessage FILE_READER_ERROR = new KeyedMessage("Error creating Reader for {0} : {1}");

    /** <p>import error</p> */
    public static final KeyedMessage IMPORT_ERROR = new KeyedMessage("Unexpected error during import: {0}");
    
    /** <p>export error</p> */
    public static final KeyedMessage EXPORT_ERROR = new KeyedMessage("Unexpected error during export: {0}");
    
    /** <p>version problem -  version in XML file is not compatible with current environment </p> */
    public static final KeyedMessage INCOMPETIBLE_VERSION  = new KeyedMessage("Incompetible version in {0} : CurrentVersion = {1}, RequestedVersion = {2}");
    
    
    /**
     * <p>Default Constructor.</p>
     */
    public SerializerException()
    {
        super();
    }

    public SerializerException(Throwable t)
    {
        super(t);
    }
    
    /**
     * <p>Constructor with exception message.</p>
     * @param message The exception message.
     */
    public SerializerException(KeyedMessage typedMessage)
    {
        super(typedMessage);
    }

    /**
     * <p>Constructor with exception message and nested exception.</p>
     * @param msg The exception message.
     * @param nested Nested exception.
     */
    public SerializerException(KeyedMessage msg, Throwable nested)
    {
        super(msg, nested);
    }

}
