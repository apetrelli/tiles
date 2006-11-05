/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.tiles.taglib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.ComponentContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.ComponentAttribute;
import org.apache.tiles.access.TilesAccess;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Base tag for the tiles tags which interact with the container.
 * Provides standard support for security, and provides access
 * to the container and component context.
 * </p>
 * This tag takes special care to ensure that the component context is
 * reset to it's original state after the execution of the tag is
 * complete. This ensures that all all included attributes in subsequent
 * tiles are scoped properly and do not bleed outside their intended
 * scope.
 *
 * @since Tiles 2.0
 * @version $Rev$
 * 
 */
public abstract class ContainerTagSupport extends BodyTagSupport
    implements TryCatchFinally {

    /**
     * The log instance for this tag.
     */
    private static final Log LOG = LogFactory.getLog(ContainerTagSupport.class);

    private String role;
    protected TilesContainer container;
    protected ComponentContext componentContext;

    private Map<String, ComponentAttribute> originalState;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int doStartTag() {
        container = TilesAccess.getContainer(pageContext.getServletContext());
        componentContext = container.getComponentContext(pageContext);
        cacheState();
        return isAccessAllowed() ? EVAL_BODY_BUFFERED : SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        if (isAccessAllowed()) {
            try {
                execute();
            } catch (TilesException e) {
                String message = "Error executing tag: " + e.getMessage();
                LOG.error(message, e);
                throw new JspException(message, e);
            } catch (IOException io) {
                String message = "IO Error executing tag: " + io.getMessage();
                LOG.error(message, io);
                throw new JspException(message, io);
            }
        }
        return EVAL_PAGE;
    }


    public void doCatch(Throwable throwable) throws Throwable {
        // noop;
    }

    public void doFinally() {
        restoreState();
    }

    public void release() {
        super.release();
        this.role = null;
        this.container = null;
        this.componentContext = null;
        originalState = null;
    }

    protected abstract void execute() throws TilesException, JspException, IOException;

    protected boolean isAccessAllowed() {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        return (role == null || req.isUserInRole(role));
    }

    private void cacheState() {
        originalState = new HashMap<String, ComponentAttribute>();
        Iterator<String> i = componentContext.getAttributeNames();
        while(i.hasNext()) {
            String name = i.next();
            ComponentAttribute original = componentContext.getAttribute(name);
            ComponentAttribute a = new ComponentAttribute(
                original.getValue(), original.getRole(), original.getType()
            );
            originalState.put(name, a);
        }
    }

    private void restoreState() {
        originalState.clear();
        originalState.putAll(originalState);
    }
}