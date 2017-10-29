package edu.psu.teamone.main;

import java.io.File;
import java.net.URI;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Exporter {

	public static void export(File file, Schedule schedule) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("schedule");

		for (Entry<Section, Meeting> entry : schedule.getSections().entrySet()) {
			Section section = entry.getKey();
			Meeting meeting = entry.getValue();

			Element sectionElement = doc.createElement("section");
			sectionElement.setAttribute("id", Integer.toString(section.getId()));
			sectionElement.setAttribute("name", section.getName());
			sectionElement.setAttribute("abbreviation", section.getAbbreviation());
			URI uri = section.getBulletinURI();
			sectionElement.setAttribute("bulletinURI", uri == null ? "" : uri.toASCIIString());
			sectionElement.setAttribute("number", Integer.toString(section.getNumber()));
		
			Element meetingElement = doc.createElement("meeting");
			meetingElement.setAttribute("startTime", meeting.getStartTime().toLocalTime().toString());
			meetingElement.setAttribute("endTime", meeting.getStopTime().toLocalTime().toString());
			boolean days[] = meeting.getDays();
			String dayNames[] = { "monday", "tuesday", "wednesday", "thursday", "friday",
				"saturday", "sunday" };
			for (int i = 0; i < days.length; i++) {
				meetingElement.setAttribute(dayNames[i], Boolean.toString(days[i]));
			}
			sectionElement.appendChild(meetingElement);

			rootElement.appendChild(sectionElement);
		}

		doc.appendChild(rootElement);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT,"yes");
			transformer.setOutputProperty(OutputKeys.METHOD,"xml");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return;
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		System.out.println("File saved!");
	}

}
