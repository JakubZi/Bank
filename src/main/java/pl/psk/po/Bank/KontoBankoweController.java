package pl.psk.po.Bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/konta")
public class KontoBankoweController {

    private final Map<String, KlientBankowy> kontoMap = new HashMap<>();

    @Autowired
    private KontoValidator kontoValidator;

    @PostMapping
    public ResponseEntity<?> utworzKonto(@Valid @RequestBody KlientBankowy klient, Errors errors) {
        kontoValidator.validate(klient, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        String numerRachunku = klient.getNumerRachunku();
        if (kontoMap.containsKey(numerRachunku)) {
            return ResponseEntity.badRequest().body("Konto o podanym numerze już istnieje");
        }
        String email = klient.getEmail();
        if (czyIstniejeKontoZMailem(email)) {
            return ResponseEntity.badRequest().body("Konto z podanym adresem e-mail już istnieje");
        }

        String numerKarty = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        klient.setNumerKarty(numerKarty);


        String numerKartyDebetowej = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        klient.setNumerKartyDebetowej(numerKartyDebetowej);


        String numerKartyPrzedplaconej = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        klient.setNumerKartyPrzedplaconej(numerKartyPrzedplaconej);

        kontoMap.put(numerRachunku, klient);
        return ResponseEntity.ok("Konto utworzone");
    }

    private boolean czyIstniejeKontoZMailem(String email) {
        return kontoMap.values().stream().anyMatch(klient -> klient.getEmail().equals(email));
    }

    @PostMapping("/{numerRachunku}/wplata/{numerKarty}")
    public ResponseEntity<?> wplata(@PathVariable String numerRachunku, @RequestParam double kwota, @PathVariable String numerKarty) {
        KlientBankowy klient = kontoMap.get(numerRachunku);
        if (klient == null) {
            return ResponseEntity.notFound().build();
        }

        if (!numerKarty.equals(klient.getNumerKarty()) && !numerKarty.equals(klient.getNumerKartyDebetowej()) && !numerKarty.equals(klient.getNumerKartyPrzedplaconej())) {
            return ResponseEntity.badRequest().body("Podana karta nie jest przypisana do tego rachunku");
        }
        try {
            klient.wplata(kwota);
            return ResponseEntity.ok("Wpłata zrealizowana");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{numerRachunku}")
    public ResponseEntity<?> pobierzKonto(@PathVariable String numerRachunku) {
        KlientBankowy klient = kontoMap.get(numerRachunku);
        if (klient == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(klient);
    }
    @PostMapping("/{numerRachunku}/wyplata/{numerKarty}")
    public ResponseEntity<?> wyplata(@PathVariable String numerRachunku, @RequestParam double kwota, @PathVariable String numerKarty) {
        KlientBankowy klient = kontoMap.get(numerRachunku);
        if (klient == null) {
            return ResponseEntity.notFound().build();
        }

        if (!numerKarty.equals(klient.getNumerKarty()) && !numerKarty.equals(klient.getNumerKartyDebetowej()) && !numerKarty.equals(klient.getNumerKartyPrzedplaconej())) {
            return ResponseEntity.badRequest().body("Podana karta nie jest przypisana do tego rachunku");
        }
        try {
            klient.wyplata(kwota);
            return ResponseEntity.ok("Wypłata zrealizowana");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PostMapping("/{numerRachunku}/przelew")
    public ResponseEntity<?> przelew(@PathVariable String numerRachunku, @Valid @RequestBody PrzelewRequest przelewRequest) {
        KlientBankowy klient = kontoMap.get(numerRachunku);
        if (klient == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            klient.transferMiedzyKontowy(przelewRequest.getNumerRachunkuDocelowego(), przelewRequest.getKwota());
            return ResponseEntity.ok("Przelew zrealizowany");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{numerRachunku}/historia")
    public ResponseEntity<?> pobierzHistorieOperacji(@PathVariable String numerRachunku) {
        KlientBankowy klient = kontoMap.get(numerRachunku);
        if (klient == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(klient.getHistoriaOperacji());
    }




    @DeleteMapping("/{numerRachunku}")
    public ResponseEntity<?> usunKonto(@PathVariable String numerRachunku) {
        if (kontoMap.remove(numerRachunku) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Konto usunięte");
    }

    @PutMapping("/{numerRachunku}")
    public ResponseEntity<?> aktualizujKonto(@PathVariable String numerRachunku, @Valid @RequestBody KlientBankowy klient) {
        if (!kontoMap.containsKey(numerRachunku)) {
            return ResponseEntity.notFound().build();
        }
        kontoMap.put(numerRachunku, klient);
        return ResponseEntity.ok("Dane konta zaktualizowane");
    }

    static class PrzelewRequest {
        private String numerRachunkuDocelowego;
        private double kwota;

        public String getNumerRachunkuDocelowego() {
            return numerRachunkuDocelowego;
        }

        public void setNumerRachunkuDocelowego(String numerRachunkuDocelowego) {
            this.numerRachunkuDocelowego = numerRachunkuDocelowego;
        }

        public double getKwota() {
            return kwota;
        }

        public void setKwota(double kwota) {
            this.kwota = kwota;
        }
    }
    @PostMapping("/{numerRachunku}/platnosc/{numerKarty}")
    public ResponseEntity<?> wykonajPlatnoscKarta(@PathVariable String numerRachunku, @RequestParam double kwota, @PathVariable String numerKarty) {
        KlientBankowy klient = kontoMap.get(numerRachunku);
        if (klient == null) {
            return ResponseEntity.notFound().build();
        }
        if (!numerKarty.equals(klient.getNumerKarty()) && !numerKarty.equals(klient.getNumerKartyDebetowej()) && !numerKarty.equals(klient.getNumerKartyPrzedplaconej())) {
            return ResponseEntity.badRequest().body("Podana karta nie jest przypisana do tego rachunku");
        }
        try {
            klient.wykonajPlatnoscKarta(numerKarty, kwota);
            return ResponseEntity.ok("Płatność kartą zrealizowana");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{numerRachunku}/wyplata-gotowki")
    public ResponseEntity<?> wyplacGotowke(@PathVariable String numerRachunku, @RequestParam double kwota) {
        KlientBankowy klient = kontoMap.get(numerRachunku);
        if (klient == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            klient.wyplacGotowke(kwota);
            return ResponseEntity.ok("Wypłata gotówki zrealizowana");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{numerRachunku}/zadluzenie/{numerKarty}")
    public ResponseEntity<?> sprawdzZadluzenieKarty(@PathVariable String numerRachunku, @PathVariable String numerKarty) {
        KlientBankowy klient = kontoMap.get(numerRachunku);
        if (klient == null) {
            return ResponseEntity.notFound().build();
        }

        double zadluzenie = klient.sprawdzZadluzenieKarty(numerKarty);

        return ResponseEntity.ok("Zadłużenie karty: " + zadluzenie);
    }


}
