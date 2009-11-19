//Use loader to grab the modules needed
YUI(yuiConfig).use('console', 'dd', 'anim', 'io', 'datatype-xml', 'dataschema-xml', 'node-base', 'node-menunav', function(Y) {
	
	//	Retrieve the Node instance representing the root menu
	//	(<div id="environments-menu">) and call the "plug" method
	//	passing in a reference to the MenuNav Node Plugin.

	var menu = Y.one("#environments-menu");

	menu.plug(Y.Plugin.NodeMenuNav);

	//	Show the menu now that it is ready

	menu.setStyle("display","inline");
	
	//new Y.Console().render(); 
    //Make this an Event Target so we can bubble to it
    var Portal = function() {
        Portal.superclass.constructor.apply(this, arguments);
    };
    Portal.NAME = 'portal';
    Portal.prototype.desktopMode = false;
    Portal.prototype.jstbLeft;
    Portal.prototype.jstbRight;
    Portal.prototype.isMoving;
    Y.extend(Portal, Y.Base);
    var portal = new Portal();
    portal.isMoving = false;
    var goingUp = false, goingRight = false, lastY = 0, lastX = 0;   
    
    ////////////////////////////////////////////////////
    // the Portlet Class
    function Portlet(config) {
        Portlet.superclass.constructor.call(this, config);
    };
    Y.extend(Portlet, Y.Base, {
    	initializer : function(cfg) { 
   	 	},
        destructor : function(cfg) { 
   	 	}     	    	
    });    
    Portlet.NAME = "portlet";
    Portlet.ATTRS = {
    	"name" : { value: "undefined" }, 
        "id" : { value: "0" },
        "toolbar" : { value : false },
        "detached" : { value : false },
        "column" : { value : 0 },
        "row" : { value : 0 }
    };
	Portlet.prototype.info = function() {
		Y.log("name: " + this.get("name"));
		Y.log("id  : " + this.get("id"));		
		Y.log("toolbar  : " + this.get("toolbar"));		
		Y.log("col, row  : " + this.get("column") + "," + this.get("row"));		
		Y.log("---------");
    };

    ////////////////////////////////////////////////////
    // the Layout Class
    function Layout(config) {
        Layout.superclass.constructor.call(this, config);
    };
    Y.extend(Layout, Y.Base, {
    	initializer : function(cfg) { 
   	 	},
        destructor : function(cfg) { 
   	 	}     	    	
    });    
    Layout.NAME = "layout";
    Layout.ATTRS = {
    	"name" : { value: "undefined" }, 
        "id" : { value: "0" },
        "nested" : { value : false },
        "column" : { value : 0 },
        "row" : { value : 0 }
    };
	Layout.prototype.info = function() {
		Y.log("name: " + this.get("name"));
		Y.log("id  : " + this.get("id"));		
		Y.log("nested  : " + this.get("nested"));		
		Y.log("col, row  : " + this.get("column") + "," + this.get("row"));		
		Y.log("---------");
    };
    
    ////////////////////////////////////////////////////    
    // Create Navigator Portlet
    var navigator = new Portlet();
    navigator.set("name", "j2-admin::PageNavigator");
    navigator.set("id", "_PageNavigator");
    navigator.set("toolbar", true);
    navigator.set("detached", false);
    var toolbox = new Portlet();
    toolbox.set("name", "j2-admin::JetspeedToolbox");
    toolbox.set("id", "_JetspeedToolbox");
    toolbox.set("toolbar", true);
    toolbox.set("detached", false);

//    var toggleMoveMode = function(e) { 
//	    window.location = window.location + "?toggle=move";
//    }       
//    Y.on("click", toggleMoveMode, "#jstbMoveMode");
    
    ////////////////////////////////////////////////////    
    // setup toolbar docking area and togglers    
    var lhsToggler = Y.one('#jstbLeftToggle');
    var rhsToggler = Y.one('#jstbRightToggle');    
    // add fx plugin to docking area
    portal.jstbLeft = Y.one('#jstbLeft').plug(Y.Plugin.NodeFX, {
        from: { width: 1 },
        to: {
            width: function(node) { // dynamic in case of change
                return 200; //node.get('scrollWidth'); // get expanded height (offsetHeight may be zero)
            }
        },
        easing: Y.Easing.easeOut,
        duration: 0.3
    });    
    portal.jstbRight = Y.one('#jstbRight').plug(Y.Plugin.NodeFX, {
        from: { width: 1 },
        to: {
            width: function(node) { // dynamic in case of change
                return 200; //node.get('scrollWidth'); // get expanded height (offsetHeight may be zero)
            }
        },
        easing: Y.Easing.easeOut,
        duration: 0.3
    });        
    var onClickToolbar = function(e) {
//    	nav.setStyle('z-index', '500');
    }

    var onClickToggler = function(e) {
    	var id = e.target.getAttribute('id');
    	var toggler = lhsToggler;
    	var toolbar = portal.jstbLeft;
    	var compareStyle = 'jstbToggle1';
    	if (id.indexOf('Left') == -1)
    	{
    		toggler = rhsToggler;
    		toolbar = portal.jstbRight;
        	var compareStyle = 'jstbToggle2';    		
    	}
    	portal.toggleToolbar(toolbar, toggler, compareStyle);    	    	
    };
    lhsToggler.on('click', onClickToggler);
    rhsToggler.on('click', onClickToggler);

    ////////////////////////////////////////////////////       
    // drag and drop
    var nav = Y.one('#jsNavigator');
    nav.data = navigator;
    var ddNav = new Y.DD.Drag({
        node: nav,
        groups: ['toolbars'],
        dragMode: 'point'                
    }).plug(Y.Plugin.DDProxy, { 
      	 moveOnEnd: false         	    	
    });    
    ddNav.addHandle('.PTitle');
    nav.on('click', onClickToolbar);

    var jetspeedZone = Y.one('#jetspeedZone');
    var jzDrop = new Y.DD.Drop({
        node: jetspeedZone,
        groups: ['toolbars']        
    });
    
    var tb = Y.one('#jsToolbox');
    tb.data = toolbox;
    var ddToolbox = new Y.DD.Drag({
        node: tb,
        groups: ['toolbars'],                 
        dragMode: 'point'        
    }).plug(Y.Plugin.DDProxy, { 
      	 moveOnEnd: false         	    	
    });    
    ddToolbox.addHandle('.PTitle');
    tb.on('click', onClickToolbar);
    
    var drop = new Y.DD.Drop({
        node: Y.one('#jstbLeft'),
        groups: ['toolbars']
    });
    var drop = new Y.DD.Drop({
        node: Y.one('#jstbRight'),
        groups: ['toolbars']        
    });
    
    var draggablePortlets = Y.Node.all('.portal-layout-cell');    
    draggablePortlets.each(function(v, k) {
        var portlet = new Portlet();
        portlet.set("name", v.getAttribute("name"));
        portlet.set("id", v.getAttribute("id"));
        portlet.set("toolbar", false);
        portlet.set("detached", false);
        portlet.set("column", v.getAttribute("column"));
        portlet.set("row", v.getAttribute("row"));
        v.data = portlet;
        var ddNav = new Y.DD.Drag({
            node: v,
            groups: ['portlets'],
            dragMode: 'intersect'                    
        }).plug(Y.Plugin.DDProxy, { 
          	 moveOnEnd: false         	    	
        });    
        ddNav.addHandle('.PTitle');
    	var drop = new Y.DD.Drop({
            node: v,
            groups: ['portlets', 'toolbars']            
        });        
    	//portlet.info();
    });
    
    var dropLayouts = Y.Node.all('.portal-layout-column');
    dropLayouts.each(function(v, k) {
        var layout = new Layout();
        layout.set("name", v.getAttribute("name"));
        layout.set("id", v.getAttribute("id"));
        layout.set("nested", false);
        layout.set("column", v.getAttribute("column"));        
        layout.set("row", 0);
        v.data = layout;
        //layout.info();
        if (v.get('children').size() == 0)
        {
	    	var drop = new Y.DD.Drop({
	        node: v,
	        groups: ['portlets']            
	    	});
        }
    });

    var onRemoveComplete = function(id, o, args) { 
    	var id = id; // Transaction ID. 
    	var data = o.responseText; // Response data. 
    	var widgetId = args[0];
    	// now remove it from the dom
    	var widget = Y.one("#" + widgetId);
    	if (widget)
    	{
    		var parent = widget.get('parentNode');
    		widget.remove();
	        if (parent.get('children').size() == 0)
	        {
	        	//node.plug(Y.Plugin.Drag);
		    	var drop = new Y.DD.Drop({
		        node: parent,
		        groups: ['portlets']            
		    	});
	        }
    	}
    };     
    
    var onClickRemove = function(e) {
    	var uri = document.location.href;
    	uri = uri.replace("/ui", "/ajaxapi");
    	var windowId =  e.currentTarget.getAttribute('id');
    	windowId = windowId.replace("jetspeed-close-", "");
    	var uri = uri + "?action=remove&id=" + windowId;
        Y.on('io:complete', onRemoveComplete, this, [windowId]); 
        var request = Y.io(uri); 
    };

    var closeWindows = Y.Node.all('.portlet-action-close');    
    closeWindows.each(function(v, k) {
        v.on('click', onClickRemove);
    });
        
    Portal.prototype.toggleToolbar = function(toolbar, toggler, compareStyle) {
        toggler.toggleClass('jstbToggle1');
        toggler.toggleClass('jstbToggle2');
        var currentStyle = toggler.getAttribute('class');
        var nodelist = toolbar.get('children');
        if (currentStyle == compareStyle)
        {
            nodelist.setStyle('display', 'block');        	
        }
        else
        {
            nodelist.setStyle('display', 'none');        	
        }	        
        toolbar.fx.set('reverse', !toolbar.fx.get('reverse')); // toggle reverse 
        toolbar.fx.run();
	};	   
    
	Y.DD.DDM.on('drag:drophit', function(e) {
		var drop = e.drop.get('node'),
            drag = e.drag.get('node');
        if (drag.data.get("toolbar"))
        {        	
            if (drop == portal.jstbLeft || drop == portal.jstbRight)
            {
	        	drag.setStyle('position', '');
				drag.setStyle('top', '');
				drag.setStyle('left', '');        		
				drop.appendChild(drag);
            }
            else
            {            		
				var dragParent = drag.get('parentNode');
            	drag.setStyle('position', 'absolute');
				drag.setStyle('top', e.drag.region.top + "px");
				drag.setStyle('left', e.drag.region.left + "px");        		
				jetspeedZone.appendChild(drag);
				if (dragParent.get("children").size() == 0)
				{        	        
				    if (dragParent == portal.jstbLeft) {
						portal.toggleToolbar(dragParent, lhsToggler, "jstbToggle1");
				    }
				    else  if (dragParent == portal.jstbRight) {
				    	portal.toggleToolbar(dragParent, rhsToggler, "jstbToggle2");        	
				    }
				}              				
            }
        }
        else
        {
        }
    });
	
    Portal.prototype.moveToLayout = function(e)
    {
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
    }

	  Portal.prototype.movePortlet = function(e)
	  {
        var drop = e.drop.get('node'),
            drag = e.drag.get('node');
        var dragParent = drag.get('parentNode');
        var dropParent = drop.get('parentNode');
        if (dropParent == portal.jstbLeft || dropParent == portal.jstbRight)
        {
          if (!dropParent.contains(drag)) {
        	  dropParent.appendChild(drag);
          }
        }
        else
        {
        	if (goingUp)
        	{
    			//Y.log("going UP");
        		// var next = drop.get('previousSibling');
    			var prev = drop.previous();
                if (prev == null)
                {
                	//drag.remove();                	
                	dropParent.prepend(drag);                	
                }
                else
                {
        			//drag.remove();
                	dropParent.insertBefore(drag, drop);
                }        		
        	}
        	else
        	{
        		var next = drop.next();
                if (next == null) 
                {
        			//Y.log("going down APPEND");
        			//drag.remove();
        			dropParent.appendChild(drag);
                }
                else
                {
        			//Y.log("going down: " + next); //next.data.get('name'));
        			//drag.remove();
        			dropParent.insertBefore(drag, next);
                }
        	}
        }
        if (dragParent.get('children').size() == 0)
        {
	    	var drop = new Y.DD.Drop({
	        node: dragParent,
	        groups: ['portlets']            
	    	});
        }        
    };    

    var onMoveComplete = function(id, o, args) { 
    	var id = id; // Transaction ID. 
    	var data = o.responseText; // Response data.
    	Y.log("move result = " + data);
    	var dataIn = Y.DataType.XML.parse(data),
    		schema = {  resultListLocator: "status", resultFields: [{key:"status"}] },
    		dataOut = Y.DataSchema.XML.apply(schema, dataIn);
		Y.log("data = " + dataOut)                
    	var widgetId = args[0];
    };     
    
    Y.DD.DDM.on('drag:end', function(e) {
        var drag = e.target;
        if (drag.target) {
            drag.target.set('locked', false);
        }
        if (drag.get('node').data.get("toolbar"))
        {
            drag.get('node').setStyle('visibility', '');        	
        }
        else
        {
        	drag.get('node').get('children').setStyle('visibility', '');
        }        
        //drag.get('node').setStyle('border', '');                
        drag.get('node').removeClass('moving');
        drag.get('dragNode').set('innerHTML', '');

        persistMove(drag.get('node'));
    });
    
    var persistMove = function(drag) {
        if (drag.data.get("toolbar") == false) {
        	var uri = document.location.href;
        	uri = uri.replace("/ui", "/ajaxapi");
        	var windowId =  drag.getAttribute('id');
        	var oldColumn = drag.data.get('column');
        	var oldRow = drag.data.get('row');        	
    		var dragParent = drag.get('parentNode');
        	var parentColumn = dragParent.data.get('column');
        	if (parentColumn != oldColumn)
        	{
        		reallocateColumn(oldColumn); // moved from different column
        		drag.data.set('column', parentColumn);
        	}
        	reallocateColumn(parentColumn);
        	var uri = uri + "?action=moveabs&id=" + windowId + "&col=" + drag.data.get('column') + "&row=" + drag.data.get('row');
            Y.on('io:complete', onMoveComplete, this, [windowId]); 
            var request = Y.io(uri);         	    	
        }    	
    };
	var reallocateColumn = function(column) {
	    var columns = Y.Node.all('.portal-layout-column');
	    columns.each(function(v, k) {
	    	if (v.data.get('column') == column)
	    	{
	    		var row = 0;
    			v.get('children').each(function(v,k) {
	    			v.data.set('row', row);
	    			row++;
	    		}, row);
	    	}
	    });
	};    
    
    Y.DD.DDM.on('drag:start', function(e) {
        var drag = e.target;
        var dragNode = drag.get('dragNode');
        var srcNode = drag.get('node');
        dragNode.set('innerHTML', srcNode.get('innerHTML'));

        if (drag.get('node').data.get("toolbar"))
        {
        	drag.get('node').setStyle('visibility', 'hidden');        	
        }
        else
        {
        	srcNode.get('children').setStyle('visibility', 'hidden');
            srcNode.addClass('moving');        	
        }
        //  drag.get('node').setStyle('border', '1px dotted #black');        
        lastX = drag.mouseXY[0];
        lastY = drag.mouseXY[1];
        //Y.log("starting drag " + lastX +  " , " + lastY);
    });

    Y.DD.DDM.on('drag:over', function(e) {
    	if (portal.isMoving)
    		return;
    	
    	var x = e.drag.mouseXY[0],
    		y = e.drag.mouseXY[1];
    	
    	if (y == lastY)
    	{    		
    	}
    	else if (y < lastY) {
            goingUp = true;
            
        } else {
            goingUp = false;
        }
        lastY = y;
        if (x < lastX) {
            goingRight = false;
        } else {
            goingRight = true;
        }        
        lastX = x;
        
        if (e.drag.get('node').data.get("toolbar"))
        {        
            var drop = e.drop.get('node'),
            drag = e.drag.get('node');            
            var dragParent = drag.get('parentNode');
            var dropParent = drop.get('parentNode');        	
            if (drop == portal.jstbLeft || drop == portal.jstbRight)
            {
				if (!drop.contains(drag)) {
					  drop.appendChild(drag);
				}
				// close up the toolbar leaving from
				if (dragParent.get("children").size() == 0)
				{        	        
				    if (dragParent == portal.jstbLeft) {
						portal.toggleToolbar(dragParent, lhsToggler, "jstbToggle1");
				    }
				    else  if (dragParent == portal.jstbRight) {
				    	portal.toggleToolbar(dragParent, rhsToggler, "jstbToggle2");        	
				    }
				}              
            }        	
            else // jetspeed drop zone
            {
            	//Y.log("hovering over the zone");
            }
        }
        else
        {
	    	var region = e.drop.get('node').get('region');
	    	if (e.drop.get('node').data.name == "portlet")
	    	{
		    	var srcRegion = e.drag.get('node').get('region');
		    	if (y >= srcRegion.top && y <= srcRegion.bottom && x >= srcRegion.left && x <= srcRegion.right)
		    	{
	//	    		Y.log("dragging over src");
		    	}	    	
		    	else if (y >= region.top && y <= region.bottom && x >= region.left && x <= region.right)
		    	{
		    		//Y.log("**** HIT");
		    		portal.isMoving = true;
		    		portal.movePortlet(e); 
		    		portal.isMoving = false;
		    	}
	    	}
	    	else if (e.drop.get('node').data.name == "layout")
	    	{
	    		portal.isMoving = true;
	    		portal.moveToLayout(e);
	    		portal.isMoving = false;
	    	}
	    	
        }
		//Y.log("x,y = " + x + "," + y);
    	
    });
    
//    Y.DD.DDM.on('drag:drag', function(e) {
//        var x = e.target.mouseXY[0];
//    	var y = e.target.mouseXY[1];
//        if (y < lastY) {
//            goingUp = true;
//        } else {
//            goingUp = false;
//        }
//        if (x < lastX) {
//            goingRight = false;
//        } else {
//            goingRight = true;
//        }        
//        lastX = x;
//        Y.log("DRAG: x = " + x + " y " + y );
//    });
    
//    Y.DD.DDM.on('drop:enter', function(e) {
//    
//    	//var region = e.drop.region;
//    	var region = e.drop.get('node').get('region');     	
//    	Y.log("region = " + region.top + "," + region.bottom + " : " + region.left + ","  +region.right);
    
//        if (!e.drag || !e.drop || (e.drop !== e.target)) {
//            return false;
//        }
        //var id = e.drop.get('node').data.get('id');
        //Y.log("entering: " + p);
        
//        if (e.drop.get('node').get('tagName').toLowerCase() === 'li') {
//            if (e.drop.get('node').hasClass('item')) {
//                _moveMod(e.drag, e.drop);
//            }
//        }
//    });    
    
    
});
