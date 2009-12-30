//Use loader to grab the modules needed
YUI(JETUI_YUI).use('jetui-portal', 'console', 'dd', 'io', 'datatype-xml', 'dataschema-xml', 'dataschema-json', 'node', 'node-menunav', function(Y) {

    var portal = JETUI_YUI.getPortalInstance();
	var config = JETUI_YUI.config;
    Y.log("Starting up JETUI " +  config.engine + " engine...");        
    //if (config.engine == Y.JetUI.Portal.CSRE)
    	
    ////////////////////////////////////////////////////    
    // Create Navigator Portlet
    var navigator = new Y.JetUI.Portlet();
    navigator.set("name", "j2-admin::PageNavigator");
    navigator.set("id", "_PageNavigator");
    navigator.set("toolbar", true);
    navigator.set("detached", false);
    var toolbox = new Y.JetUI.Portlet();
    toolbox.set("name", "j2-admin::JetspeedToolbox");
    toolbox.set("id", "_JetspeedToolbox");
    toolbox.set("toolbar", true);
    toolbox.set("detached", false);
    
    ////////////////////////////////////////////////////    
    // setup toolbar docking area and togglers    
    var lhsToggler = Y.one('#jstbLeftToggle');
    var rhsToggler = Y.one('#jstbRightToggle');    
    portal.jstbLeft = Y.one('#jstbLeft');
	if (!Y.Lang.isNull(portal.jstbLeft)) {
   	   var currentStyle = portal.jstbLeft.getStyle('display');
       var nodelist = portal.jstbLeft.get('children');
       nodelist.setStyle('display', currentStyle);          
	}
    portal.jstbRight = Y.one('#jstbRight')
	if (!Y.Lang.isNull(portal.jstbRight)) {
   	   var currentStyle = portal.jstbRight.getStyle('display');
       var nodelist = portal.jstbRight.get('children');
       nodelist.setStyle('display', currentStyle);          
	}
    var onClickToolbar = function(e) {
//    	Y.log("clickd on e.target = " + e.target);
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
    if (!Y.Lang.isNull(lhsToggler)) {
    	lhsToggler.on('click', onClickToggler);
    }
    if (!Y.Lang.isNull(rhsToggler)) {
    	rhsToggler.on('click', onClickToggler);
    }
    ////////////////////////////////////////////////////       
    // drag and drop
    var nav = Y.one("[id='template-top2.jsPageNavigator']"); 
    if (!Y.Lang.isNull(nav)) {
	    nav.data = navigator;
	    var ddNav = new Y.DD.Drag({
	        node: nav,
	        groups: ['toolbars'],
	        dragMode: 'point'                
	    }).plug(Y.Plugin.DDProxy, { 
	      	 moveOnEnd: false         	    	
	    });    
	    ddNav.addHandle(config.dragHandleStyle);
	    nav.on('click', onClickToolbar);
    }    
    var jetspeedZone = Y.one('#jetspeedZone');
    if (!Y.Lang.isNull(jetspeedZone)) {   
	    var jzDrop = new Y.DD.Drop({
	        node: jetspeedZone,
	        groups: ['toolbars']        
	    });
    }    
    var tb = Y.one('#jsToolbox');
    if (!Y.Lang.isNull(tb)) {    
	    tb.data = toolbox;
	    var ddToolbox = new Y.DD.Drag({
	        node: tb,
	        groups: ['toolbars'],                 
	        dragMode: 'point'        
	    }).plug(Y.Plugin.DDProxy, { 
	      	 moveOnEnd: false         	    	
	    });    
	    ddToolbox.addHandle(config.dragHandleStyle); 
	    tb.on('click', onClickToolbar);
    }
    var jstbLeft = Y.one('#jstbLeft');
    if (!Y.Lang.isNull(jstbLeft)) {    
	    var drop = new Y.DD.Drop({
	        node: jstbLeft,
	        groups: ['toolbars']
	    });
    }
    var jstbRight = Y.one('#jstbRight');
    if (!Y.Lang.isNull(jstbRight)) {    
	    var drop = new Y.DD.Drop({
	        node: jstbRight,
	        groups: ['toolbars']        
	    });
    }
	var draggablePortlets = Y.Node.all(config.portletStyle);    
    draggablePortlets.each(function(v, k) {
        var portlet = Y.JetUI.Portlet.attach(v);
    	//Y.log("portlet = " + v.getAttribute("name") + v.getAttribute("id") + "locked = " + v.getAttribute("locked"));
        var dragGroups = ['portlets'];
        var dragMode = 'intersect';
        var dropGroups  = ['portlets', 'toolbars'];
        if (portlet.get("toolbar") == true || portlet.get("detached") == true) {
	        dragGroups = ['toolbars'],	        
	        dragMode = 'point';
	        dropGroups = [];
        }        
        var ddNav = new Y.DD.Drag({
            node: v,
            groups: dragGroups,
            dragMode: dragMode                    
        }).plug(Y.Plugin.DDProxy, { 
          	 moveOnEnd: false         	    	
        });    
        ddNav.addHandle(config.dragHandleStyle);
    	var drop = new Y.DD.Drop({
            node: v,
            groups: dropGroups            
        });
    	// portlet.info();
    });
    
    var dropLayouts = Y.Node.all(config.layoutStyle); 
    dropLayouts.each(function(v, k) {
    	//Y.log("layout = " + v.getAttribute("name") + v.getAttribute("id"));
        var layout = Y.JetUI.Layout.attach(v);
        //layout.info();
        if (v.get('children').size() == 0)
        {
	    	var drop = new Y.DD.Drop({
	        node: v,
	        groups: ['portlets']            
	    	});
        }
    });
    
    var closeWindows = Y.Node.all('.portlet-action-close');
    closeWindows.each(function(v, k) {
        v.on('click', portal.removePortlet);
    });

    var detachWindows = Y.Node.all('.portlet-action-detach');
    detachWindows.each(function(v, k) {
        v.on('click', portal.detachPortlet);
    });
    
	Y.DD.DDM.on('drag:drophit', function(e) {
	    var portal = JETUI_YUI.getPortalInstance();
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

        portal.movePortlet(drag.get('node'), e);
    });        	
    
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
        portal.lastX = drag.mouseXY[0];
        portal.lastY = drag.mouseXY[1];
    });

    Y.DD.DDM.on('drag:over', function(e) {
        var portal = JETUI_YUI.getPortalInstance();
    	if (portal.isMoving)
    		return;
    	
    	var x = e.drag.mouseXY[0],
    		y = e.drag.mouseXY[1];
    	
    	if (y == portal.lastY)
    	{    		
    	}
    	else if (y < portal.lastY) {
            portal.goingUp = true;
            
        } else {
            portal.goingUp = false;
        }
    	portal.lastY = y;
        if (x < portal.lastX) {
            portal.goingRight = false;
        } else {
            portal.goingRight = true;
        }        
        portal.lastX = x;
        
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
	    	if (e.drop.get('node').data.name == "Portlet")
	    	{
		    	var srcRegion = e.drag.get('node').get('region');
		    	if (y >= srcRegion.top && y <= srcRegion.bottom && x >= srcRegion.left && x <= srcRegion.right)
		    	{
		    		//Y.log("dragging over src");
		    	}	    	
		    	else if (y >= region.top && y <= region.bottom && x >= region.left && x <= region.right)
		    	{
		    		// Y.log("**** HIT");
		    		portal.isMoving = true;
		    		portal.moveToGrid(e); 
		    		portal.isMoving = false;
		    	}
	    	}
	    	else if (e.drop.get('node').data.name == "Layout")
	    	{
	    		portal.isMoving = true;
	    		portal.moveToLayout(e);
	    		portal.isMoving = false;
	    	}
	    	
        }
		//Y.log("x,y = " + x + "," + y);
    	
    });
    
});