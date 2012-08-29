/**
 * Copyright (c) 2010-2012, Institute of Telematics (Dennis Pfisterer, Marco Wegner, Dennis Boldt,
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
package fabric.module.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fabric.wsdlschemaparser.schema.FComplexType;
import fabric.wsdlschemaparser.schema.FElement;
import fabric.wsdlschemaparser.schema.FSchema;
import fabric.wsdlschemaparser.schema.FSchemaObject;
import fabric.wsdlschemaparser.schema.FSchemaType;
import fabric.wsdlschemaparser.schema.FSimpleType;
import fabric.wsdlschemaparser.schema.FTopLevelObjectList;

/**
 * This class represents a walker that can process elements
 * of a Schema object tree.
 * 
 * @author Marco Wegner
 */
public final class FSchemaTreeWalker {

    /**
     * The logging instance.
     */
    private static final Logger log = LoggerFactory.getLogger(FSchemaTreeWalker.class);

    /**
     * The handler for specific Schema object tree items.
     */
    private FSchemaTreeItemHandler itemHandler;

    /**
     * Construct a new Schema object tree walker.
     */
    public FSchemaTreeWalker( ) {
        super( );
    }

    /**
     * Sets the handler for specific Schema object tree items.
     * 
     * @param itemHandler The new Schema object item tree handler.
     */
    private void setItemHandler(FSchemaTreeItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    /**
     * Returns the handler for specific Schema object tree items.
     * 
     * @return The Schema object item tree handler.
     */
    private FSchemaTreeItemHandler getItemHandler( ) {
        return this.itemHandler;
    }

    /**
     * Walk a Schema object tree. Fabric's default handler for Schema object
     * tree items is used.
     * 
     * @param schema The root of the Schema tree to be walked.
     * @throws Exception If an error occurs while walking the tree.
     */
    public void walk(FSchema schema) throws Exception {
        walk(schema, null);
    }

    /**
     * Walk a Schema object tree.
     * 
     * @param schema The root of the Schema tree to be walked.
     * @param itemHandler The tree item handler to use when walking the tree. If
     *        <code>null</code>, Fabric's default handler for Schema object tree
     *        items is used.
     * @throws Exception If an error occurs while walking the tree.
     */
    public void walk(FSchema schema, FSchemaTreeItemHandler itemHandler) throws Exception {

        final FSchemaTreeItemHandler handler;
        if (itemHandler == null) {
            handler = new FDefaultSchemaHandler( );
        } else {
            handler = itemHandler;
        }
        setItemHandler(handler);

        handler.startSchema(schema);
        log.debug("Start handling Schema object tree");

        final FTopLevelObjectList tlo = schema.getTopLevelObjectList( );

        if (null == tlo) {
            log.error("Top level object list is null.");
        }
        else {
            for (final FElement e : tlo.getTopLevelElements( )) {
                handleElement(e, null);
            }
        }

        handler.endSchema(schema);
        log.debug("Done handling Schema object tree");
    }

    /**
     * Handles an element in the Schema object tree.
     * 
     * @param e The element to be handled.
     * @param parent The element's parent. If <code>null</code> this usually
     *        signifies a top-level element.
     * @throws Exception If an error occurs while walking the tree.
     */
    private void handleElement(FElement e, FComplexType parent) throws Exception {
        final FSchemaTreeItemHandler handler = getItemHandler( );
        final String elemName = e.getName( );

        if (e.isReference( )) {
            handler.startElementReference(e);
            log.debug("Start handling element reference '{}'", elemName);
            // TODO handle element reference
            handler.endElementReference(e);
            log.debug("Done handling element reference '{}'", elemName);
        } else if (e.isTopLevel( )) {
            handler.startTopLevelElement(e);
            log.debug("Start handling top-level element '{}'", elemName);
            handleSchemaType(e.getSchemaType( ), e);
            handler.endTopLevelElement(e);
            log.debug("Done handling top-level element '{}'", elemName);
        } else {
            handler.startLocalElement(e, parent);
            log.debug("Start handling local element '{}'", elemName);
            handleSchemaType(e.getSchemaType( ), e);
            handler.endLocalElement(e, parent);
            log.debug("Done handling local element '{}'", elemName);
        }
    }

    /**
     * Generic handling method for Schema types.
     * 
     * @param type The type to be handled.
     * @param parent The Schema type's parent element.
     * @throws Exception If an error occurs while walking the tree.
     */
    private void handleSchemaType(FSchemaType type, FElement parent) throws Exception {
        if (type instanceof FSimpleType) {
            handleSimpleType((FSimpleType)type, parent);
        } else if (type instanceof FComplexType) {
            handleComplexType((FComplexType)type, parent);
        } else {
            throw new Exception("Unknown SchemaType: " + type.toString( ));
        }
    }

    /**
     * Handles a simple type.
     * 
     * @param type The simple type to be handled.
     * @param parent The simple type's parent element.
     * @throws Exception If an error occurs while walking the tree.
     */
    private void handleSimpleType(FSimpleType type, FElement parent) throws Exception {
        final FSchemaTreeItemHandler handler = getItemHandler( );
        final String typeName = type.getName( );

        if (type.isTopLevel( )) {
            handler.startTopLevelSimpleType(type, parent);
            log.debug("Start handling top-level simple type '{}'", typeName);
            handleSimpleTypeContent( );
            handler.endTopLevelSimpleType(type, parent);
            log.debug("Done handling top-level simple type '{}'", typeName);
        } else {
            handler.startLocalSimpleType(type, parent);
            log.debug("Start handling local simple type '{}'", typeName);
            handleSimpleTypeContent( );
            handler.endLocalSimpleType(type, parent);
            log.debug("Done handling local simple type '{}'", typeName);
        }
    }

    /**
     * 
     */
    private void handleSimpleTypeContent( ) {
        //
    }

    /**
     * Handles a complex type.
     * 
     * @param type The complex type to be handled.
     * @param parent The complex type's parent element.
     * @throws Exception If an error occurs while walking the tree.
     */
    private void handleComplexType(FComplexType type, FElement parent) throws Exception {
        final FSchemaTreeItemHandler handler = getItemHandler( );
        final String typeName = type.getName( );

        if (type.isTopLevel( )) {
            handler.startTopLevelComplexType(type, parent);
            log.debug("Start handling top-level complex type '{}'", typeName);
            handleComplexContent(type);
            handler.endTopLevelComplexType(type, parent);
            log.debug("Done handling top-level complex type '{}'", typeName);
        } else {
            handler.startLocalComplexType(type, parent);
            log.debug("Start handling local complex type '{}'", typeName);
            handleComplexContent(type);
            handler.endLocalComplexType(type, parent);
            log.debug("Done handling local complex type '{}'", typeName);
        }
    }

    /**
     * Handles the content of a complex type. The content usually comprises more
     * types, elements and/or attributes.
     * 
     * @param parent The complex type whose children are to be handled.
     * @throws Exception If an error occurs while walking the tree.
     */
    private void handleComplexContent(FComplexType parent) throws Exception {
        for (final FSchemaObject o : parent.getChildObjects( )) {
            if (o instanceof FElement) {
                handleElement((FElement)o, parent);
            } else if (o instanceof FComplexType) {
                // TODO fix parent
                handleComplexType((FComplexType)o, null);
            } else {
                throw new Exception("Unknown child object: " + o.toString( ));
            }
        }
    }
}
