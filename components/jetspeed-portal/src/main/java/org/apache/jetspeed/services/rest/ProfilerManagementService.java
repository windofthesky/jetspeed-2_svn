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
package org.apache.jetspeed.services.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.services.beans.ProfileCriterionBean;
import org.apache.jetspeed.services.beans.ProfileDataTableBean;
import org.apache.jetspeed.services.beans.ProfileEditBean;
import org.apache.jetspeed.services.beans.UpdateResultBean;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserManagerService. This REST service provides access to the jetspeed user manager. The access of all methods are restricted to the users with the 'admin'
 * role.
 * 
 * @version $Id$
 */
@Path("/profiler/")
public class ProfilerManagementService extends AbstractRestService
{

    private static Logger log = LoggerFactory.getLogger(ProfilerManagementService.class);

    private Profiler profiler;

    public ProfilerManagementService(Profiler profiler,
                                     PortletActionSecurityBehavior securityBehavior)
    {
        super(securityBehavior);
        this.profiler = profiler;
    }

    /**
     * Find users according to query parameters.
     * 
     * @param servletRequest
     * @param uriInfo
     * @return
     */
    @GET
    @Path("/list")
    public ProfileDataTableBean listProfiles(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo)
    {
        checkPrivilege(servletRequest, JetspeedActions.VIEW);
        
        Collection<ProfilingRule> rules = profiler.getRules();
        return new ProfileDataTableBean(rules);
    }

    @GET
    @Path("/edit/{id}/")
    public ProfileEditBean lookupProfile(@Context HttpServletRequest servletRequest,
                                                  @Context UriInfo uriInfo,
                                                  @PathParam("id") String profileId) {
        checkPrivilege(servletRequest, JetspeedActions.VIEW);

        if (StringUtils.isBlank(profileId)) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(
                    new UpdateResultBean(Response.Status.BAD_REQUEST.getStatusCode(),  "Profile id not specified")).build());
        }
        ProfilingRule rule = profiler.getRule(profileId);
        if (rule == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(
                    new UpdateResultBean(Response.Status.BAD_REQUEST.getStatusCode(), "Profile id not found with the specified id: " + profileId)).build());
        }
        ProfileEditBean editBean = new ProfileEditBean(rule.getId(), rule.getTitle(), rule.getClassname());
        for (RuleCriterion criterion : rule.getRuleCriteria()) {
            editBean.add(new ProfileCriterionBean(criterion.getName(), criterion.getValue(),
                               criterion.getType(), criterion.getFallbackType(), criterion.getFallbackOrder()));
        }
        return editBean;
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/update")
    public UpdateResultBean addOrUpdateProfile(String json, @Context HttpServletRequest servletRequest) {

        checkPrivilege(servletRequest, JetspeedActions.VIEW);

        ObjectMapper writeMapper = new ObjectMapper();
        ProfileEditBean dtoProfile = null;
        try {
            dtoProfile = writeMapper.readValue(json, ProfileEditBean.class);
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new UpdateResultBean(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Bad input on profile data")).build());
        }
        if (StringUtils.isBlank(dtoProfile.getId())) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new UpdateResultBean(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Profile id not specified")).build());
        }
        if (StringUtils.isBlank(dtoProfile.getConcreteClass())) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new UpdateResultBean(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Concrete Class not specified")).build());
        }
        try {
            boolean isAdd = false;
            ProfilingRule rule = profiler.getRule(dtoProfile.getId());
            if (rule == null) {
                rule = profiler.createProfilingRule(dtoProfile.getConcreteClass().indexOf("StandardProfilingRule") > -1);
                rule.setId(dtoProfile.getId());
                isAdd = true;
            }
            rule.setId(dtoProfile.getId());
            rule.setClassname(dtoProfile.getConcreteClass());
            rule.setTitle(dtoProfile.getTitle());

            // process criteria deletes
            if (!isAdd) {
                Map<String, ProfileCriterionBean> beanMap = new HashMap<>();
                for (ProfileCriterionBean pcb : dtoProfile.getCriteria()) {
                    beanMap.put(pcb.getName(), pcb);
                }
                List<RuleCriterion> deletes = new ArrayList<>();
                for (RuleCriterion rc : rule.getRuleCriteria()) {
                    if (beanMap.get(rc.getName()) == null) {
                        deletes.add(rc);
                    }
                }
                for (RuleCriterion rc : deletes) {
                    rule.getRuleCriteria().remove(rc);
                }
            }

            // process criteria adds and updates
            Map<String, RuleCriterion> rulesMap = new HashMap<>();
            for (RuleCriterion rc : rule.getRuleCriteria()) {
                rulesMap.put(rc.getName(), rc);
            }
            for (ProfileCriterionBean dto : dtoProfile.getCriteria()) {
                RuleCriterion rc = rulesMap.get(dto.getName());
                if (rc == null) {
                    rc = profiler.createRuleCriterion();
                    rc.setName(dto.getName());
                    rc.setValue(dto.getValue());
                    rc.setType(dto.getResolverType());
                    rc.setFallbackType(dto.getFallback());
                    rc.setFallbackOrder(dto.getOrder());
                    rc.setRuleId(rule.getId());
                    rule.getRuleCriteria().add(rc);
                }
                else {
                    rc.setValue(dto.getValue());
                    rc.setType(dto.getResolverType());
                    rc.setFallbackType(dto.getFallback());
                    rc.setFallbackOrder(dto.getOrder());
                    rc.setRuleId(rule.getId());
                }
            }

            profiler.storeProfilingRule(rule);
            return new UpdateResultBean(Response.Status.OK.getStatusCode(), "OK");
        }
        catch (Exception e) {
            String message = "failed to update profile for " + dtoProfile.getId();
            log.error(message, e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new UpdateResultBean(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message)).build());
        }

    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public UpdateResultBean deleteProfiles(List<String> profileIds, @Context HttpServletRequest servletRequest) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("processing DELETE on /profiler for %s", profileIds));
        }

        checkPrivilege(servletRequest, JetspeedActions.VIEW);

        if (profileIds == null || profileIds.size() == 0) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new UpdateResultBean(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Profile ids to delete not specified")).build());
        }

        try {
            //profileIds = PathSegmentUtils.parseNames(profileIdsString);
            for (String id : profileIds) {
                ProfilingRule rule = profiler.getRule(id);
                if (rule != null) {
                    profiler.deleteProfilingRule(rule);
                }
            }
            return new UpdateResultBean(Response.Status.OK.getStatusCode(), "OK");
        } catch (Exception e) {
            String message = String.format("Error converting profiler ids [%s]", profileIds);
            log.debug(message);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new UpdateResultBean(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message)).build());
        }
    }

}
