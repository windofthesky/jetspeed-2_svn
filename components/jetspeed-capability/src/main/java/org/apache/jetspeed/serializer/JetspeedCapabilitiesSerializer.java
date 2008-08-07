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
package org.apache.jetspeed.serializer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.capabilities.Capability;
import org.apache.jetspeed.capabilities.Client;
import org.apache.jetspeed.capabilities.MediaType;
import org.apache.jetspeed.capabilities.MimeType;
import org.apache.jetspeed.serializer.objects.JSCapabilities;
import org.apache.jetspeed.serializer.objects.JSCapability;
import org.apache.jetspeed.serializer.objects.JSClient;
import org.apache.jetspeed.serializer.objects.JSClients;
import org.apache.jetspeed.serializer.objects.JSMediaType;
import org.apache.jetspeed.serializer.objects.JSMediaTypes;
import org.apache.jetspeed.serializer.objects.JSMimeType;
import org.apache.jetspeed.serializer.objects.JSMimeTypes;
import org.apache.jetspeed.serializer.objects.JSSnapshot;

/**
 * JetspeedCapabilitiesSerializer - Capabilities component serializer
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedCapabilitiesSerializer extends AbstractJetspeedComponentSerializer
{
    protected Capabilities caps;

    private static class Refs
    {
        HashMap mimeMap = new HashMap();

        HashMap mimeMapInt = new HashMap();

        HashMap mediaMap = new HashMap();

        HashMap capabilityMap = new HashMap();

        HashMap capabilityMapInt = new HashMap();

        HashMap clientMap = new HashMap();
    }

    public JetspeedCapabilitiesSerializer(Capabilities caps)
    {
        this.caps = caps;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processImport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processImport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_CAPABILITIES))
        {
            log.info("creating clients, mediatypes and mimetypes");
            log.debug("importCapabilitiesInfrastructure - processing");
            recreateCapabilities(snapshot, settings, log);
            recreateMimeTypes(snapshot, settings, log);
            recreateMediaTypes(snapshot, settings, log);
            recreateClients(snapshot, settings, log);
            log.debug("importCapabilitiesInfrastructure - processing done");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processExport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processExport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_CAPABILITIES))
        {
            log.debug("collecting clients, mediatypes and mimetypes");

            Refs refs = new Refs();
            
             //get the clients (which in turn will get the mime types and capabilities)
            exportClients(refs, snapshot, settings, log);
            // get the mediatTypes, too
            exportMediaTypes(refs, snapshot, settings, log);
        }
    }
    
    protected void deleteData(Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_CAPABILITIES))
        {
            log.debug("deleting clients, mediatypes and mimetypes");
            
            try
            {
                Iterator _it = caps.getMediaTypes();

                while (_it != null && _it.hasNext())
                {
                    caps.deleteMediaType((MediaType)_it.next());
                }
                    
                _it = caps.getClients();
                while (_it != null && _it.hasNext())
                {
                    caps.deleteClient((Client) _it.next());
                }

                _it = caps.getCapabilities();
                while (_it != null && _it.hasNext())
                {
                    caps.deleteCapability((Capability) _it.next());
                }
                
                _it = caps.getMimeTypes();
                while (_it != null && _it.hasNext())
                {
                    caps.deleteMimeType((MimeType) _it.next());
                }
            }
            catch (Exception e)
            {
                throw new SerializerException(e);
            }
        }
    }

    private void recreateCapabilities(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        log.debug("recreateCapabilities - processing");
        JSCapabilities capabilities = snapshot.getCapabilities();
        if ((capabilities != null) && (capabilities.size() > 0))
        {
            Iterator _it = capabilities.iterator();
            while (_it.hasNext())
            {
                JSCapability _c = (JSCapability) _it.next();
                // create a new Capability
                try
                {
                    Capability capability = caps.createCapability(_c.getName());
                    /**
                     * THE KEY_OVERWRITE_EXISTING test is not required for
                     * capabilites, since they carry no other information than
                     * the name Used here for consistency, though
                     */
                    if ((isSettingSet(settings, JetspeedSerializer.KEY_OVERWRITE_EXISTING))
                            || (capability.getCapabilityId() == 0))
                    {
                        caps.storeCapability(capability);
                    }
                }
                catch (Exception e)
                {
                    throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(
                            "org.apache.jetspeed.capabilities.Capabilities", e.getLocalizedMessage()));
                }
            }
        }
        else
            log.debug("NO CAPABILITES?????");
        log.debug("recreateCapabilities - done");
    }

    private void recreateMimeTypes(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        log.debug("recreateMimeTypes - processing");
        JSMimeTypes mimeTypes = snapshot.getMimeTypes();
        if ((mimeTypes != null) && (mimeTypes.size() > 0))
        {
            Iterator _it = mimeTypes.iterator();
            while (_it.hasNext())
            {
                JSMimeType _c = (JSMimeType) _it.next();
                // create a new Mime Type
                try
                {
                    MimeType mimeType = caps.createMimeType(_c.getName());
                    /**
                     * THE KEY_OVERWRITE_EXISTING test is not required for mime
                     * types, since they carry no other information than the
                     * name Used here for consistency, though
                     */
                    if ((isSettingSet(settings, JetspeedSerializer.KEY_OVERWRITE_EXISTING))
                            || (mimeType.getMimetypeId() == 0))
                    {
                        caps.storeMimeType(mimeType);
                    }

                }
                catch (Exception e)
                {
                    throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(
                            "org.apache.jetspeed.capabilities.MimeType", e.getLocalizedMessage()));
                }
            }
        }
        else
            log.debug("NO MIME TYPES?????");
        log.debug("recreateMimeTypes - done");
    }

    private void recreateMediaTypes(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        String _line;

        log.debug("recreateMediaTypes - processing");
        JSMediaTypes mediaTypes = snapshot.getMediaTypes();
        if ((mediaTypes != null) && (mediaTypes.size() > 0))
        {
            Iterator _it = mediaTypes.iterator();
            while (_it.hasNext())
            {
                JSMediaType _c = (JSMediaType) _it.next();
                // create a new Media
                try
                {
                    MediaType mediaType = caps.createMediaType(_c.getName());
                    /**
                     * THE KEY_OVERWRITE_EXISTING test IS required for media
                     * types, since they carry no other information than the
                     * name Used here for consistency, though
                     */
                    if ((isSettingSet(settings, JetspeedSerializer.KEY_OVERWRITE_EXISTING))
                            || (mediaType.getMediatypeId() == 0))
                    {
                        // set object fields
                        mediaType.setCharacterSet(_c.getCharacterSet());
                        mediaType.setTitle(_c.getTitle());
                        mediaType.setDescription(_c.getDescription());

                        try
                        {
                            _line = _c.getMimeTypesString().toString();
                            List<String> list = getTokens(_line);
                            if ((list != null) && (list.size() > 0))
                            {
                                Iterator<String> _it1 = list.iterator();
                                int added = 0;
                                while (_it1.hasNext())
                                {
                                    MimeType _mt = caps.createMimeType((String) _it1.next());
                                    if (_mt != null)
                                        mediaType.addMimetype(_mt);
                                    added++;
                                }
                            }
                        }
                        catch (Exception e1)
                        {
                            e1.printStackTrace();
                        }
                        try
                        {
                            _line = _c.getCapabilitiesString().toString();
                            List<String> list = getTokens(_line);
                            if ((list != null) && (list.size() > 0))
                            {
                                Iterator<String> _it1 = list.iterator();
                                if ((list != null) && (list.size() > 0))
                                {
                                    int added = 0;
                                    while (_it1.hasNext())
                                    {
                                        Capability _ct = caps.createCapability((String) _it1.next());
                                        if (_ct != null)
                                            mediaType.addCapability(_ct);
                                        added++;
                                    }
                                }
                            }
                        }
                        catch (Exception e1)
                        {
                            e1.printStackTrace();
                        }
                        caps.storeMediaType(mediaType);
                    }
                }
                catch (Exception e)
                {
                    throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(
                            "org.apache.jetspeed.capabilities.MediaType", e.getLocalizedMessage()));
                }
            }
        }
        else
            log.debug("NO MEDIA TYPES?????");
        log.debug("recreateMediaTypes - done");
    }

    private void recreateClients(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        String _line;

        log.debug("recreateClients - processing");
        JSClients clients = snapshot.getClients();
        if ((clients != null) && (clients.size() > 0))
        {
            Iterator _it = clients.iterator();
            while (_it.hasNext())
            {
                JSClient _c = (JSClient) _it.next();
                // create a new Media
                try
                {
                    Client client = caps.createClient(_c.getName());
                    /**
                     * THE KEY_OVERWRITE_EXISTING test IS required for media
                     * types, since they carry no other information than the
                     * name Used here for consistency, though
                     */
                    if ((isSettingSet(settings, JetspeedSerializer.KEY_OVERWRITE_EXISTING))
                            || (client.getClientId() == 0))
                    {
                        // set object fields
                        client.setUserAgentPattern(_c.getUserAgentPattern());
                        client.setManufacturer(_c.getManufacturer());
                        client.setModel(_c.getModel());
                        client.setEvalOrder(_c.getEvalOrder());
                        String myPrefMimeType = _c.getPreferredMimeTypeID();
                        client.setVersion(_c.getVersion());
                        try
                        {
                            _line = _c.getMimeTypesString().toString();
                            List<String> list = getTokens(_line);
                            if ((list != null) && (list.size() > 0))
                            {
                                Iterator<String> _it1 = list.iterator();
                                int added = 0;
                                while (_it1.hasNext())
                                {
                                    MimeType _mt = caps.createMimeType((String) _it1.next());
                                    if (_mt != null)
                                    {
                                        client.getMimetypes().add(_mt);
                                        if (_mt.getMimetypeId() == 0)
                                        {
                                            caps.storeMimeType(_mt);
                                        }
                                        if (myPrefMimeType.equalsIgnoreCase(_mt.getName()))
                                            client.setPreferredMimeTypeId(_mt.getMimetypeId());

                                    }
                                    added++;
                                }
                            }
                        }
                        catch (Exception e1)
                        {
                            e1.printStackTrace();
                        }
                        try
                        {
                            _line = _c.getCapabilitiesString().toString();
                            List<String> list = getTokens(_line);
                            if ((list != null) && (list.size() > 0))
                            {
                                Iterator<String> _it1 = list.iterator();
                                if ((list != null) && (list.size() > 0))
                                {
                                    int added = 0;
                                    while (_it1.hasNext())
                                    {
                                        Capability _ct = caps.createCapability((String) _it1.next());
                                        if (_ct != null)
                                            client.getCapabilities().add(_ct);
                                        added++;
                                    }
                                }
                            }
                        }
                        catch (Exception e1)
                        {
                            e1.printStackTrace();
                        }
                        caps.storeClient(client);
                    }
                }
                catch (Exception e)
                {
                    throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(
                            "org.apache.jetspeed.capabilities.Client", e.getLocalizedMessage()));
                }
            }
        }
        else
            log.debug("NO MEDIA TYPES?????");
        log.debug("recreateClients - done");
    }
    
    /**
     * extract the capabilities and save in snapshot file
     * 
     * @throws SerializerException
     */
    private void exportCapabilites(Refs refs, JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {

        Iterator list = caps.getCapabilities();

        while (list.hasNext())
        {
            try
            {
                Capability _cp = (Capability) list.next();
                JSCapability _jsC = new JSCapability();
                _jsC.setName(_cp.getName());
                refs.capabilityMap.put(_jsC.getName(), _jsC);
                refs.capabilityMapInt.put(new Integer(_cp.getCapabilityId()), _jsC);
                snapshot.getCapabilities().add(_jsC);
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "C", e.getMessage()}));
            }
        }
        return;

    }

    /**
     * exstract the mime types anmd save in snapshot file
     * 
     * @throws SerializerException
     */
    private void exportMimeTypes(Refs refs, JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {

        Iterator list = caps.getMimeTypes();

        while (list.hasNext())
        {
            try
            {
                MimeType _mt = (MimeType) list.next();
                JSMimeType _jsM = new JSMimeType();
                _jsM.setName(_mt.getName());
                refs.mimeMap.put(_jsM.getName(), _jsM);
                refs.mimeMapInt.put(new Integer(_mt.getMimetypeId()), _jsM);

                snapshot.getMimeTypes().add(_jsM);
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "MimeType", e.getMessage()}));
            }
        }
        return;

    }

    /**
     * create a JS CLient
     * 
     * @return
     * @throws SerializerException
     */

    private JSClient createJSClient(Refs refs, Client c) throws SerializerException
    {
        try
        {
            JSClient jsC = new JSClient(c);
            // find the mimeTypes
            Iterator _itM = c.getMimetypes().iterator();
            while (_itM.hasNext())
            {
                MimeType _m = (MimeType) _itM.next();
                JSMimeType _mt = (JSMimeType) refs.mimeMap.get(_m.getName());
                if (_mt != null) jsC.getMimeTypes().add(_mt);
            }
            
            Integer id = new Integer(c.getPreferredMimeTypeId());
            JSMimeType _mt = (JSMimeType) refs.mimeMapInt.get(id);
            if (_mt != null)
                jsC.setPreferredMimeTypeID(_mt.getName());
            else
                jsC.setPreferredMimeTypeID("???");

            // find the capabilities
            Iterator _itC = c.getCapabilities().iterator();
            while (_itC.hasNext())
            {
                Capability _c = (Capability) _itC.next();
                JSCapability _ct = (JSCapability) refs.capabilityMap.get(_c
                        .getName());
                if (_ct != null) jsC.getCapabilities().add(_ct);
            }

            return jsC;
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                            .create(new String[]
                            { "Client", e.getMessage()}));
        }

    }

    

    /**
     * extract the current clients and save in the snapshot file
     * 
     * @param list
     * @param caps
     * @return
     * @throws SerializerException
     */
    private void exportClients(Refs refs, JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {

        /** first the the mime types */
        exportMimeTypes(refs, snapshot, settings, log);

        /** second get the capabilities */
        this.exportCapabilites(refs, snapshot, settings, log);

        /** now get the clients */
        Iterator _it = caps.getClients();
        while (_it.hasNext())
        {
            Client c = (Client) _it.next();
            JSClient jsC = createJSClient(refs, c);
            if (jsC == null)
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "Client", "createClient returned NULL"}));
            refs.clientMap.put(jsC.getName(), jsC);
            snapshot.getClients().add(jsC);
        }
        return;
    }

    /**
     * extract the media types and save in snapshot file
     * 
     * @param caps
     *            capabilit container
     * @throws SerializerException
     */
    private void exportMediaTypes(Refs refs, JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        Iterator list = caps.getMediaTypes();

        while (list.hasNext())
        {
            try
            {
                MediaType _mt = (MediaType) list.next();
                JSMediaType _jsM = new JSMediaType(_mt);
                // find the mimeTypes
                Iterator _itM = _mt.getMimetypes().iterator();
                while (_itM.hasNext())
                {
                    MimeType _m = (MimeType) _itM.next();
                    JSMimeType _mttype = (JSMimeType) refs.mimeMap.get(_m.getName());
                    if (_mttype != null) _jsM.getMimeTypes().add(_mttype);
                }
                // find the capabilities
                Iterator _itC = _mt.getCapabilities().iterator();
                while (_itC.hasNext())
                {
                    Capability _c = (Capability) _itC.next();
                    JSCapability _ct = (JSCapability) refs.capabilityMap.get(_c
                            .getName());
                    if (_ct != null) _jsM.getCapabilities().add(_ct);
                }
                refs.mediaMap.put(_jsM.getName(), _jsM);
                snapshot.getMediaTypes().add(_jsM);
            } catch (Exception e)
            {
                // do whatever
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "MediaType", e.getMessage()}));
            }
        }
        return;
    }
}
