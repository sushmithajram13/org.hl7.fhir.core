package org.hl7.fhir.r5.elementmodel;

/*-
 * #%L
 * org.hl7.fhir.r5
 * %%
 * Copyright (C) 2014 - 2019 Health Level 7
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.hl7.fhir.exceptions.DefinitionException;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.exceptions.FHIRFormatError;
import org.hl7.fhir.r5.conformance.ProfileUtilities;
import org.hl7.fhir.r5.context.IWorkerContext;
import org.hl7.fhir.r5.elementmodel.Element.SpecialElement;
import org.hl7.fhir.r5.formats.FormatUtilities;
import org.hl7.fhir.r5.formats.IParser.OutputStyle;
import org.hl7.fhir.r5.model.DateTimeType;
import org.hl7.fhir.r5.model.ElementDefinition.PropertyRepresentation;
import org.hl7.fhir.r5.model.Enumeration;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.hl7.fhir.r5.utils.ToolingExtensions;
import org.hl7.fhir.r5.utils.formats.XmlLocationAnnotator;
import org.hl7.fhir.r5.utils.formats.XmlLocationData;
import org.hl7.fhir.utilities.ElementDecoration;
import org.hl7.fhir.utilities.i18n.I18nConstants;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.validation.ValidationMessage.IssueSeverity;
import org.hl7.fhir.utilities.validation.ValidationMessage.IssueType;
import org.hl7.fhir.utilities.xhtml.CDANarrativeFormat;
import org.hl7.fhir.utilities.xhtml.XhtmlComposer;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.hl7.fhir.utilities.xhtml.XhtmlParser;
import org.hl7.fhir.utilities.xml.IXMLWriter;
import org.hl7.fhir.utilities.xml.XMLUtil;
import org.hl7.fhir.utilities.xml.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XmlParser extends ParserBase {
  private boolean allowXsiLocation;
  private String version;

  public XmlParser(IWorkerContext context) {
    super(context);
  }

  
  public boolean isAllowXsiLocation() {
    return allowXsiLocation;
  }

  public void setAllowXsiLocation(boolean allowXsiLocation) {
    this.allowXsiLocation = allowXsiLocation;
  }

  public Element parse(InputStream stream) throws FHIRFormatError, DefinitionException, FHIRException, IOException {
		Document doc = null;
  	try {
  		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  		// xxe protection
  		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
  		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
  		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
  		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
  		factory.setXIncludeAware(false);
  		factory.setExpandEntityReferences(false);
  			
  		factory.setNamespaceAware(true);
  		if (policy == ValidationPolicy.EVERYTHING) {
  		  // The SAX interface appears to not work when reporting the correct version/encoding.
  		  // if we can, we'll inspect the header/encoding ourselves 
  		  if (stream.markSupported()) {
  		    stream.mark(1024);
  		    version = checkHeader(stream);
  		    stream.reset();
  		  }
  			// use a slower parser that keeps location data
  			TransformerFactory transformerFactory = TransformerFactory.newInstance();
  			Transformer nullTransformer = transformerFactory.newTransformer();
  			DocumentBuilder docBuilder = factory.newDocumentBuilder();
  			doc = docBuilder.newDocument();
  			DOMResult domResult = new DOMResult(doc);
  			SAXParserFactory spf = SAXParserFactory.newInstance();
  			spf.setNamespaceAware(true);
  			spf.setValidating(false);
    		// xxe protection
  		  spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
  			SAXParser saxParser = spf.newSAXParser();
  			XMLReader xmlReader = saxParser.getXMLReader();
    		// xxe protection
  		  xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
  		  xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
  	   			
  			XmlLocationAnnotator locationAnnotator = new XmlLocationAnnotator(xmlReader, doc);
  			InputSource inputSource = new InputSource(stream);
  			SAXSource saxSource = new SAXSource(locationAnnotator, inputSource);
  			nullTransformer.transform(saxSource, domResult);
  		} else {
  			DocumentBuilder builder = factory.newDocumentBuilder();
  			doc = builder.parse(stream);
  		}
  	} catch (Exception e) {
      logError(0, 0, "(syntax)", IssueType.INVALID, e.getMessage(), IssueSeverity.FATAL);
      doc = null;
  	}
  	if (doc == null)
  		return null;
  	else
      return parse(doc);
  }


  private void checkForProcessingInstruction(Document document) throws FHIRFormatError {
    if (policy == ValidationPolicy.EVERYTHING && FormatUtilities.FHIR_NS.equals(document.getDocumentElement().getNamespaceURI())) {
      Node node = document.getFirstChild();
      while (node != null) {
        if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)
          logError(line(document), col(document), "(document)", IssueType.INVALID, context.formatMessage(
            I18nConstants.NO_PROCESSING_INSTRUCTIONS_ALLOWED_IN_RESOURCES), IssueSeverity.ERROR);
        node = node.getNextSibling();
      }
    }
  }

  
  private int line(Node node) {
		XmlLocationData loc = (XmlLocationData) node.getUserData(XmlLocationData.LOCATION_DATA_KEY);
		return loc == null ? 0 : loc.getStartLine();
  }

  private int col(Node node) {
		XmlLocationData loc = (XmlLocationData) node.getUserData(XmlLocationData.LOCATION_DATA_KEY);
		return loc == null ? 0 : loc.getStartColumn();
  }

  public Element parse(Document doc) throws FHIRFormatError, DefinitionException, FHIRException, IOException {
    checkForProcessingInstruction(doc);
    org.w3c.dom.Element element = doc.getDocumentElement();
    return parse(element);
  }
  
  public Element parse(org.w3c.dom.Element element) throws FHIRFormatError, DefinitionException, FHIRException, IOException {
    String ns = element.getNamespaceURI();
    String name = element.getLocalName();
    String path = "/"+pathPrefix(ns)+name;
    
    StructureDefinition sd = getDefinition(line(element), col(element), ns, name);
    if (sd == null)
      return null;

    Element result = new Element(element.getLocalName(), new Property(context, sd.getSnapshot().getElement().get(0), sd));
    checkElement(element, path, result.getProperty());
    result.markLocation(line(element), col(element));
    result.setType(element.getLocalName());
    parseChildren(path, element, result);
    result.numberChildren();
    return result;
  }

  private String pathPrefix(String ns) {
    if (Utilities.noString(ns))
      return "";
    if (ns.equals(FormatUtilities.FHIR_NS))
      return "f:";
    if (ns.equals(FormatUtilities.XHTML_NS))
      return "h:";
    if (ns.equals("urn:hl7-org:v3"))
      return "v3:";
    return "?:";
  }

  private boolean empty(org.w3c.dom.Element element) {
    for (int i = 0; i < element.getAttributes().getLength(); i++) {
      String n = element.getAttributes().item(i).getNodeName();
      if (!n.equals("xmlns") && !n.startsWith("xmlns:"))
        return false;
    }
    if (!Utilities.noString(element.getTextContent().trim()))
      return false;
    
    Node n = element.getFirstChild();
    while (n != null) {
      if (n.getNodeType() == Node.ELEMENT_NODE)
        return false;
      n = n.getNextSibling();
    }
    return true;
  }
  
  private void checkElement(org.w3c.dom.Element element, String path, Property prop) throws FHIRFormatError {
    if (policy == ValidationPolicy.EVERYTHING) {
      if (empty(element) && FormatUtilities.FHIR_NS.equals(element.getNamespaceURI())) // this rule only applies to FHIR Content
        logError(line(element), col(element), path, IssueType.INVALID, context.formatMessage(I18nConstants.ELEMENT_MUST_HAVE_SOME_CONTENT), IssueSeverity.ERROR);
      String ns = FormatUtilities.FHIR_NS;
      if (ToolingExtensions.hasExtension(prop.getDefinition(), "http://hl7.org/fhir/StructureDefinition/elementdefinition-namespace"))
      	ns = ToolingExtensions.readStringExtension(prop.getDefinition(), "http://hl7.org/fhir/StructureDefinition/elementdefinition-namespace");
      else if (ToolingExtensions.hasExtension(prop.getStructure(), "http://hl7.org/fhir/StructureDefinition/elementdefinition-namespace"))
      	ns = ToolingExtensions.readStringExtension(prop.getStructure(), "http://hl7.org/fhir/StructureDefinition/elementdefinition-namespace");
      if (!element.getNamespaceURI().equals(ns))
        logError(line(element), col(element), path, IssueType.INVALID, context.formatMessage(I18nConstants.WRONG_NAMESPACE__EXPECTED_, ns), IssueSeverity.ERROR);
    }
  }

  public Element parse(org.w3c.dom.Element base, String type) throws Exception {
    StructureDefinition sd = getDefinition(0, 0, FormatUtilities.FHIR_NS, type);
    Element result = new Element(base.getLocalName(), new Property(context, sd.getSnapshot().getElement().get(0), sd));
    String path = "/"+pathPrefix(base.getNamespaceURI())+base.getLocalName();
    checkElement(base, path, result.getProperty());
    result.setType(base.getLocalName());
    parseChildren(path, base, result);
    result.numberChildren();
    return result;
  }

  private void parseChildren(String path, org.w3c.dom.Element node, Element element) throws FHIRFormatError, FHIRException, IOException, DefinitionException {
  	// this parsing routine retains the original order in a the XML file, to support validation
  	reapComments(node, element);
    List<Property> properties = element.getProperty().getChildProperties(element.getName(), XMLUtil.getXsiType(node));

  	String text = XMLUtil.getDirectText(node).trim();
    if (!Utilities.noString(text)) {
    	Property property = getTextProp(properties);
    	if (property != null) {
        if ("ED.data[x]".equals(property.getDefinition().getId()) || (property.getDefinition()!=null && property.getDefinition().getBase()!=null && "ED.data[x]".equals(property.getDefinition().getBase().getPath()))) {
          if ("B64".equals(node.getAttribute("representation"))) {
            element.getChildren().add(new Element("dataBase64Binary", property, "base64Binary", text).markLocation(line(node), col(node)));
          } else {
            element.getChildren().add(new Element("dataString", property, "string", text).markLocation(line(node), col(node)));
          }
        } else {
          element.getChildren().add(
              new Element(property.getName(), property, property.getType(), text).markLocation(line(node), col(node)));
        }
      } 
    	else {
        logError(line(node), col(node), path, IssueType.STRUCTURE, context.formatMessage(I18nConstants.TEXT_SHOULD_NOT_BE_PRESENT), IssueSeverity.ERROR);
    	}    		
    }
    
    for (int i = 0; i < node.getAttributes().getLength(); i++) {
    	Node attr = node.getAttributes().item(i);
    	String value = attr.getNodeValue();
    	if (!validAttrValue(value)) {
        logError(line(node), col(node), path, IssueType.STRUCTURE, context.formatMessage(I18nConstants.XML_ATTR_VALUE_INVALID, attr.getNodeName()), IssueSeverity.ERROR);
    	}
    	if (!(attr.getNodeName().equals("xmlns") || attr.getNodeName().startsWith("xmlns:"))) {
      	Property property = getAttrProp(properties, attr.getNodeName());
      	if (property != null) {
	    	  String av = attr.getNodeValue();
	    	  if (ToolingExtensions.hasExtension(property.getDefinition(), "http://www.healthintersections.com.au/fhir/StructureDefinition/elementdefinition-dateformat"))
	    	  	av = convertForDateFormatFromExternal(ToolingExtensions.readStringExtension(property.getDefinition(), "http://www.healthintersections.com.au/fhir/StructureDefinition/elementdefinition-dateformat"), av);
	    		if (property.getName().equals("value") && element.isPrimitive())
	    			element.setValue(av);
	    		else
	    	    element.getChildren().add(new Element(property.getName(), property, property.getType(), av).markLocation(line(node), col(node)));
        } else {
          boolean ok = false;
          if (FormatUtilities.FHIR_NS.equals(node.getNamespaceURI())) {
            if (attr.getLocalName().equals("schemaLocation") && FormatUtilities.NS_XSI.equals(attr.getNamespaceURI())) {
              ok = ok || allowXsiLocation; 
            }
          } else
            ok = ok || (attr.getLocalName().equals("schemaLocation")); // xsi:schemalocation allowed for non FHIR content
          ok = ok || (hasTypeAttr(element) && attr.getLocalName().equals("type") && FormatUtilities.NS_XSI.equals(attr.getNamespaceURI())); // xsi:type allowed if element says so
          if (!ok)  
            logError(line(node), col(node), path, IssueType.STRUCTURE, context.formatMessage(I18nConstants.UNDEFINED_ATTRIBUTE__ON__FOR_TYPE__PROPERTIES__, attr.getNodeName(), node.getNodeName(), element.fhirType(), properties), IssueSeverity.ERROR);
      	}
    	}
    }
    
    Node child = node.getFirstChild();
    while (child != null) {
    	if (child.getNodeType() == Node.ELEMENT_NODE) {
    		Property property = getElementProp(properties, child.getLocalName());
    		if (property != null) {
    			if (!property.isChoice() && "xhtml".equals(property.getType())) {
    			  XhtmlNode xhtml;
    			  if (property.getDefinition().hasRepresentation(PropertyRepresentation.CDATEXT))
    			    xhtml = new CDANarrativeFormat().convert((org.w3c.dom.Element) child);
          	else 
              xhtml = new XhtmlParser().setValidatorMode(true).parseHtmlNode((org.w3c.dom.Element) child);
						element.getChildren().add(new Element(property.getName(), property, "xhtml", new XhtmlComposer(XhtmlComposer.XML, false).compose(xhtml)).setXhtml(xhtml).markLocation(line(child), col(child)));
    			} else {
    			  String npath = path+"/"+pathPrefix(child.getNamespaceURI())+child.getLocalName();
    				Element n = new Element(child.getLocalName(), property).markLocation(line(child), col(child));
    				checkElement((org.w3c.dom.Element) child, npath, n.getProperty());
    				boolean ok = true;
    				if (property.isChoice()) {
    					if (property.getDefinition().hasRepresentation(PropertyRepresentation.TYPEATTR)) {
    						String xsiType = ((org.w3c.dom.Element) child).getAttributeNS(FormatUtilities.NS_XSI, "type");
    						if (Utilities.noString(xsiType)) {
                  if (ToolingExtensions.hasExtension(property.getDefinition(), "http://hl7.org/fhir/StructureDefinition/elementdefinition-defaulttype")) {
                    xsiType = ToolingExtensions.readStringExtension(property.getDefinition(), "http://hl7.org/fhir/StructureDefinition/elementdefinition-defaulttype");
                    n.setType(xsiType);
                  } else {
                    logError(line(child), col(child), path, IssueType.STRUCTURE, context.formatMessage(I18nConstants.NO_TYPE_FOUND_ON_, child.getLocalName()), IssueSeverity.ERROR);
      		          ok = false;
                  }
    						} else {
    							if (xsiType.contains(":"))
    								xsiType = xsiType.substring(xsiType.indexOf(":")+1);
    							n.setType(xsiType);
    							n.setExplicitType(xsiType);
    						}
    					} else
    					  n.setType(n.getType());
    				}
    				element.getChildren().add(n);
    				if (ok) {
    					if (property.isResource())
                parseResource(npath, (org.w3c.dom.Element) child, n, property);
    					else
    						parseChildren(npath, (org.w3c.dom.Element) child, n);
    				}
    			}
      	} else
          logError(line(child), col(child), path, IssueType.STRUCTURE, context.formatMessage(I18nConstants.UNDEFINED_ELEMENT_, child.getLocalName()), IssueSeverity.ERROR);
    	} else if (child.getNodeType() == Node.CDATA_SECTION_NODE){
        logError(line(child), col(child), path, IssueType.STRUCTURE, context.formatMessage(I18nConstants.CDATA_IS_NOT_ALLOWED), IssueSeverity.ERROR);
    	} else if (!Utilities.existsInList(child.getNodeType(), 3, 8)) {
        logError(line(child), col(child), path, IssueType.STRUCTURE, context.formatMessage(I18nConstants.NODE_TYPE__IS_NOT_ALLOWED, Integer.toString(child.getNodeType())), IssueSeverity.ERROR);
    	}
    	child = child.getNextSibling();
    }
  }

  private boolean validAttrValue(String value) {
    if (version == null) {
      return true;
    }
    if (version.equals("1.0")) {
      boolean ok = true;
      for (char ch : value.toCharArray()) {
        if (ch <= 0x1F && !Utilities.existsInList(ch, '\r', '\n', '\t')) {
          ok = false;
        }
      }
      return ok;
    } else
      return true;
  }


  private Property getElementProp(List<Property> properties, String nodeName) {
		List<Property> propsSortedByLongestFirst = new ArrayList<Property>(properties);
		// sort properties according to their name longest first, so .requestOrganizationReference comes first before .request[x]
		// and therefore the longer property names get evaluated first
		Collections.sort(propsSortedByLongestFirst, new Comparator<Property>() {
			@Override
			public int compare(Property o1, Property o2) {
				return o2.getName().length() - o1.getName().length();
			}
		});
  	for (Property p : propsSortedByLongestFirst)
  		if (!p.getDefinition().hasRepresentation(PropertyRepresentation.XMLATTR) && !p.getDefinition().hasRepresentation(
          PropertyRepresentation.XMLTEXT)) {
  		  if (p.getName().equals(nodeName)) 
				  return p;
  		  if (p.getName().endsWith("[x]") && nodeName.length() > p.getName().length()-3 && p.getName().substring(0, p.getName().length()-3).equals(nodeName.substring(0, p.getName().length()-3))) 
				  return p;
  		}
  	return null;
	}

	private Property getAttrProp(List<Property> properties, String nodeName) {
  	for (Property p : properties)
  		if (p.getName().equals(nodeName) && p.getDefinition().hasRepresentation(
          PropertyRepresentation.XMLATTR))
				return p;
  	return null;
  }

	private Property getTextProp(List<Property> properties) {
  	for (Property p : properties)
  		if (p.getDefinition().hasRepresentation(PropertyRepresentation.XMLTEXT)) 
				return p;
  	return null;
	}

	private String convertForDateFormatFromExternal(String fmt, String av) throws FHIRException {
  	if ("v3".equals(fmt)) {
  		DateTimeType d = DateTimeType.parseV3(av);
  		return d.asStringValue();
  	} else
      throw new FHIRException(context.formatMessage(I18nConstants.UNKNOWN_DATA_FORMAT_, fmt));
	}

  private String convertForDateFormatToExternal(String fmt, String av) throws FHIRException {
    if ("v3".equals(fmt)) {
      DateTimeType d = new DateTimeType(av);
      return d.getAsV3();
    } else
      throw new FHIRException(context.formatMessage(I18nConstants.UNKNOWN_DATE_FORMAT_, fmt));
  }

  private void parseResource(String string, org.w3c.dom.Element container, Element parent, Property elementProperty) throws FHIRFormatError, DefinitionException, FHIRException, IOException {
  	org.w3c.dom.Element res = XMLUtil.getFirstChild(container);
    String name = res.getLocalName();
    StructureDefinition sd = context.fetchResource(StructureDefinition.class, ProfileUtilities.sdNs(name, context.getOverrideVersionNs()));
    if (sd == null)
      throw new FHIRFormatError(context.formatMessage(I18nConstants.CONTAINED_RESOURCE_DOES_NOT_APPEAR_TO_BE_A_FHIR_RESOURCE_UNKNOWN_NAME_, res.getLocalName()));
    parent.updateProperty(new Property(context, sd.getSnapshot().getElement().get(0), sd), SpecialElement.fromProperty(parent.getProperty()), elementProperty);
    parent.setType(name);
    parseChildren(res.getLocalName(), res, parent);
	}

	private void reapComments(org.w3c.dom.Element element, Element context) {
	  Node node = element.getPreviousSibling();
	  while (node != null && node.getNodeType() != Node.ELEMENT_NODE) {
	  	if (node.getNodeType() == Node.COMMENT_NODE)
	  		context.getComments().add(0, node.getTextContent());
	  	node = node.getPreviousSibling();
	  }
		node = element.getLastChild();
		while (node != null && node.getNodeType() != Node.ELEMENT_NODE) {
			node = node.getPreviousSibling();
		}
		while (node != null) {
			if (node.getNodeType() == Node.COMMENT_NODE)
				context.getComments().add(node.getTextContent());
			node = node.getNextSibling();
		}
	}

  private boolean isAttr(Property property) {
    for (Enumeration<PropertyRepresentation> r : property.getDefinition().getRepresentation()) {
      if (r.getValue() == PropertyRepresentation.XMLATTR) {
        return true;
      }
    }
    return false;
  }

  private boolean isCdaText(Property property) {
    for (Enumeration<PropertyRepresentation> r : property.getDefinition().getRepresentation()) {
      if (r.getValue() == PropertyRepresentation.CDATEXT) {
        return true;
      }
    }
    return false;
  }

  private boolean isTypeAttr(Property property) {
    for (Enumeration<PropertyRepresentation> r : property.getDefinition().getRepresentation()) {
      if (r.getValue() == PropertyRepresentation.TYPEATTR) {
        return true;
      }
    }
    return false;
  }

  private boolean isText(Property property) {
		for (Enumeration<PropertyRepresentation> r : property.getDefinition().getRepresentation()) {
			if (r.getValue() == PropertyRepresentation.XMLTEXT) {
				return true;
			}
		}
		return false;
  }

	@Override
  public void compose(Element e, OutputStream stream, OutputStyle style, String base) throws IOException, FHIRException {
	  XMLWriter xml = new XMLWriter(stream, "UTF-8");
    xml.setSortAttributes(false);
    xml.setPretty(style == OutputStyle.PRETTY);
    xml.start();
    xml.setDefaultNamespace(e.getProperty().getNamespace());
    if (hasTypeAttr(e))
      xml.namespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
    composeElement(xml, e, e.getType(), true);
    xml.end();

  }

  private boolean hasTypeAttr(Element e) {
    if (isTypeAttr(e.getProperty()))
      return true;
    for (Element c : e.getChildren()) {
      if (hasTypeAttr(c))
        return true;
    }
    return false;
  }


  public void compose(Element e, IXMLWriter xml) throws Exception {
    xml.start();
    xml.setDefaultNamespace(e.getProperty().getNamespace());
    composeElement(xml, e, e.getType(), true);
    xml.end();
  }

  private void composeElement(IXMLWriter xml, Element element, String elementName, boolean root) throws IOException, FHIRException {
    if (showDecorations) {
      @SuppressWarnings("unchecked")
      List<ElementDecoration> decorations = (List<ElementDecoration>) element.getUserData("fhir.decorations");
      if (decorations != null)
        for (ElementDecoration d : decorations)
          xml.decorate(d);
    }
    for (String s : element.getComments()) {
      xml.comment(s, true);
    }
    if (isText(element.getProperty())) {
      if (linkResolver != null)
        xml.link(linkResolver.resolveProperty(element.getProperty()));
      xml.enter(elementName);
      xml.text(element.getValue());
      xml.exit(elementName);   
    } else if (!element.hasChildren() && !element.hasValue()) {
      if (element.getExplicitType() != null)
        xml.attribute("xsi:type", element.getExplicitType());
      xml.element(elementName);
    } else if (element.isPrimitive() || (element.hasType() && isPrimitive(element.getType()))) {
      if (element.getType().equals("xhtml")) {
        String rawXhtml = element.getValue();
        if (isCdaText(element.getProperty())) {
          new CDANarrativeFormat().convert(xml, element.getXhtml());
        } else {
          xml.escapedText(rawXhtml);
          xml.anchor("end-xhtml");
        }
      } else if (isText(element.getProperty())) {
        if (linkResolver != null)
          xml.link(linkResolver.resolveProperty(element.getProperty()));
        xml.text(element.getValue());
      } else {
        if (isTypeAttr(element.getProperty()) && !Utilities.noString(element.getType())) {
          xml.attribute("xsi:type", element.getType());
        }
        if (element.hasValue()) {
          if (linkResolver != null)
            xml.link(linkResolver.resolveType(element.getType()));
        xml.attribute("value", element.getValue());
        }
        if (linkResolver != null)
          xml.link(linkResolver.resolveProperty(element.getProperty()));
				if (element.hasChildren()) {
					xml.enter(elementName);
					for (Element child : element.getChildren()) 
						composeElement(xml, child, child.getName(), false);
					xml.exit(elementName);
				} else
          xml.element(elementName);
      }
    } else {
      if (isTypeAttr(element.getProperty()) && !Utilities.noString(element.getType())) {
        xml.attribute("xsi:type", element.getType());
      }
      for (Element child : element.getChildren()) {
        if (isAttr(child.getProperty())) {
          if (linkResolver != null)
            xml.link(linkResolver.resolveType(child.getType()));
          String av = child.getValue();
          if (ToolingExtensions.hasExtension(child.getProperty().getDefinition(), "http://www.healthintersections.com.au/fhir/StructureDefinition/elementdefinition-dateformat"))
            av = convertForDateFormatToExternal(ToolingExtensions.readStringExtension(child.getProperty().getDefinition(), "http://www.healthintersections.com.au/fhir/StructureDefinition/elementdefinition-dateformat"), av);
          xml.attribute(child.getName(), av);
      }
      }
      if (linkResolver != null)
        xml.link(linkResolver.resolveProperty(element.getProperty()));
      xml.enter(elementName);
      if (!root && element.getSpecial() != null) {
        if (linkResolver != null)
          xml.link(linkResolver.resolveProperty(element.getProperty()));
        xml.enter(element.getType());
      }
      for (Element child : element.getChildren()) {
        if (isText(child.getProperty())) {
          if (linkResolver != null)
            xml.link(linkResolver.resolveProperty(element.getProperty()));
          xml.text(child.getValue());
        } else if (!isAttr(child.getProperty()))
          composeElement(xml, child, child.getName(), false);
      }
	    if (!root && element.getSpecial() != null)
        xml.exit(element.getType());
      xml.exit(elementName);
    }
  }

  private String checkHeader(InputStream stream) throws IOException {
    try {
      // the stream will either start with the UTF-8 BOF or with <xml
      int i0 = stream.read();
      int i1 = stream.read();
      int i2 = stream.read();
      
      StringBuilder b = new StringBuilder();
      if (i0 == 0xEF && i1 == 0xBB && i2 == 0xBF) {
        // ok, it's UTF-8
      } else if (i0 == 0x3C && i1 == 0x3F && i2 == 0x78) { // <xm
        b.append((char) i0);
        b.append((char) i1);
        b.append((char) i2);
      } else if (i0 == 60) { // just plain old XML with no header
        return "1.0";        
      } else {
        throw new Exception(context.formatMessage(I18nConstants.XML_ENCODING_INVALID));
      }
      int i = stream.read();
      do {
        b.append((char) i);
        i = stream.read();
      } while (i != 0x3E);
      String header = b.toString();
      String e = null;
      i = header.indexOf("encoding=\"");
      if (i > -1) {
        e = header.substring(i+10, i+15);
      } else {
        i = header.indexOf("encoding='");
        if (i > -1) {
          e = header.substring(i+10, i+15);
        } 
      }
      if (e != null && !"UTF-8".equalsIgnoreCase(e)) {
        logError(0, 0, "XML", IssueType.INVALID, context.formatMessage(I18nConstants.XML_ENCODING_INVALID), IssueSeverity.ERROR);
      }

      i = header.indexOf("version=\"");
      if (i > -1) {
        return header.substring(i+9, i+12);
      } else {
        i = header.indexOf("version='");
        if (i > -1) {
          return header.substring(i+9, i+12);          
        } 
      }
      return "??";
    } catch (Exception e) {
      // suppress this error 
      logError(0, 0, "XML", IssueType.INVALID, e.getMessage(), IssueSeverity.ERROR);
    }
    return "??";
  }

}
