package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.AppResourceService;
import com.sqli.imputation.service.dto.AppChargeDTO;
import com.sqli.imputation.service.dto.AppRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class DefaultAppResourceService implements AppResourceService {

    public static final String REQUEST_ACTION = "getAllImputProjetMonth";
    private final String APP_SOAP_ENDPOINT_URL = "https://sqli.steering-project.com/sdp/administration/imputation_cra.php";
    private final String APP_SOAP_ACTION = "https://sqli.steering-project.com/sdp/administration/imputation_cra.php/getAllImputProjetMonth";
    private final String NAME_SPACE = "ns1";
    private final String NAME_SPACE_URL = "http://webservice";

    @Autowired
    private AppParserService appParserService;

    @Override
    public List<AppChargeDTO> getTeamCharges(AppRequestDTO requestBody) throws IOException, SOAPException {
        return appParserService.parse(callSoapWebService(requestBody));
    }

    private String callSoapWebService(AppRequestDTO requestBody) throws SOAPException, IOException {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(requestBody), APP_SOAP_ENDPOINT_URL);

        System.out.println("Response SOAP Message:");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapResponse.writeTo(out);
        String strMsg = new String(out.toByteArray());
        soapConnection.close();
        return strMsg;
    }

    private SOAPMessage createSOAPRequest(AppRequestDTO requestBody) throws SOAPException, IOException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(soapMessage, requestBody);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", APP_SOAP_ACTION);

        soapMessage.saveChanges();

        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

    private void createSoapEnvelope(SOAPMessage soapMessage, AppRequestDTO requestBody) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(NAME_SPACE, NAME_SPACE_URL);
        envelope.addNamespaceDeclaration("ns2", "http://xml.apache.org/xml-soap");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");

        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement(REQUEST_ACTION, NAME_SPACE);
        SOAPElement params = soapBodyElem.addChildElement("params");
        params.setAttribute("xsi:type", "ns2:Map");

        addItemParam(params, "code_projet", requestBody.getAgresso());
        addItemParam(params, "mois", "" + requestBody.getMonth());
        addItemParam(params, "annee", "" + requestBody.getYear());
    }

    private void addItemParam(SOAPElement params, String key, String value) throws SOAPException {
        SOAPElement itemElement = params.addChildElement("item");
        SOAPElement keyElement = itemElement.addChildElement("key");
        keyElement.setAttribute("xsi:type", "xsd:string");
        SOAPElement valueElement = itemElement.addChildElement("value");
        valueElement.setAttribute("xsi:type", "xsd:string");
        keyElement.addTextNode(key);
        valueElement.addTextNode(value);
    }
}
