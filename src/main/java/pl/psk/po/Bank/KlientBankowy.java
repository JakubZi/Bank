package pl.psk.po.Bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KlientBankowy implements IKlientBankowy, IKartaKredytowa, IKartaDebetowa, IKartaPrzedplacona {
    private String imie;
    private String nazwisko;
    private String numerRachunku;
    private double saldo;
    private List<Operacja> historiaOperacji;
    private String numerPESEL;
    private String email;
    private String numerKarty;
    private String numerKartyDebetowej;
    private String numerKartyPrzedplaconej;

    public boolean czyKartaDebetowa() {
        return numerKartyDebetowej != null && !numerKartyDebetowej.isEmpty();
    }

    public boolean czyKartaKredytowa() {
        return numerKarty != null && !numerKarty.isEmpty();
    }
    public String getNumerKartyDebetowej() {
        return numerKartyDebetowej;
    }

    public void setNumerKartyDebetowej(String numerKartyDebetowej) {
        this.numerKartyDebetowej = numerKartyDebetowej;
    }

    public String getNumerKarty() {
        return numerKarty;
    }

    public void setNumerKarty(String numerKarty) {
        this.numerKarty = numerKarty;
    }
    @Override
    public void wplata(double kwota) {
        if (kwota <= 0) {
            throw new IllegalArgumentException("Kwota wpłaty musi być większa od zera");
        }
        this.saldo += kwota;
        dodajOperacje("Wpłata", kwota);
    }

    @Override
    public void wyplata(double kwota) {
        if (kwota <= 0) {
            throw new IllegalArgumentException("Kwota wypłaty musi być większa od zera");
        }
        if (kwota > saldo) {
            throw new IllegalArgumentException("Niewystarczające środki na koncie");
        }
        this.saldo -= kwota;
        if (this.saldo < 0) {
            throw new IllegalStateException("Saldo karty jest ujemne, zadłużenie: " + Math.abs(this.saldo));
        }
        dodajOperacje("Wypłata", -kwota);
    }

    @Override
    public void transferMiedzyKontowy(String numerRachunkuDocelowego, double kwota) {
        if (kwota <= 0) {
            throw new IllegalArgumentException("Kwota przelewu musi być większa od zera");
        }
        if (kwota > saldo) {
            throw new IllegalArgumentException("Niewystarczające środki na koncie");
        }
        this.saldo -= kwota;
        dodajOperacje("Przelew wychodzący", -kwota);
    }

    @Override
    public void wykonajPlatnoscKarta(String numerKarty, double kwota) {
        if (kwota <= 0) {
            throw new IllegalArgumentException("Kwota płatności musi być większa od zera");
        }

        if (numerKarty.equals(this.numerKarty)) {

            this.saldo -= kwota;
        } else if (numerKarty.equals(this.numerKartyDebetowej) || numerKarty.equals(this.numerKartyPrzedplaconej)) {

            if (kwota > saldo) {
                throw new IllegalArgumentException("Niewystarczające środki na koncie");
            }
            this.saldo -= kwota;
        } else {
            throw new IllegalArgumentException("Podana karta nie jest przypisana do tego rachunku");
        }
        dodajOperacje("Płatność kartą", -kwota);
    }


    @Override
    public void wyplacGotowke(double kwota) {
        if (kwota <= 0) {
            throw new IllegalArgumentException("Kwota wypłaty musi być większa od zera");
        }
        if (kwota > saldo) {
            throw new IllegalArgumentException("Niewystarczające środki na koncie");
        }
        this.saldo -= kwota;
        dodajOperacje("Wypłata gotówki", -kwota);
    }


    @Override
    public void wyswietlHistorieOperacji() {
        for (Operacja operacja : historiaOperacji) {
            System.out.println(operacja);
        }
    }

    private void dodajOperacje(String rodzajOperacji, double kwota) {
        if (historiaOperacji == null) {
            historiaOperacji = new ArrayList<>();
        }
        historiaOperacji.add(new Operacja(rodzajOperacji, kwota, LocalDateTime.now()));
    }

    @Override
    public double sprawdzZadluzenieKarty(String numerKarty) {
        if (numerKarty.equals(this.numerKarty)) {

            return Math.abs(this.saldo);
        } else if (numerKarty.equals(this.numerKartyDebetowej) || numerKarty.equals(this.numerKartyPrzedplaconej)) {

            return 0;
        } else {
            throw new IllegalArgumentException("Podana karta nie jest przypisana do tego rachunku");
        }
    }


}
