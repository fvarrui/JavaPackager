package io.github.fvarrui.javapackager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML utils
 */
public class XMLUtils {

	/**
	 * Pretiffy an XML file
	 * @param file Xml file
	 * @throws Exception Something went wrong
	 */
	public static final void prettify(File file) throws Exception {
		replaceWordInXMLFile(file,"quotes","&quot;" );

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);

		trimWhitespace(document);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		DocumentType doctype = document.getDoctype();
		if(doctype != null) {
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
		}

		transformer.transform(new DOMSource(document), new StreamResult(file));

	}

	/**
	 * Removes whitespaces from nodes
	 * @param node Root node
	 */
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

	/**
	 * Replaces all occurrences of a specified word in an XML file with another word.
	 *
	 * @param file        The XML file in which the replacement will be performed.
	 * @param targetWord  The word to be replaced in the file.
	 * @param replacement The word that will replace the target word.
	 * @throws IOException If an error occurs while reading or writing the file.
	 */
	public static void replaceWordInXMLFile(File file, String targetWord, String replacement) throws IOException {
		// Leer el archivo como String
		String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

		// Reemplazar la palabra
		content = content.replaceAll(targetWord, replacement);

		// Sobrescribir el archivo con el nuevo contenido
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

}
