/** 03.09.2012 16:30 */
package fabric.wsdlschemaparser.wsdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingFault;
import javax.wsdl.Service;
import javax.wsdl.Port;
import javax.wsdl.Fault;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;

import org.w3c.dom.Element;
import com.ibm.wsdl.xml.WSDLReaderImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.XmlException;

import fabric.wsdlschemaparser.schema.FSchema;

/**
 * WSDL parser to import webservice descriptions and transform
 * them to an internal Fabric representation. That is a set of
 * collections, each containing distinct parts of the WSDL
 * document (e.g. types, messages, port types, bindings and
 * services).
 *
 * Furthermore, this class will extract all inline XML Schema
 * code, so that it can be processed with existing Fabric
 * modules later.
 *
 * @author seidel
 */
public class FWSDL
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(FWSDL.class);

  /** XML Schema document from WSDL file (if any) */
  private FSchema schema;

  /** Message definitions from WSDL file */
  private Set<FMessage> messages;

  /** Port type definitions from WSDL file */
  private Set<FPortType> portTypes;

  /** Binding definitions from WSDL file */
  private Set<FBinding> bindings;

  /** Service definitions from WSDL file */
  private Set<FService> services;

  /**
   * Parameterized constructor to import a WSDL document
   * from file. After calling the constructor, all parts
   * of the webservice description can be accessed through
   * the public getter methods.
   *
   * @param wsdlFile WSDL document to import
   *
   * @throws WSDLException Error reading WSDL definition
   * from file
   * @throws XmlException Error getting Schema document
   * while extracting WSDL types
   * @throws Exception Error adding Schema document to
   * FSchema object
   */
  public FWSDL(final File wsdlFile) throws WSDLException, XmlException, Exception
  {
    this.init();

    // Read WSDL definition from file
    LOGGER.info(String.format("Reading WSDL document from file '%s'.", wsdlFile.getPath()));
    Definition wsdlDefinition = new WSDLReaderImpl().readWSDL(wsdlFile.getAbsolutePath());

    // Extract parts from WSDL definition
    this.extractTypes(wsdlDefinition, wsdlFile.toURI());
    this.extractMessages(wsdlDefinition);
    this.extractPortTypes(wsdlDefinition);
    this.extractBindings(wsdlDefinition);
    this.extractServices(wsdlDefinition);
  }

  /**
   * Initialize member variables.
   */
  private void init()
  {
    this.schema = null;

    this.messages = new HashSet<FMessage>();
    this.portTypes = new HashSet<FPortType>();
    this.bindings = new HashSet<FBinding>();
    this.services = new HashSet<FService>();
  }

  /**
   * Extract inline XML Schema code from WSDL file (if any).
   * Otherwise the internal member variable will be 'null'.
   *
   * The code of this method is based on an earlier
   * implementation by Marco Wegner.
   *
   * @param wsdlDefinition WSDL definition read from file
   * @param schemaLocation URI of XML Schema document
   * 
   * @throws XmlException Error getting Schema document
   * while extracting WSDL types
   * @throws Exception Error adding Schema document to
   * FSchema object
   */
  private void extractTypes(final Definition wsdlDefinition, final URI schemaLocation) throws XmlException, Exception
  {
    if (null != wsdlDefinition.getTypes())
    {
      // Get namespaces from WSDL definition
      Map<?, ?> wsdlNamespaces = wsdlDefinition.getNamespaces();

      for (Object element: wsdlDefinition.getTypes().getExtensibilityElements())
      {
        if (element instanceof Schema)
        {
          Schema inlineSchema = (Schema)element;
          LOGGER.info("Found inline XML Schema in WSDL document.");

          // Get root of XML Schema document
          Element rootElement = inlineSchema.getElement();

          // Iterate over all namespace declarations and copy
          // them to the tree of the XML Schema document
          for (Object namespaceKey: wsdlNamespaces.keySet())
          {
            String shortNamespace = (String)namespaceKey;
            String longNamespace = (String)wsdlNamespaces.get(namespaceKey);

            if (rootElement.getAttribute("xmlns:" + shortNamespace).length() == 0)
            {
              rootElement.setAttribute("xmlns:" + shortNamespace, longNamespace);
            }
          }

          // Fallback on global definitions
          if (rootElement.getAttribute("targetNamespace").length() == 0)
          {
            rootElement.setAttribute("targetNamespace", wsdlDefinition.getTargetNamespace());
          }

          // Build new Schema document from root element and its children
          org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument.Schema schemaDocument = SchemaDocument.Factory.parse(rootElement).getSchema();

          LOGGER.debug(String.format("Adding inline schema to FSchema object:\n %s", schemaDocument.toString()));

          // Create Fabric schema object from XML Schema
          this.schema = new FSchema();
          this.schema.addSchema(schemaDocument, schemaLocation);
        }
      }
    }
  }

  /**
   * Extract webservice messages from WSDL file.
   *
   * @param wsdlDefinition WSDL definition read from file
   */
  private void extractMessages(final Definition wsdlDefinition)
  {
    Map<?, ?> wsdlMessages = wsdlDefinition.getMessages();

    if (null != wsdlMessages)
    {
      LOGGER.debug(String.format("Found %d message%s.", wsdlMessages.size(),
              (wsdlMessages.size() != 1 ? "s" : "")));

      // Iterate over all messages
      Iterator messageIterator = wsdlMessages.entrySet().iterator();
      while (messageIterator.hasNext())
      {
        Entry messageObject = (Entry)messageIterator.next();
        // Object messageKey = messageObject.getKey();
        Object messageValue = messageObject.getValue();

        Message message = (Message)messageValue;
        FMessageImpl webserviceMessage = FMessage.factory.create(message.getQName().getLocalPart());
        this.messages.add(webserviceMessage);

        LOGGER.debug(String.format("  └ Processing message '%s' with %d part%s...",
                message.getQName().getLocalPart(), message.getParts().size(),
                (message.getParts().size() != 1 ? "s" : "")));

        // Iterate over all message parts
        Iterator partIterator = message.getParts().entrySet().iterator();
        while (partIterator.hasNext())
        {
          Entry partObject = (Entry)partIterator.next();
          // Object partKey = partObject.getKey();
          Object partValue = partObject.getValue();

          Part part = (Part)partValue;
          FMessagePartImpl messagePart = FMessagePart.factory.create(part.getName(), part.getElementName(), part.getTypeName());
          webserviceMessage.addPart(messagePart);
        }
      }
    }
  }

  /**
   * Extract port types from WSDL file. Each port type may define
   * the interface (i.e. signature) of multiple service operations.
   *
   * @param wsdlDefinition WSDL definition read from file.
   */
  private void extractPortTypes(final Definition wsdlDefinition)
  {
    Map<?, ?> wsdlPortTypes = wsdlDefinition.getPortTypes();

    if (null != wsdlPortTypes)
    {
      LOGGER.debug(String.format("Found %d port type%s.", wsdlPortTypes.size(),
              (wsdlPortTypes.size() != 1 ? "s" : "")));

      // Iterate over all port types
      Iterator portTypeIterator = wsdlPortTypes.entrySet().iterator();
      while (portTypeIterator.hasNext())
      {
        Entry object = (Entry)portTypeIterator.next();
        // Object key = object.getKey();
        Object value = object.getValue();

        PortType portType = (PortType)value;
        FPortTypeImpl serviceInterface = FPortType.factory.create(portType.getQName().getLocalPart());
        this.portTypes.add(serviceInterface);

        LOGGER.debug(String.format("  └ Processing port type '%s' with %d operation%s...",
                portType.getQName().getLocalPart(), portType.getOperations().size(),
                (portType.getOperations().size() != 1 ? "s" : "")));

        // Iterate over all operations
        for (Object o: portType.getOperations())
        {
          if (o instanceof Operation)
          {
            Operation operation = (Operation)o;

            // Operation must either have input or output message (or both)
            if ((null != operation.getInput() && null != operation.getInput().getMessage()) ||
                (null != operation.getOutput() && null != operation.getOutput().getMessage()))
            {
              // Determine operation type
              FOperationType methodType = FOperationType.INVALID;
              OperationType style = operation.getStyle();
              if (OperationType.ONE_WAY == style)
              {
                methodType = FOperationType.ONE_WAY;
              }
              else if (OperationType.REQUEST_RESPONSE == style)
              {
                methodType = FOperationType.REQUEST_RESPONSE;
              }
              else if (OperationType.SOLICIT_RESPONSE == style)
              {
                methodType = FOperationType.SOLICIT_RESPONSE;
              }
              else if (OperationType.NOTIFICATION == style)
              {
                methodType = FOperationType.NOTIFICATION;
              }

              // Collect fault messages (if any)
              HashSet<FOperationFaultMessage> faultMessages = new HashSet<FOperationFaultMessage>();
              Map<?, ?> faults = operation.getFaults();
              for (Object faultKey: faults.keySet())
              {
                String faultName = (String)faultKey;
                Fault fault = (Fault)faults.get(faultKey);
                QName faultMessage = fault.getMessage().getQName();

                faultMessages.add(new FOperationFaultMessage(faultName, faultMessage));
              }

              // Create operation input message
              FOperationInputMessage inputMessage = null;
              if (null != operation.getInput() && null != operation.getInput().getMessage())
              {
                inputMessage = new FOperationInputMessage(operation.getInput().getName(), operation.getInput().getMessage().getQName());
              }

              // Create operation output message
              FOperationOutputMessage outputMessage = null;
              if (null != operation.getOutput() && null != operation.getOutput().getMessage())
              {
                outputMessage = new FOperationOutputMessage(operation.getOutput().getName(), operation.getOutput().getMessage().getQName());
              }

              FOperationImpl method = FOperation.factory.create(operation.getName(), methodType, inputMessage, outputMessage, faultMessages);

              // Add operation to webservice interface
              serviceInterface.addOperation(method);
            }
          }
        }
      }
    }
  }

  /**
   * Extract bindings from WSDL file.
   *
   * @param wsdlDefinition WSDL definition read from file
   */
  private void extractBindings(final Definition wsdlDefinition)
  {
    // javax.wsdl.Definition has two getters for bindings:
    //
    //   getBindings(): Get all bindings from the current
    //                  WSDL definition object.
    //
    //   getAllBindings(): Get all bindings from the current
    //                     WSDL definition object as well as
    //                     any imported definitions.
    Map<?, ?> wsdlBindings = wsdlDefinition.getAllBindings();

    if (null != wsdlBindings)
    {
      LOGGER.debug(String.format("Found %d binding%s.", wsdlBindings.size(),
              (wsdlBindings.size() != 1 ? "s" : "")));

      // Iterate over all bindings
      Iterator bindingIterator = wsdlBindings.entrySet().iterator();
      while (bindingIterator.hasNext())
      {
        Entry object = (Entry)bindingIterator.next();
        // Object key = object.getKey();
        Object value = object.getValue();

        Binding binding = (Binding)value;

        // Binding type must be defined
        if (binding.isUndefined())
        {
          LOGGER.debug(String.format("  └ Skipping binding '%s' with undefined type...", binding.getQName().getLocalPart()));
        }
        else
        {
          LOGGER.debug(String.format("  └ Processing binding '%s' with %d operation%s...",
                  binding.getQName().getLocalPart(), binding.getBindingOperations().size(),
                  (binding.getBindingOperations().size() != 1 ? "s" : "")));

          // Create new binding object
          FBindingImpl portTypeBinding = FBinding.factory.create(binding.getQName().getLocalPart(), binding.getPortType().getQName());

          // Add per-binding information
          for (Object ee: binding.getExtensibilityElements())
          {
            if (ee instanceof ExtensibilityElement)
            {
              ExtensibilityElement element = (ExtensibilityElement)ee;
              FExtensibilityElement extensibilityElement = new FExtensibilityElement(element);
              portTypeBinding.addPerBindingInformation(extensibilityElement);

              LOGGER.debug(String.format("      └ Adding per-binding information of type '%s' to binding '%s'.",
                      extensibilityElement.getImplementationName(), portTypeBinding.getBindingName()));
            }
          }
          this.bindings.add(portTypeBinding);

          // Extract methods for current binding
          for (Object o: binding.getBindingOperations())
          {
            BindingOperation operation = (BindingOperation)o;

            // Determine operation type
            FOperationType methodType = FOperationType.INVALID;
            OperationType style = operation.getOperation().getStyle();
            if (OperationType.ONE_WAY == style)
            {
              methodType = FOperationType.ONE_WAY;
            }
            else if (OperationType.REQUEST_RESPONSE == style)
            {
              methodType = FOperationType.REQUEST_RESPONSE;
            }
            else if (OperationType.SOLICIT_RESPONSE == style)
            {
              methodType = FOperationType.SOLICIT_RESPONSE;
            }
            else if (OperationType.NOTIFICATION == style)
            {
              methodType = FOperationType.NOTIFICATION;
            }

            LOGGER.debug(String.format("      └ Processing binding operation '%s()' of type '%s'...",
                    operation.getName(), methodType));

            // Create input message for binding operation
            FBindingOperationInputMessage inputMessage =
                    new FBindingOperationInputMessage(operation.getBindingInput().getName());

            // Add per-message information for input message
            for (Object ee: operation.getBindingInput().getExtensibilityElements())
            {
              if (ee instanceof ExtensibilityElement)
              {
                ExtensibilityElement element = (ExtensibilityElement)ee;
                FExtensibilityElement extensibilityElement = new FExtensibilityElement(element);

                inputMessage.addPerMessageInformation(extensibilityElement);

                LOGGER.debug(String.format("          └ Adding per-message information of type '%s' to input message.",
                        extensibilityElement.getImplementationName()));
              }
            }

            // Create output message for binding operation
            FBindingOperationOutputMessage outputMessage =
                    new FBindingOperationOutputMessage(operation.getBindingOutput().getName());

            // Add per-message information for output message
            for (Object ee: operation.getBindingOutput().getExtensibilityElements())
            {
              if (ee instanceof ExtensibilityElement)
              {
                ExtensibilityElement element = (ExtensibilityElement)ee;
                FExtensibilityElement extensibilityElement = new FExtensibilityElement(element);

                outputMessage.addPerMessageInformation(extensibilityElement);

                LOGGER.debug(String.format("          └ Adding per-message information of type '%s' to output message.",
                        extensibilityElement.getImplementationName()));
              }
            }

            // Create fault messages for binding operation (if any)
            HashSet<FBindingOperationFaultMessage> faultMessages = new HashSet<FBindingOperationFaultMessage>();
            Map<?, ?> faults = operation.getBindingFaults();
            for (Object faultKey: faults.keySet())
            {
              String faultName = (String)faultKey;
              BindingFault fault = (BindingFault)faults.get(faultKey);

              FBindingOperationFaultMessage faultMessage = new FBindingOperationFaultMessage(faultName);

              // Add per-message information for fault message
              for (Object ee: fault.getExtensibilityElements())
              {
                if (ee instanceof ExtensibilityElement)
                {
                  ExtensibilityElement element = (ExtensibilityElement)ee;
                  FExtensibilityElement extensibilityElement = new FExtensibilityElement(element);

                  faultMessage.addPerMessageInformation(extensibilityElement);

                  LOGGER.debug(String.format("          └ Adding per-message information of type '%s' to fault message.",
                          extensibilityElement.getImplementationName()));
                }
              }

              faultMessages.add(faultMessage);
            }

            // Create new binding operation object
            FBindingOperationImpl method = FBindingOperation.factory.create(operation.getName(),
                    methodType, inputMessage, outputMessage, faultMessages);

            // Add per-operation information
            for (Object ee: operation.getExtensibilityElements())
            {
              if (ee instanceof ExtensibilityElement)
              {
                ExtensibilityElement element = (ExtensibilityElement)ee;
                FExtensibilityElement extensibilityElement = new FExtensibilityElement(element);
                method.addPerOperationInformation(extensibilityElement);

                LOGGER.debug(String.format("          └ Adding per-operation information of type '%s' to operation '%s()'.",
                        extensibilityElement.getImplementationName(), method.getBindingOperationName()));
              }
            }

            // Add operation to port type binding
            portTypeBinding.addBindingOperation(method);
          }
        }
      }
    }
  }

  /**
   * Extract services with their ports (endpoints) from WSDL file.
   *
   * @param wsdlDefinition WSDL definition load from file
   */
  private void extractServices(final Definition wsdlDefinition)
  {
    Map<?, ?> wsdlServices = wsdlDefinition.getServices();

    if (null != wsdlServices)
    {
      LOGGER.debug(String.format("Found %d service%s.", wsdlServices.size(),
              (wsdlServices.size() != 1 ? "s" : "")));

      // Iterate over all services
      Iterator serviceIterator = wsdlServices.entrySet().iterator();
      while (serviceIterator.hasNext())
      {
        Entry serviceObject = (Entry)serviceIterator.next();
        Object serviceKey = serviceObject.getKey();
        Object serviceValue = serviceObject.getValue();

        // Create new webservice object
        Service service = (Service)serviceValue;
        FServiceImpl webservice = FService.factory.create(service.getQName().getLocalPart());
        this.services.add(webservice);

        // Iterate over all endpoints
        Map<?, ?> ports = service.getPorts();
        for (Object portKey: ports.keySet())
        {
          String portName = (String)portKey;
          Port port = (Port)ports.get(portKey);

          Binding binding = port.getBinding();

          // WSDL specification says, we can only have one address here,
          // so we will just use the first extensibility element:
          //   http://www.w3.org/TR/wsdl#_ports
          List<ExtensibilityElement> elements = port.getExtensibilityElements();
          FExtensibilityElement extensibilityElement = new FExtensibilityElement(elements.get(0));

          // Create new endpoint object and add it to service
          FPortImpl endpoint = FPort.factory.create(portName, binding.getQName(), extensibilityElement);
          webservice.addPort(endpoint);
        }
      }
    }
  }

  /**
   * Get inline XML Schema document (if any).
   *
   * @return Inline XML Schema document
   */
  public FSchema getSchema()
  {
    return this.schema;
  }

  /**
   * Get webservice messages.
   *
   * @return Webservice messages
   */
  public HashSet<FMessage> getMessages()
  {
    return (HashSet<FMessage>)this.messages;
  }

  /**
   * Get webservice port types (interfaces).
   *
   * @return Webservice port types
   */
  public HashSet<FPortType> getPortTypes()
  {
    return (HashSet<FPortType>)this.portTypes;
  }

  /**
   * Get port type bindings.
   *
   * @return Port type bindings
   */
  public HashSet<FBinding> getBindings()
  {
    return (HashSet<FBinding>)this.bindings;
  }

  /**
   * Get service definitions.
   *
   * @return Service endpoints
   */
  public HashSet<FService> getServices()
  {
    return (HashSet<FService>)this.services;
  }

  /**
   * Create a human-readable form of all methods that
   * are defined in the WSDL file and print a text
   * version of the inline XML Schema document (if any).
   *
   * @return String representation of FWSDL object
   */
  @Override
  public String toString()
  {
    String result = "";

    // Print inline XML Schema (if any)
    if (null != this.schema && this.schema.toString().length() > 0)
    {
      result += "\nInline XML Schema:\n" + this.schema.toString();
    }

    // Print all webservice messages
    for (FMessage webserviceMessage: this.messages)
    {
      result += "\n" + webserviceMessage.toString() + "\n";
    }

    // Print all webservice methods
    for (FPortType serviceInterface: this.portTypes)
    {
      result += "\n" + serviceInterface.toString() + "\n";
    }

    // Print all port type bindings
    for (FBinding portTypeBinding: this.bindings)
    {
      result += "\n" + portTypeBinding.toString() + "\n";
    }

    // Print all services and their endpoints
    for (FService webservice: this.services)
    {
      result += "\n" + webservice.toString() + "\n";
    }

    return result;
  }
}
