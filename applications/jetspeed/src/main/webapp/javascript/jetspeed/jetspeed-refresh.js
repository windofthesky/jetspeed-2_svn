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
 * author: taylor
 */

var jetspeedRefresher = (function() {

    return {
        reloadPortlet : function(self, id, name, msWait, pContent) {
            self.getContent(id, name, pContent);
            setTimeout(self.reloadPortlet, msWait, self, id, name, msWait, pContent);
        },

        reloadFunction : function(self, id, name, msWait, f, pContent) {
            if (f.indexOf(".") == -1) {
                if (f.indexOf("angular") == 0) {
                    var arguments = f.split(":");
                    angular.element(document.getElementById(arguments[1])).scope().refresh();
                }
                else {
                    window[f](id, name); // global function
                }
            }
            else {
                var namespaced = f.split(".");
                window[namespaced[0]][namespaced[1]](id, name);
            }
            setTimeout(self.reloadFunction, msWait, self, id, name, msWait, f, pContent);
        },

        load : function(layoutCell, pContent) {
            var x = document.getElementsByClassName(layoutCell);
            var i = 0;
            var self = this;
            for (i = 0; i < x.length; i++) {
                if (x[i].getAttribute('refreshRate')) {
                    if (x[i].getAttribute('refreshFunction')) {
                        setTimeout(this.reloadFunction, x[i].getAttribute('refreshRate'), self, x[i].id, x[i].getAttribute('name'),
                            x[i].getAttribute('refreshRate'), x[i].getAttribute('refreshFunction'), pContent);
                    }
                    else {
                        setTimeout(self.reloadPortlet, x[i].getAttribute('refreshRate'), self, x[i].id, x[i].getAttribute('name'),
                            x[i].getAttribute('refreshRate'), pContent);
                    }
                }
            }
        },

        getContent : function(id, name, pContent) {
            this.ajax("/jetspeed/portlet?entity=" + id + "&portlet=" + name + "&skipHead=true", function(data)
            {
                var container = document.getElementById(id);
                var inner = container.getElementsByClassName(pContent);
                inner[0].innerHTML = "<span style=\"line-height:0.005px;\">&nbsp;</span>" + data;
            });
        },

        ajax : function(url, callback) {
            if(typeof jQuery != 'undefined')
            {
                jQuery.ajax({
                    url: url,
                    success: function(data, status, xhr)
                    {
                        if(callback)
                        {
                            callback(data);
                        }
                    }
                });
            }
            else
            {
                var xmlhttp = null;

                if(typeof XMLHttpRequest!='undefined')
                {
                    xmlhttp = new XMLHttpRequest();
                }
                else
                {
                    try
                    {
                        xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
                    }
                    catch (e)
                    {
                        try
                        {
                            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                        }
                        catch (E)
                        {
                            xmlhttp = null;
                        }
                    }
                }

                if(xmlhttp)
                {
                    xmlhttp.open('GET', url, true);

                    xmlhttp.onreadystatechange = function()
                    {
                        if (xmlhttp.readyState == 4)
                        {
                            if(xmlhttp.status == 200)
                            {
                                if(callback)
                                {
                                    callback(xmlhttp.responseText);
                                }
                            }
                        }
                    };

                    xmlhttp.send(null);
                }
            }
        }
    }
})();
