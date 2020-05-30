package io.github.fvarrui.javapackager.utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {

	public static final void prettify(File file) throws MojoExecutionException {
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);

			trimWhitespace(document);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,"yes");			
			transformer.transform(new DOMSource(document), new StreamResult(file));

		} catch (Exception e) {

			throw new MojoExecutionException(e.getMessage(), e);

		}
	}
	
	public static void trimWhitespace(Node node) {
	    NodeList children = node.getChildNodes();
	    for(int i = 0; i < children.getLength(); ++i) {
	        Node child = children.item(i);
	        if(child.getNodeType() == Node.TEXT_NODE) {
	            child.setTextContent(child.getTextContent().trim());
	        }
	        trimWhitespace(child);
	    }
	}

}
