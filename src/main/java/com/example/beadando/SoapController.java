package com.example.beadando; // a te csomagnevedet írd ide

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import soapclient.MNBArfolyamServiceSoap;
import soapclient.MNBArfolyamServiceSoapImpl;

@RestController
public class SoapController {

    @GetMapping("/soap-teszt")
    public String teszt() throws Exception {
        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();
        return service.getExchangeRates("2026-10-01", "2026-11-01", "EUR");
    }
}