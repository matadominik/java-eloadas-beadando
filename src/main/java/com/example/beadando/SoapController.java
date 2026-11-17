package com.example.beadando; // a te csomagnevedet írd ide

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import soapclient.MNBArfolyamServiceSoap;
import soapclient.MNBArfolyamServiceSoapImpl;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SoapController {

    @GetMapping("/soap-teszt")
    public String teszt() throws Exception {
        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();
        return service.getExchangeRates("2026-10-01", "2026-11-01", "EUR");
    }

    @GetMapping("/soap-teszt2")
    public List<Arfolyam> teszt2() throws Exception {
        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();
        String xml = service.getExchangeRates("2026-10-01", "2026-11-01", "EUR");

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
        NodeList napok = doc.getElementsByTagName("Day");

        List<Arfolyam> lista = new ArrayList<>();
        for (int i = napok.getLength() - 1; i >= 0; i--) {   // visszafelé: régebbitől az újabbig
            Element nap = (Element) napok.item(i);
            Element rate = (Element) nap.getElementsByTagName("Rate").item(0);
            double ertek = Double.parseDouble(rate.getTextContent().replace(",", "."));
            lista.add(new Arfolyam(nap.getAttribute("date"), ertek));
        }
        return lista;
    }
}