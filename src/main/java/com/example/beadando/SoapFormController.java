package com.example.beadando;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

@Controller
public class SoapFormController {
    @GetMapping("/soap")
    public String urlap(Model model) {
        model.addAttribute("param", new MessagePrice());
        model.addAttribute("currentPage", "soap");
        return "soap_form";
    }

    @PostMapping("/soap")
    public String eredmeny(@ModelAttribute MessagePrice messagePrice, Model model) throws Exception {
        List<Arfolyam> lista = lekerdez(messagePrice.getStartDate(),
                messagePrice.getEndDate(), messagePrice.getCurrency());
        model.addAttribute("currency", messagePrice.getCurrency());
        model.addAttribute("rates", lista);
        model.addAttribute("currentPage", "soap");
        return "soap_result";
    }

    private List<Arfolyam> lekerdez(String kezdo, String veg, String deviza) throws Exception {
        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();
        String xml = service.getExchangeRates(kezdo, veg, deviza);

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
        NodeList napok = doc.getElementsByTagName("Day");

        List<Arfolyam> lista = new ArrayList<>();
        for (int i = napok.getLength() - 1; i >= 0; i--) {
            Element nap = (Element) napok.item(i);
            Element rate = (Element) nap.getElementsByTagName("Rate").item(0);
            double ertek = Double.parseDouble(rate.getTextContent().replace(",", "."));
            lista.add(new Arfolyam(nap.getAttribute("date"), ertek));
        }
        return lista;
    }
}
