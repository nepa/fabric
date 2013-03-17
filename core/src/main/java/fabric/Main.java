/**
 * Copyright (c) 2010-2013, Institute of Telematics (Dennis Pfisterer, Marco Wegner, Dennis Boldt,
 * Sascha Seidel, Joss Widderich, et al.), University of Luebeck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *     following disclaimer in the documentation and/or other materials provided with the distribution.
 *   - Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fabric;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.tr.util.Logging;
import de.uniluebeck.sourcegen.Workspace;

import fabric.wsdlschemaparser.wsdl.FWSDL;
import fabric.wsdlschemaparser.schema.FSchema;

import fabric.module.api.FModuleRegistry;
import fabric.module.api.FItemHandlerBase;
import fabric.module.api.FSchemaTreeWalker;
import fabric.module.api.FSchemaTreeItemHandler;
import fabric.module.api.FWSDLProcessor;
import fabric.module.api.FWSDLItemHandler;

import fabric.module.cpp.test.CppModule;
import fabric.module.dot.FabricDotGraphModule;
import fabric.module.typegen.FabricTypeGenModule;
import fabric.module.exi.FabricEXIModule;
import fabric.module.midgen4j.MidGen4JModule;
import fabric.module.midgen4j.rest.MidGen4JRESTModule;
import fabric.module.midgen4j.websockets.MidGen4JWebSocketsModule;

public class Main {

    static {
        Logging.setLoggingDefaults();
    }

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private File wsdlFile = null;
    private File schemaFile = null;
    private Workspace workspace = null;

    private final Properties properties = new Properties();
    private final FModuleRegistry registry = new FModuleRegistry();

    /** WSDL handlers */
    private final List<FWSDLItemHandler> wsdlHandlers = new ArrayList<FWSDLItemHandler>();

    /** XML Schema handlers */
    private final List<FSchemaTreeItemHandler> schemaHandlers = new ArrayList<FSchemaTreeItemHandler>();

    public Main(String[] args) {

        // Create the command line parser
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("x", "xsd", true, "The XML Schema file to load");
        options.addOption("w", "wsdl", true, "The WSDL file to load");
        options.addOption("p", "properties", true, "The properties file to configure the modules");
        options.addOption("m", "modules", true, "Comma-separated list of modules to run");
        options.addOption("v", "verbose", false, "Verbose logging output");
        options.addOption("h", "help", false, "Help output");
        options.addOption("o", "output", true, "The code output directory");

        // Load all modules
        try {
            registerModules();
        } catch (Exception e) {
            LOGGER.error("", e);
            System.exit(1);
        }

        // Parse the command line
        try {
            CommandLine line = parser.parse(options, args);

            // Check if verbose output should be used
            if (line.hasOption('v')) {
                Logger.getRootLogger().setLevel(Level.DEBUG);
            } else {
                Logger.getRootLogger().setLevel(Level.INFO);
            }

            // Output help and exit
            if (line.hasOption('h')) {
                usage(options, registry);
                System.exit(0);
            }

            // Load properties from file
            if (line.hasOption('p')) {
                String propertiesFile = line.getOptionValue('p');
                LOGGER.debug("Loading properties from '" + propertiesFile + "'.");
                properties.load(new FileReader(new File(propertiesFile)));
            }
            
            // Set code output directory
            if (line.hasOption('o')) {
            	String oValue = line.getOptionValue('o');

            	// Ensure that last character is the file.separator
            	if (!oValue.endsWith(System.getProperty("file.separator"))) {
            		oValue += System.getProperty("file.separator");
            	}

            	File fileOutput = new File(oValue);

            	if (!fileOutput.exists()) {
            		LOGGER.error("The desired output directory '" + oValue + "' does not exist.");
            		System.exit(0);
            	} else if (!fileOutput.isDirectory()) {
            		LOGGER.error("The desired output directory '" + oValue + "' is a file.");
            		System.exit(0);
            	} else {
                LOGGER.debug("Setting output directory to '" + oValue + "'.");
                properties.setProperty("fabric.output_directory", oValue); // Add code output directory to properties
              }
            }

            workspace = new Workspace(properties);

            // Create module instances
            if (line.hasOption('m')) {
                for (String moduleName : line.getOptionValue('m').split(",")) {
                    moduleName = moduleName.trim();
                    LOGGER.debug("Creating instance of module {}", moduleName);

                    ArrayList<FItemHandlerBase> handlers = this.registry.get(moduleName).getHandlers(this.workspace);
                    for (FItemHandlerBase handler: handlers) {
                        // Add an XML Schema handler
                        if (handler instanceof FSchemaTreeItemHandler) {
                            FSchemaTreeItemHandler schemaHandler = (FSchemaTreeItemHandler)handler;
                            this.schemaHandlers.add(schemaHandler);
                        }
                        // Add a WSDL handler
                        else if (handler instanceof FWSDLItemHandler) {
                            FWSDLItemHandler wsdlHandler = (FWSDLItemHandler)handler;
                            this.wsdlHandlers.add(wsdlHandler);
                        }
                        // Catch unknown handler
                        else {
                            throw new IllegalStateException(String.format("Module provided unknown handler type '%s'.",
                                    handler.getClass().getSimpleName()));
                        }
                    }
                }
            }

            if (line.hasOption('x') && line.hasOption('w')) {
                throw new Exception("Only one of -x or -w is allowed");
            } else if (line.hasOption('x')) {
                schemaFile = new File(line.getOptionValue('x'));
                properties.setProperty("fabric.xsd", line.getOptionValue('x')); // Add path of XSD file to properties
            } else if (line.hasOption('w')) {
                wsdlFile = new File(line.getOptionValue('w'));
                properties.setProperty("fabric.wsdl", line.getOptionValue('w')); // Add path of WSDL file to properties
            } else {
                throw new Exception("Supply one of -x or -w");
            }

        } catch (Exception e) {
            LOGGER.error("Invalid command line: " + e, e);
            usage(options, registry);
            System.exit(1);
        }

        // Handle the different file types
        try {
            FSchema schema = null;
            FWSDL wsdl = null;

            // Create FSchema object
            if (null != this.schemaFile) {
                schema = new FSchema(this.schemaFile);
            }
            // Create FWSDL object
            else if (null != this.wsdlFile) {
                wsdl = new FWSDL(this.wsdlFile);

                // Extract inline XML Schema (if any)
                if (null != wsdl.getSchema()) {
                    schema = wsdl.getSchema();
                }
            }

            // Walk XML Schema tree (if any)
            if (null != schema) {
                LOGGER.debug("Walking XML Schema tree.");
                System.out.println(schema.toString());

                FSchemaTreeWalker treeWalker = new FSchemaTreeWalker();
                for (FSchemaTreeItemHandler schemaHandler: this.schemaHandlers) {
                    treeWalker.walk(schema, schemaHandler);
                }
            }

            // Process WSDL elements (if any)
            if (null != wsdl) {
                LOGGER.debug("Handling WSDL elements.");
                System.out.println(wsdl.toString());

                FWSDLProcessor processor = new FWSDLProcessor();
                for (FWSDLItemHandler wsdlHandler: this.wsdlHandlers) {
                    processor.process(wsdl, wsdlHandler);
                }
            }

            // Generate code from workspace
            this.workspace.generate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * All possible modules are added here
     *
     * @throws Exception
     */
    private void registerModules() throws Exception {
        this.registry.register(new FabricDotGraphModule(this.properties));
        this.registry.register(new CppModule(this.properties));
        this.registry.register(new FabricEXIModule(this.properties));
        this.registry.register(new FabricTypeGenModule(this.properties));
        this.registry.register(new MidGen4JModule(this.properties));
        this.registry.register(new MidGen4JRESTModule(this.properties));
        this.registry.register(new MidGen4JWebSocketsModule(this.properties));
    }

    /**
     * Prints the usage of the program.
     *
     * @param options
     * @param registry
     */
    private void usage(Options options, FModuleRegistry registry) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(120, Main.class.getCanonicalName(), null, options, null);
        System.out.println(registry.toString());
    }

    public static void main(String[] args) {
        new Main(args);
    }
}
