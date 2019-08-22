/*----------------------------------------------------------------------
    Cls_xmlmanager.java
    21-08-2019
    Descripción:
        Clase especializada en manejo de archivos XML
        version prototipo
        Oscar Barrios Landa
    email:
        barrioslandaoscar@gmail.com
-----------------------------------------------------------------------*/

package general;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import application.Cls_global;

import javax.xml.xpath.*;
import java.io.*;
import java.util.Iterator;

import org.w3c.dom.*;
import javax.xml.namespace.NamespaceContext;

public class Cls_xmlmanager
{
	public String d_mensaje;
	public int flag_error;

	public Cls_xmlmanager(){}

	public Object getd_elementvalue(String d_xmlfile,String d_xpathcomand)
	{
		Object obj_result = null;
		Object obj_return = null;

		DocumentBuilderFactory obj_domfactory = DocumentBuilderFactory.newInstance();
		obj_domfactory.setNamespaceAware(true);
		DocumentBuilder obj_docbuilder = null;
		Document	obj_document = null;

		try
		{
			obj_docbuilder = obj_domfactory.newDocumentBuilder();
			File obj_xmlfile = new File(d_xmlfile);
			obj_document = obj_docbuilder.parse(obj_xmlfile);

			XPath obj_xpath =  XPathFactory.newInstance().newXPath();
			obj_xpath.setNamespaceContext(new NSContext());

			//-- Busqueda
			XPathExpression obj_xpathExpression = obj_xpath.compile(d_xpathcomand);

			obj_result = obj_xpathExpression.evaluate(obj_document,XPathConstants.NODESET);
		    NodeList lst_nodos = (NodeList) obj_result;
		    obj_return = lst_nodos.item(0).getNodeValue();
		}
		catch (SAXException | IOException e)
		{
			e.printStackTrace();
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}


		return obj_return;
	}

	public int getn_validacionSchemaXML(String d_urlxml, String d_urlSchema)
	{
		d_urlxml = d_urlxml.trim();
		d_urlSchema = d_urlSchema.trim();

		if(d_urlxml.isEmpty())
		{
			flag_error = 1;
			d_mensaje = "El parametro d_urlxml se encuentra vacio";
			return 1;
		}

		if(d_urlSchema.isEmpty())
		{
			flag_error = 1;
			d_mensaje = "El parametro d_urlSchema se encuentra vacio";
			return 1;
		}

		try
		{

			//Create DocumentBuilderFactory
			DocumentBuilderFactory obj_factory = DocumentBuilderFactory.newInstance();

			obj_factory.setNamespaceAware(true);
			obj_factory.setValidating(true);
			obj_factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
									 "http://www.w3.org/2001/XMLSchema");
			obj_factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",
									 d_urlSchema);

			//-- Create  a DocumentBuilder
			DocumentBuilder obj_builder = obj_factory.newDocumentBuilder();

			//Create a ErrorHandler and set ErrorHandler
			// on DocumentBuilderparser
			Validator obj_errorhandler = new Validator();
			obj_builder.setErrorHandler(obj_errorhandler);

			//Parse XML Document
			obj_builder.parse(d_urlxml);

			//Output Validation Errors
			if(obj_errorhandler.b_validationError == true)
			{
				flag_error = 1;
				d_mensaje = "XML Document has Error: \n" +
							obj_errorhandler.b_validationError +" \n" +
						    obj_errorhandler.obj_saxParseException.getMessage();
				return 1;
			}

		}
		catch (java.io.IOException ioe)
		{
			System.out.println("IOException " + ioe.getMessage());
		}
		catch (SAXException e)
		{
			System.out.println("SAXException" + e.getMessage());
		}
		catch (ParserConfigurationException e)
		{
			System.out.println("ParserConfigurationException "+ e.getMessage());
		}

		return 0;
	}
	private class Validator extends DefaultHandler
	{
		public SAXParseException obj_saxParseException = null;

		public boolean b_validationError = false;

		public void error(SAXParseException exception) throws SAXException
		{
			b_validationError = true;
			obj_saxParseException = exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException
		{
			b_validationError = true;
			obj_saxParseException = exception;
		}

		public void warning(SAXParseException exception) throws SAXException {}
	}

	public class NSContext implements NamespaceContext
	{
		@Override
		public String getNamespaceURI(String prefix)
		{
			if (prefix == null)
				throw new IllegalArgumentException("prefix is null");
			else if (prefix.equals(Cls_global.SCHEMA_NAMESPACE_PREFIX))
				return Cls_global.SCHEMA_NAMESPACE;
			else
				return null;
		}

		@Override
		public String getPrefix(String uri)
		{
			return null;
		}

		@Override
		public Iterator<?> getPrefixes(String uri)
		{
			return null;
		}
	}
}
