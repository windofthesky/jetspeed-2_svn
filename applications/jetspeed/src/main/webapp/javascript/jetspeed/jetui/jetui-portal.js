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
        NAME : 'Portal'
        
    });
    
    Y.extend(Y.JetUI.Portal, Y.Base, {
        
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
            toggler.toggleClass('jstbToggle1');
            toggler.toggleClass('jstbToggle2');
            var currentStyle = toggler.getAttribute('class');
            var nodelist = toolbar.get('children');
            if (currentStyle == compareStyle) {
                nodelist.setStyle('display', 'block');          
            } else {
                nodelist.setStyle('display', 'none');           
            }
            toolbar.fx.set('reverse', !toolbar.fx.get('reverse')); // toggle reverse 
            toolbar.fx.run();
        },
        
        /**
         * @method moveToLayout
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
         * @method movePortlet
         */
        movePortlet : function(e) {
            if (!JETUI_YUI || !JETUI_YUI.portalInstance)
                return;
            
            var portal = JETUI_YUI.portalInstance;
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
            "row" : { value : 0 }
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
            Y.log("col, row  : " + this.get("column") + "," + this.get("row"));     
            Y.log("---------");
        }
    });

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
    
}, '3.0.0', {requires:['dd', 'io', 'dataschema-json', 'node-base', 'node-menunav']});
