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
 * are Copyright (C) 2005. All Rights Reserved.
 *
 * Contributor(s): Nathan L. Fiedler.
 *
 * $Id: DefaultRuntime.java 17 2007-12-24 01:54:00Z nfiedler $
 */

package com.bluemarsh.jswat.core.runtime;

import com.bluemarsh.jswat.core.util.Processes;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.ErrorManager;

/**
 * The default implementation of a JavaRuntime.
 *
 * @author Nathan Fiedler
 */
public class DefaultRuntime implements JavaRuntime {
    /** Unique identifier for this instance. */
    private String runtimeIdentifier;
    /** Base directory of this runtime. */
    private File baseDirectory;
    /** Contains the source code for the Java core classes. */
    private List<String> sourceCodes;
    /** Name of runtime executable. */
    private String runtimeExec;
    /** Name of this runtime; not to be persisted. */
    private transient String cachedName;

    public JavaRuntime clone() {
        try {
            // Make a deep copy of this runtime for the sake of editing.
            DefaultRuntime clone = (DefaultRuntime) super.clone();
            if (sourceCodes != null) {
                clone.sourceCodes = new ArrayList<String>();
                clone.sourceCodes.addAll(sourceCodes);
            }
            return clone;
        } catch (CloneNotSupportedException cnse) {
            // This cannot happen, let the code throw an NPE.
            return null;
        }
    }

    public File findExecutable(File base, String exec) {
        if (base == null) {
            return null;
        }
        if (exec == null) {
            return null;
        }
        File bindir = new File(base, "bin");
        if (!bindir.exists()) {
            // Fall back to the base directory in the hopes that it
            // will suffice for this particular runtime.
            bindir = base;
        }
        File execf = new File(bindir, exec);
        if (!execf.exists()) {
            return null;
        }
        return execf;
    }

    public String getBase() {
        if (baseDirectory == null) {
            return null;
        } else {
            return baseDirectory.getAbsolutePath();
        }
    }

    public String getExec() {
        return runtimeExec;
    }

    public String getName() {
        //
        // Typical output from the Sun JVM:
        //
        // java version "1.5.0_01"
        // Java(TM) 2 Runtime Environment, Standard Edition (build 1.5.0_01-b08)
        // Java HotSpot(TM) Client VM (build 1.5.0_01-b08, mixed mode, sharing)
        //
        if (cachedName == null) {
            File exec = findExecutable(baseDirectory, runtimeExec);
            if (exec != null && exec.exists()) {
                String[] cmd = new String[] {
                    exec.getAbsolutePath(), "-version"
                };
                java.lang.Runtime rt = java.lang.Runtime.getRuntime();
                try {
                    Process proc = rt.exec(cmd);
                    cachedName = Processes.waitFor(proc);
                    // Take the first line of output as the runtime name.
                    String lt = System.getProperty("line.separator");
                    int idx = cachedName.indexOf(lt);
                    if (idx > -1) {
                        cachedName = cachedName.substring(0, idx);
                    }
                } catch (IOException ioe) {
                    ErrorManager.getDefault().notify(ioe);
                }
            }
        }
        if (cachedName == null) {
            // Apparently that did not work.
            return getIdentifier();
        }
        return cachedName;
    }

    public String getIdentifier() {
        return runtimeIdentifier;
    }

    public List<String> getSources() {
        return sourceCodes;
    }

    public boolean isValid() {
        if (baseDirectory != null && runtimeExec != null) {
            // Try to find the executable.
            File exec = findExecutable(baseDirectory, runtimeExec);
            return exec != null && exec.exists();
        }
        return false;
    }

    public void setBase(String base) {
        if (base == null || base.length() == 0) {
            throw new IllegalArgumentException("invalid base value");
        }
        // Must leave validation for manager to perform, otherwise we mess
        // up the deserialization process.
        baseDirectory = new File(base);
    }

    public void setExec(String exec) {
        if (exec == null || exec.length() == 0) {
            throw new IllegalArgumentException("invalid exec value");
        }
        runtimeExec = exec;
        // Must leave validation for manager to perform, otherwise we mess
        // up the deserialization process.
    }

    public void setIdentifier(String id) {
        runtimeIdentifier = id;
    }

    public void setSources(List<String> sources) {
        if (sources == null || sources.isEmpty()) {
            sourceCodes = null;
        } else {
            sourceCodes = sources;
        }
    }
}