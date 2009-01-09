/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is JSwat. The Initial Developer of the Original
 * Software is Nathan L. Fiedler. Portions created by Nathan L. Fiedler
 * are Copyright (C) 2005-2007. All Rights Reserved.
 *
 * Contributor(s): Nathan L. Fiedler.
 *
 * $Id$
 */

package com.bluemarsh.jswat.nodes.variables;

import com.bluemarsh.jswat.core.session.Session;
import com.bluemarsh.jswat.core.session.SessionProvider;
import com.bluemarsh.jswat.core.watch.Watch;
import com.bluemarsh.jswat.core.watch.WatchFactory;
import com.bluemarsh.jswat.core.watch.WatchManager;
import com.bluemarsh.jswat.core.watch.WatchProvider;
import com.bluemarsh.jswat.nodes.variables.VariableNode.Kind;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Implements the action of adding the selected fields to the
 * Watches window.
 *
 * @author  Nathan Fiedler
 */
public class AddWatchAction extends NodeAction {
    /** silence the compiler warnings */
    private static final long serialVersionUID = 1L;

    protected boolean asynchronous() {
        // performAction() should run in event thread
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length > 0) {
            boolean enable = true;
            // For now, just allow watching objects.
            for (Node n : activatedNodes) {
                if (!(n instanceof VariableNode)) {
                    enable = false;
                    break;
                }
            }
            return enable;
        } else {
            return false;
        }
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public String getName() {
        return NbBundle.getMessage(AddWatchAction.class,
                "LBL_WatchAction_Name");
    }

    protected void performAction(Node[] activatedNodes) {
        Session session = SessionProvider.getCurrentSession();
        WatchManager wm = WatchProvider.getWatchManager(session);
        WatchFactory wf = WatchProvider.getWatchFactory();
        for (Node n : activatedNodes) {
            if (n instanceof VariableNode) {
                VariableNode vn = (VariableNode) n;
                StringBuilder name = new StringBuilder();
                if (vn.getKind() == Kind.THIS) {
                    // Shortcut for 'this' node.
                    name.append("this");
                } else {
                    // Otherwise, build out a complete reference.
                    name.insert(0, vn.getName());
                    Node node = vn.getParentNode();
                    while (node != null && node instanceof VariableNode &&
                            ((VariableNode) node).getKind() != Kind.THIS) {
                        // Add period separator for all but array access.
                        if (name.charAt(0) != '[') {
                            name.insert(0, '.');
                        }
                        name.insert(0, node.getName());
                        node = node.getParentNode();
                    }
                }
                Watch w = wf.createExpressionWatch(name.toString());
                wm.addWatch(w);
            }
        }
    }
}
