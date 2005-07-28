Copyright 2004 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Apache Portals Struts Framework Bridge:

This is the initial version of a small bridge to allow Struts to be used as Action handler and context renderer within a portlet.
It's far from complete yet but already the standard MailReader example application does run nicely under Jetspeed 2.
This demo application can be found under the applications folder in Jetspeed 2 and is automatically build and deployed with its setup.

Documentation I haven't really written yet (you're looking at all there is right now) but will be provided as soon as possible.

Overview:

  The problem with embedding Struts within a portlet is that Struts processes action handling and view rendering all in one event.
  To be able to use Struts the framework creates a *virtual* servlet context in which it intercepts the Struts processing after
  its action handling and postpones the view rendering to the portlet RenderRequest.
  To be able to do this *right* a Struts portlet application needs to follow some rules.

  Not every Struts application will run out of the box!

  A Struts page url within the portlet is encoded as a portlet render parameter. To be able to target a different page, adapted versions
  of the <html:form> and <html:link> tags are supplied (taglib struts-portlet.tld). These will generated a PortletURL with the target
  page correctly encoded as (action) parameter. Other Struts tags will have to be adapted as well but only these are done for now.

Assumptions, restrictions and features:

  All user interactions go through actions. Direct jsp access by the user will break the framework. This is already *recommended*
  for any Struts application so this should be not a big problem. Enforce this by storing all jsp files beneath WEB-INF.

  Struts page url's may not contain '|' or '$' characters. These are currently used to encode a page url to be able to use it as
  a render parameter.

  No direct output rendering from an action. All output should be rendered from an ActionForward after action processing.
  After an action the resulting view rendering ActionForward is included, forwarded or redirected to by Struts. The framework will
  intercept this and postpone the actual rendering to the RenderRequest event of the portlet.

  For an include or forward the current Struts context will be temporarily saved in a StrutsRenderContext object in the session. This
  contains the actual render path, the current ActionMapping, ActionForm, ActionMessages and errors (if defined).

  For a redirect just the new Struts page url will be set (note: these ActionForwards need to point at an action mapping, NOT a jsp).

  During a RenderRequest the framework will send Struts once again through the same processing path but will intercept before it will
  actually start checking/validating parameters. It'll then check if a StrutsRenderContext is available. If so, the context will be
  loaded into the request context and the saved ActionForward path will now be included (forwards are not allowed in portlets). Otherwise
  Struts will just continue processing (but won't have any parameters to process).

  The consequence of this solution is that input action chaining isn't supported (atleat not yet). So action processing realy should be
  done in one action only. Furthermore, view rendering should normally NOT be dependent on request scope input parameters. This is
  supported by the StrutsRenderContext (which ONLY saves the ActionForm and messages and currently NO other parameters or attributes)
  but really only meant for handling validation errors and will only work once (refreshing after a validation error will clear any input
  /errormessage). A proper MVC implementation should already give you that by the way. If input state has to be preserved beyond one
  request, session scope ActionForms and ActionMessages stored in the session (supported since Struts 1.2) should be used.

  To get the proper flow, ActionForwards used after a successfull action processing really should define redirect="true".

  Struts will normaly forward back to the error page after a validation error. The input attribute of the actionmapping is used for that.
  But this normally is pointing to a ActionForward directed at an jsp or pointing to a jsp itself. That won't do using this framework.
  Therefore the page url requesting the action is saved by the struts-portlet tags as well. If after an action processing errors are
  found the framework will ignore the input page url but uses its own saved url.

  One thing which this bridge supports while formally this is not allowed in a portlet is accepting response.sendError() calls. During
  ActionRequest processing the error code (and optional message) are saved in a StrutsErrorContext. When the RenderRequest comes along
  an error page will be rendered (currently embedded within StrutsPortlet). A page refresh will bring up the current Struts page again.
  Futhermore, any non-handled exception will end up on that *catch-all* error page.

Usage:

  - run maven install (default goal so just maven will do)
    This will build and install (in your local maven repository) the portals-bridges-struts-<version>.jar.
    This jar has to be put in the WEB-INF/lib of a portlet.

    It depends on portals-bridges-common which delivers the ServletContextProvider interface.
    This interface has to be implemented for a specific portal and gives StrutsPortlet access to the servlet context
    its running in. For J2 this has already been done: org.apache.jetspeed.portlet.ServletContextProviderImpl, and is
    globabally available to any J2 portlet (embedded in the jetspeed-commons-<version>.jar located in Tomcat/shared/lib).

  - create/modify web.xml:
    Define as action servlet: org.apache.portals.bridges.struts.PortletServlet which extends org.apache.struts.action.ActionServlet.
    Define an extension mapping for the action servlet (*.do). Directory mapping is not supported.

  - create/modify portlet.xml:
    Use as portlet-class: org.apache.portals.bridges.struts.StrutsPortlet
    Define the following init-param elements:
      - name:     ServletContextProvider
        value:    org.apache.jetspeed.portlet.ServletContextProviderImpl (or one of your own if not using J2)
        required: yes
      - name:     ViewPage
        value:    <action mapping> to welcome page like: /welcome.do or /index.do (note the extension: jsp's won't do)
        required: yes
        The welcome file list from web.xml is not used!
      - name:     EditPage
        value:    <action mapping> to default edit page
        required: no, default taken from ViewPage
      - name:     HelpPage
        value:    <action mapping> to default help page
        required: no, default taken from ViewPage
      - name:     ActionPage
        value:    <action mapping> to default action page
        required: no, default taken from ViewPage

  - Use the right struts.jar and its .tld files. The framework currently has been tested against version 1.2.1.

  - The struts-portlet.tld is included in the struts-portlet.jar so you can refer to it directly from within jsp files
    using something like <%@ taglib uri="http://struts.apache.org/tags-portlet" prefix="sp" %>.
   
   - modify struts-config.xml:
    Define as controller processorClass: org.apache.portals.bridges.struts.PortletRequestProcessor.
    Note, tiles usage isn't possible yet (probably won't be difficult to support so maybe soon).

  - replace all struts tags which generate action urls:
    The struts-portlet.tld currently contains adaptions for <html:form> and <html:link>. Others will need to be provided later.
    These adapted tags ensures the correct portlet url's will be generated.

  - replace remote javascript retrieval ( <javascript src="url"/> with <struts-portlet:script src="url"/> for the same reason
    as above.

  - setup the actions and mappings with respect to the above mentioned assumptions and restrictions.

Demo:

  As example implementation and proof of concept the Struts MailReader example application (from version 1.2.1) is
  converted to a Struts Portlet.

  The example has been modified on the following points:
  - all jsp files and the tour.html are moved below WEB-INF in folder view.
  - the <html:form> and <html:link> tags are replaced by their struts-portlet versions
  - the remote javascript tags (for validation) are replaced by the struts-portlet script tag
  - the welcome file index.jsp is removed (not needed anymore and did issue an illegal forward anyway)
  - the CheckLogonTag has been removed:
    This one issued an illegal forward when the user wasn't logged on. Besided being *bad* practice to do flow control during
    view rendering having moved the jsp files below the WEB-INF now enforced action processing anyway so the check is done now
    appropriately there.
  - The struts-config files (there are two) are modified according the above rules.
  - Logoff doesn't invalidate the session anymore which could interfere with other portlets within the same application.

  As the demo is configured for deployment under Jetspeed-2 a ServletContextProvider implementation is already available at runtime (see above).
  To get it running under a different portal an implementation for ServletContextProvider has to be provided. If its not
  delivered by the portal itself you can try to define one yourself and include it in the demo.


20040729, Ate Douma