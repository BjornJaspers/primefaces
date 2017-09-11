/**
 * Copyright 2009-2017 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.terminal;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRuleset;

import org.primefaces.facelets.MethodRule;
import org.primefaces.model.TreeNode;

public class TerminalHandler extends ComponentHandler {

    private static final MethodRule COMMAND_HANDLER =
            new MethodRule("commandHandler", String.class, new Class[]{ String.class, String[].class });

    private static final MethodRule COMMAND_MODEL =
            new MethodRule("commandModel", TreeNode.class, new Class[]{});

    public TerminalHandler(ComponentConfig config) {
        super(config);
    }

    @SuppressWarnings("unchecked")
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);
        metaRuleset.addRule(COMMAND_HANDLER);
        metaRuleset.addRule(COMMAND_MODEL);

        return metaRuleset;
    }

}
