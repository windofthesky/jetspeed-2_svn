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
 *
 * @version $Id$
 */

YUI.add('jetui-portal', function(Y) {

    /**
     * JetUI Portal JavaScript Framework
     *
     * @module jetui-portal
     */
    
    Y.namespace("JetUI");
    
    // JETUI_YUI must be defined outside. However, just check again not to throw exceptions.
    if (!JETUI_YUI) {
        JETUI_YUI = {};
    }
    
    /**
     * Global method definition to returns the portal singleton instance
     * @method
     */
    JETUI_YUI.getPortalInstance = function() {
        if (!JETUI_YUI._portalInstance) {
            JETUI_YUI._portalInstance = new Y.JetUI.Portal(JETUI_YUI.config);
        }
        return JETUI_YUI._portalInstance;
    };
    
    /**
     * Create a portal to represent a portal screen.
     *
     * @class JetUI.Portal
     * @extends Base
     * @param config {Object} Configuration object
     * @constructor
     */
    Y.JetUI.Portal = function() {
        Y.JetUI.Portal.superclass.constructor.apply(this, arguments);
    };
    
    Y.mix(Y.JetUI.Portal, {
        
        /**
         * The identity of the widget.
         *
         * @property JetUI.Portal.NAME
         * @type String
         * @static
         */
        NAME : 'Portal',
    	/**
    	 * Module Constants
    	 */
    	CSRE : 'CSRE',
    	SSRE : 'SSRE'                
    });
    
    Y.extend(Y.JetUI.Portal, Y.Base, {
        
        /**
         * @property portal context path
         * @type String
         */
        portalContextPath : "",
        
        /**
         * @property portal base path
         * @type String
         */
        portalServletPath : "",
        
        /**
         * @property portal page path
         * @type String
         */
        portalPagePath : "/",
        
        /**
         * @property desktopMode
         * @type Boolean
         */
        desktopMode : false,
        
        /**
         * @property jstbLeft
         * @type Object
         */
        jstbLeft : null,
        
        /**
         * @property jstbRight
         * @type Object
         */
        jstbRight : null,
        
        /**
         * @property isMoving
         * @type Boolean
         */
        isMoving : false,
        
        /**
         * @property goingUp
         * @type Boolean
         */
        goingUp : false,
        
        /**
         * @property goingRight
         * @type Boolean
         */
        goingRight : false,
        
        /**
         * @property lastY
         * @type Number
         */
        lastY : 0,
        
        /**
         * @property lastX
         * @type Number
         */
        lastX : 0,
        
        /**
         * Construction logic executed during instantiation.
         *
         * @method initializer
         * @protected
         */
        initializer : function(cfg) {
            if (cfg) {
                this.portalContextPath = cfg.portalContextPath;
                this.portalServletPath = cfg.portalServletPath;
                this.portalPagePath = cfg.portalPagePath;
            }
        },
        
        /**
         * Destruction logic executed during instantiation.
         *
         * @method initializer
         * @protected
         */
        destructor : function(cfg) { 
        },
        
        /**
         * Toggles toolbar
         * 
         * @method toggleToolbar
         */
        toggleToolbar : function(toolbar, toggler, compareStyle) {
            var portal = JETUI_YUI.getPortalInstance();        	
            toggler.toggleClass('jstbToggle1');
            toggler.toggleClass('jstbToggle2');
            var currentStyle = toggler.getAttribute('class');
            var nodelist = toolbar.get('children');
            var state = 'normal';
            var reverse = false;
            if (currentStyle == compareStyle) {
            	toolbar.setStyle('display', 'block');
            	nodelist.setStyle('display', 'block');
            	reverse = true;
            } else {
                nodelist.setStyle('display', 'none');
                toolbar.setStyle('display', 'none');
                state = 'closed';
            }
            var windowId = (toolbar == portal.jstbLeft) ? 'template-top2.jstbLeft' : 'template-top2.jstbRight';
            var uri = portal.portalContextPath + "/services/pagelayout/fragment/" + windowId + "/mod/?_type=json";
            uri += "&state=" + state;
            var config = {
                    on: { complete: portal.onStateComplete },
                    method: "PUT",
                    headers: { "X-Portal-Path" : portal.portalPagePath },
                    arguments: { complete: [ windowId ] }
                };
            var request = Y.io(uri, config);            
        },
        
        /**
         * @method moveToLayout moves a portlet window to layout column grid position in the browser
         * this is a client side only operation. Operates in grid (non-detached) mode.
         */
        moveToLayout : function(e) {
            var drop = e.drop.get('node'),
                drag = e.drag.get('node');
            var dragParent = drag.get('parentNode');       
            drop.appendChild(drag);
            if (dragParent.get('children').size() == 0)
            {
                //node.plug(Y.Plugin.Drag);
                var drop = new Y.DD.Drop({
                node: dragParent,
                groups: ['portlets']            
                });
            }
            // BOZO: im manipulating internal DD structures, should find a way to detach the handler
            var i = 0;
            while (i < Y.DD.DDM.targets.length) {
                if (Y.DD.DDM.targets[i] == e.drop) {
                    Y.DD.DDM.targets.splice(i, 1);
                    break;
                }
                i++;
            }
            // I don't think this is working
            e.drop.unplug(Y.Plugin.Drop);
        },
 
        /**
         * @method moveToGrid moves a portlet window to another grid position in the browser
         * this is a client side only operation. Operates in grid (non-detached) mode.
         */
        moveToGrid : function(e) {
            var portal = JETUI_YUI.getPortalInstance();
            var drop = e.drop.get('node'),
                drag = e.drag.get('node');
            var dragParent = drag.get('parentNode');
            var dropParent = drop.get('parentNode');
            if (dropParent == portal.jstbLeft || dropParent == portal.jstbRight) {
              if (!dropParent.contains(drag)) {
                  dropParent.appendChild(drag);
              }
            } else {
                if (portal.goingUp) {
                    //Y.log("going UP");
                    // var next = drop.get('previousSibling');
                    var prev = drop.previous();
                    if (prev == null) {
                        //drag.remove();                    
                        dropParent.prepend(drag);                   
                    } else {
                        //drag.remove();
                        dropParent.insertBefore(drag, drop);
                    }
                } else {
                    var next = drop.next();
                    if (next == null) {
                        //Y.log("going down APPEND");
                        //drag.remove();
                        dropParent.appendChild(drag);
                    } else {
                        //Y.log("going down: " + next); //next.data.get('name'));
                        //drag.remove();
                        dropParent.insertBefore(drag, next);
                    }
                }
            }
            if (dragParent.get('children').size() == 0) {
                var drop = new Y.DD.Drop({
                    node: dragParent,
                    groups: ['portlets']            
                });
            }                           
        },

        /**
         * @method detachPortlet detaches a portlet from a grid position and moves it to a z-order top detached window
         */
        detachPortlet : function(e) {
            var portal = JETUI_YUI.getPortalInstance();
            var windowId = null;
            if (e instanceof String) {
                windowId = e;
            } else {
                var windowId = e.currentTarget.getAttribute("id");
                windowId = windowId.replace(/^jetspeed-detach-/, "");
            }
            var window = Y.one("[id='" + windowId + "']");
            if (window) {
                Y.log("data = " + window.data.get("name"));            	
            }            
            var jetspeedZone = Y.one('#jetspeedZone');
            if (!Y.Lang.isNull(jetspeedZone)) {
        		var dragParent = window.get('parentNode');
            	var parentColumn = dragParent.data.get('column');
            	portal.reallocateColumn(parentColumn);

            	window.data.set("x", 10);
                window.data.set("y", 10);
                window.setStyle('position', 'absolute');
                window.setStyle('top', '10px');
                window.setStyle('left', '10px');
            	window.data.set('detached', true);
            	window.data.set("toolbar", true);
            	var drag = Y.DD.DDM.getDrag(window);
            	drag.removeFromGroup("portals");
            	drag.addToGroup("toolbars");
            	drag.set('dragMode', 'point');
            	var drop = Y.DD.DDM.getDrop(window);
            	var i = 0;
                while (i < Y.DD.DDM.targets.length) {
                    if (Y.DD.DDM.targets[i] == drop) {
                        Y.DD.DDM.targets.splice(i, 1);
                        break;
                    }
                    i++;
                }
                jetspeedZone.appendChild(window);
    			if (dragParent.get("children").size() == 0)
    			{
                    var drop = new Y.DD.Drop({
                        node: dragParent,
                        groups: ['portlets']            
                    });
    			}                
            }            
        },
        
        onMoveComplete : function(id, o, args) { 
            var id = id; // Transaction ID. 
            var data = o.responseText; // Response data.
            Y.log("move result = " + data);
            var windowId = args.complete[0];
        },             

        onStateComplete : function(id, o, args) { 
            var id = id; // Transaction ID. 
            var data = o.responseText; // Response data.
            Y.log("move result = " + data);
            var windowId = args.complete[0];
        },             
        
        /**
         * @method movePortlet persist the move operation to the persistent store over restful put request
         */
        movePortlet : function(drag, e) {
            var portal = JETUI_YUI.getPortalInstance();        	
            var windowId =  drag.getAttribute('id');
            if (drag.data.get("toolbar") == false) {
            	var oldColumn = drag.data.get('column');
            	var oldRow = drag.data.get('row');        	
        		var dragParent = drag.get('parentNode');
            	var parentColumn = dragParent.data.get('column');
            	if (parentColumn != oldColumn)
            	{
            		this.reallocateColumn(oldColumn); // moved from different column
            		drag.data.set('column', parentColumn);
            	}
            	portal.reallocateColumn(parentColumn);
                var uri = portal.portalContextPath + "/services/pagelayout/fragment/" + windowId + "/pos/?_type=json";
                uri += "&col=" + drag.data.get('column') + "&row=" + drag.data.get('row');
                var config = {
                        on: { complete: portal.onMoveComplete },
                        method: "PUT",
                        headers: { "X-Portal-Path" : portal.portalPagePath },
                        arguments: { complete: [ windowId ] }
                    };
                var request = Y.io(uri, config);
            }    	
            else
            {
                var uri = portal.portalContextPath + "/services/pagelayout/fragment/" + windowId + "/pos/?_type=json";
                uri += "&x=" + e.target.region.top + "&y=" + e.target.region.left + "&detached=true";
                var config = {
                        on: { complete: portal.onMoveComplete },
                        method: "PUT",
                        headers: { "X-Portal-Path" : portal.portalPagePath },
                        arguments: { complete: [ windowId ] }
                    };
                var request = Y.io(uri, config);
            }        	
        },
                
        reallocateColumn : function(column) {
    	    var columns = Y.Node.all(JetuiConfiguration.layoutStyle); 
    	    columns.each(function(v, k) {
    	    	if (v.data.get('locked') == false)
    	    	{
    		    	if (v.data.get('column') == column)
    		    	{
    		    		var row = 0;
    	    			v.get('children').each(function(v,k) {
    		    			v.data.set('row', row);
    		    			row++;
    		    		}, row);
    		    	}
    	    	}
    	    });
    	},
    	
        /**
         * @method onPortletRemoveComplete
         */
        onPortletRemoveComplete : function(id, o, args) {
            var id = id; // Transaction ID.
            var data = o.responseText; // Response data. 
            var windowId = args.complete[0];
            var window = Y.one("[id='" + windowId + "']");
            if (window) {
                var parent = window.get('parentNode');
                window.remove();
                if (parent.get('children').size() == 0)
                {
                    var drop = new Y.DD.Drop({
                        node: parent,
                        groups: ['portlets']            
                    });
                }
            }
        },
        
        /**
         * @method removePortlet removes a portlet from the persistent store over restful delete request
         */
        removePortlet : function(e) {
            var portal = JETUI_YUI.getPortalInstance();
            var windowId = null;
            if (e instanceof String) {
                windowId = e;
            } else {
                var windowId = e.currentTarget.getAttribute("id");
                windowId = windowId.replace(/^jetspeed-close-/, "");
            }
            var uri = portal.portalContextPath + "/services/pagelayout/fragment/" + windowId + "/?_type=json";
            var config = {
                    on: { complete: portal.onPortletRemoveComplete },
                    method: "DELETE",
                    headers: { "X-Portal-Path" : portal.portalServletPath + ":" + portal.portalPagePath },
                    arguments: { complete: [ windowId ] }
                };
            var request = Y.io(uri, config);
        },
        
        /**
         * @method createDecoratorActionNode
         */
        createDecoratorActionNode : function(decoAction) {
            var portal = JETUI_YUI.getPortalInstance();
            var node = Y.Node.create("<a class='action portlet-action'/>");
            var icon = Y.Node.create("<img border='0'/>");
            node.setAttribute("href", decoAction.action);
            if (decoAction.target) {
                node.setAttribute("target", decoAction.target);
            }
            node.setAttribute("title", decoAction.name);
            icon.setAttribute("src", portal.portalContextPath + "/" + decoAction.link);
            icon.setAttribute("alt", decoAction.alt);
            node.appendChild(icon);
            return node;
        },
        
        /**
         * @method onPortletDecorationReadComplete
         */
        onPortletDecorationReadComplete : function(id, o, args) {
            var portal = JETUI_YUI.getPortalInstance();
            var windowId = args.complete[0];
            var actionBarElem = args.complete[1];
            var existingActionElem = null;
            var childElems = actionBarElem.getElementsByTagName("*");
            if (childElems.size() > 0) {
                existingActionElem = childElems.item(0);
            }
            var result = null;

            try {
                result = Y.JSON.parse(o.responseText);
                if (!result) {
                    Y.log("Error: no data found.");
                    return;
                }
            } catch (e) {
                Y.log("Error: " + e.message);
                return;
            }
            
            var decoActions = result.decoratorActions;
            for (var i = 0; i < decoActions.length; i++) {
                var decoActionNode = portal.createDecoratorActionNode(decoActions[i]);
                if (existingActionElem) {
                    actionBarElem.insertBefore(decoActionNode, existingActionElem);
                } else {
                    actionBarElem.appendChild(decoActionNode);
                }
            }
        },
        
        /**
         * @method onPortletRenderComplete
         */
        onPortletRenderComplete : function(id, o, args) {
            var portal = JETUI_YUI.getPortalInstance();
            var id = id;
            var v = args.complete;
            var windowId = v.get("id");
            var titleElem = null;
            var actionBarElem = null;
            var closeElem = null;
            var contentElem = null;
            var children = v.getElementsByTagName("*");
            children.each(function(v, k) {
                if (v.hasClass("PTitleContent")) {
                    titleElem = v;
                } else if (v.hasClass("PActionBar")) {
                    actionBarElem = v;
                } else if (v.hasClass("PContent")) {
                    contentElem = v;
                } else if (/^jetspeed-close/.test("" + v.get("id"))) {
                    closeElem = v;
                }
            });
            var title = o.getResponseHeader("JS_PORTLET_TITLE");
            if (titleElem) {
                titleElem.setContent(title);
            }
            if (closeElem) {
                closeElem.setAttribute("id", "jetspeed-close-" + windowId);
                closeElem.on('click', portal.removePortlet);
            }
            var portletContent = o.responseText;
            if (contentElem) {
                contentElem.setContent(portletContent);
            }
            
            if (actionBarElem) {
                var uri = portal.portalContextPath + "/services/pagelayout/decoration/fragment/" + windowId + "/?_type=json";
                var config = {
                        on: { complete: portal.onPortletDecorationReadComplete },
                        headers: { "X-Portal-Path" : portal.portalServletPath + ":" + portal.portalPagePath },
                        arguments: { complete: [ windowId, actionBarElem ] }
                    };
                var request = Y.io(uri, config);
            }
        },
        
        /**
         * @method addPortlet
         */
        addPortlet : function(fragment) {
            var portal = JETUI_YUI.getPortalInstance();
            var templatePanel = Y.Node.one("#jsPortletTemplate");
            var v = templatePanel.cloneNode(true);
            v.setStyle('display', '');
            v.set("id", fragment.id);
            v.setAttribute("name", fragment.name);
            v.setAttribute("row", fragment.properties.row);
            v.setAttribute("column", fragment.properties.column);
            
            var portlet = Y.JetUI.Portlet.attach(v);
            var dragGroups = ['portlets'];
            var dragMode = 'intersect';
            var dropGroups  = ['portlets', 'toolbars'];
            if (portlet.get("toolbar") == false) {
                var ddNav = new Y.DD.Drag({
                    node: v,
                    groups: dragGroups,
                    dragMode: dragMode
                }).plug(Y.Plugin.DDProxy, {
                     moveOnEnd: false
                });
                ddNav.addHandle(JetuiConfiguration.dragHandleStyle);
                var drop = new Y.DD.Drop({
                    node: v,
                    groups: dropGroups            
                });
            }
            var columns = [];
            Y.Node.all(JetuiConfiguration.layoutStyle).each(function(v, k) {
                var locked = v.getAttribute("locked");
                if (!locked || "false" == locked) {
                    columns.push(v);
                }
            });
            columns[parseInt(fragment.properties.column)].appendChild(v);
            
            var uri = portal.portalContextPath + "/portlet" + portal.portalPagePath + "?entity=" + fragment.id;
            var request = Y.io(uri, { on: { complete: portal.onPortletRenderComplete }, arguments: { complete: v } } );
        }
    });
    
    /**
     * Create a portlet window to represent a portal window.
     *
     * @class JetUI.Portlet
     * @extends Base
     * @param config {Object} Configuration object
     * @constructor
     */
    Y.JetUI.Portlet = function(config) {
        Y.JetUI.Portlet.superclass.constructor.call(this, config);
    };
    
    Y.mix(Y.JetUI.Portlet, {
        
        /**
         * The identity of the widget.
         *
         * @property JetUI.Portlet.NAME
         * @type String
         * @static
         */
        NAME : 'Portlet',
        
        /**
         * Static property used to define the default attribute configuration of
         * the Widget.
         *
         * @property JetUI.Portlet.ATTRS
         * @type Object
         * @protected
         * @static
         */
        ATTRS: {
            "name" : { value: "undefined" }, 
            "id" : { value: "0" },
            "toolbar" : { value : false },
            "detached" : { value : false },
            "column" : { value : 0 },
            "row" : { value : 0 },
            "x" : { value : 0 },
            "y" : { value : 0 }
        }
    });

    Y.extend(Y.JetUI.Portlet, Y.Base, {
        
        /**
         * Construction logic executed during instantiation.
         *
         * @method initializer
         * @protected
         */
        initializer : function(cfg) { 
        },
        
        /**
         * Destruction logic executed during instantiation.
         *
         * @method initializer
         * @protected
         */
        destructor : function(cfg) { 
        },
        
        /**
         * @method info
         */
        info : function() {
            Y.log("name: " + this.get("name"));
            Y.log("id  : " + this.get("id"));       
            Y.log("toolbar  : " + this.get("toolbar"));     
            Y.log("detached  : " + this.get("detached"));     
            Y.log("col, row  : " + this.get("column") + "," + this.get("row"));     
            Y.log("x, y  : " + this.get("x") + "," + this.get("y"));     
            Y.log("---------");
        }
    });
    
    /**
     * Create a portlet window and attach the portlet window to the sepcified node.
     * @method attach
     */
    Y.JetUI.Portlet.attach = function(node) {
        var portlet = new Y.JetUI.Portlet();
        portlet.set("name", node.getAttribute("name"));
        portlet.set("id", node.getAttribute("id"));
        portlet.set("toolbar", Boolean(node.getAttribute("locked").toLowerCase() === 'true' || node.getAttribute("detached").toLowerCase() === 'true'));
        portlet.set("detached", Boolean(node.getAttribute("detached").toLowerCase() === 'true'));
        portlet.set("column", node.getAttribute("column"));
        portlet.set("row", node.getAttribute("row"));
        portlet.set("x", node.getAttribute("x"));
        portlet.set("y", node.getAttribute("y"));
        node.data = portlet;
        return portlet;
    };
    
    /**
     * Create a layout window to represent a layout window.
     *
     * @class JetUI.Layout
     * @extends Base
     * @param config {Object} Configuration object
     * @constructor
     */
    Y.JetUI.Layout = function(config) {
        Y.JetUI.Layout.superclass.constructor.call(this, config);
    };
    
    Y.mix(Y.JetUI.Layout, {
        
        /**
         * The identity of the widget.
         *
         * @property JetUI.Layout.NAME
         * @type String
         * @static
         */
        NAME : 'Layout',
        
        /**
         * Static property used to define the default attribute configuration of
         * the Widget.
         *
         * @property JetUI.Layout.ATTRS
         * @type Object
         * @protected
         * @static
         */
        ATTRS: {
            "name" : { value: "undefined" }, 
            "id" : { value: "0" },
            "nested" : { value : false },
            "column" : { value : 0 },
            "locked" : { value : false },
            "row" : { value : 0 }
        }
    });
    
    Y.extend(Y.JetUI.Layout, Y.Base, {
        
        /**
         * Construction logic executed during instantiation.
         *
         * @method initializer
         * @protected
         */
        initializer : function(cfg) { 
        },
        
        /**
         * Destruction logic executed during instantiation.
         *
         * @method initializer
         * @protected
         */
        destructor : function(cfg) { 
        },
        
        /**
         * @method info
         */
        info : function() {
            Y.log("name: " + this.get("name"));
            Y.log("id  : " + this.get("id"));       
            Y.log("nested  : " + this.get("nested"));       
            Y.log("locked  : " + this.get("locked"));       
            Y.log("col, row  : " + this.get("column") + "," + this.get("row"));     
            Y.log("---------");
        }
    });
    
    /**
     * Create a layout and attach the layout to the sepcified node.
     * @method attach
     */
    Y.JetUI.Layout.attach = function(node) {
        var layout = new Y.JetUI.Layout();
        layout.set("name", node.getAttribute("name"));
        layout.set("id", node.getAttribute("id"));
        layout.set("nested", false);
        var locked = node.getAttribute("locked");
        locked = (locked == null || locked == "false") ? false : true;          
        layout.set("locked", locked);
        layout.set("column", node.getAttribute("column"));        
        layout.set("row", 0);
        node.data = layout;
        return layout;
    };
    
}, '3.0.0', {requires:['dd', 'io', 'dataschema-json', 'node', 'node-menunav']});
