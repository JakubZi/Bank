package pl.psk.po.Bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class KlientBankowyTest {

    @Test
    void wplata() {
        KlientBankowy klient = new KlientBankowy();
        double saldoBefore = klient.getSaldo();
        double kwotaWplaty = 500.0;

        klient.wplata(kwotaWplaty);

        assertEquals(saldoBefore + kwotaWplaty, klient.getSaldo());
    }

    @Test
    void wyplata() {
        KlientBankowy klient = new KlientBankowy("Jan", "Kowalski", "1234567890", 1000.0, null, "12345678901", "jan.kowalski@example.com", "1111222233334444", "2222333344445555", "3333444455556666");
        double saldoBefore = klient.getSaldo();
        double kwotaWyplaty = 500.0;

        klient.wyplata(kwotaWyplaty);

        assertEquals(saldoBefore - kwotaWyplaty, klient.getSaldo());
    }

    @Test
    void transferMiedzyKontowy() {
        KlientBankowy klient = new KlientBankowy("Jan", "Kowalski", "1234567890", 1000.0, null, "12345678901", "jan.kowalski@example.com", "1111222233334444", "2222333344445555", "3333444455556666");
        double saldoBefore = klient.getSaldo();
        double kwotaPrzelewu = 300.0;
        String numerRachunkuDocelowego = "0987654321";

        klient.transferMiedzyKontowy(numerRachunkuDocelowego, kwotaPrzelewu);

        assertEquals(saldoBefore - kwotaPrzelewu, klient.getSaldo());
    }

    @Test
    void wykonajPlatnoscKarta() {
        KlientBankowy klient = new KlientBankowy("Jan", "Kowalski", "1234567890", 1000.0, null, "12345678901", "jan.kowalski@example.com", "1111222233334444", "2222333344445555", "3333444455556666");
        double saldoBefore = klient.getSaldo();
        double kwotaPlatnosci = 200.0;
        String numerKarty = "1111222233334444";

        klient.wykonajPlatnoscKarta(numerKarty, kwotaPlatnosci);

        assertEquals(saldoBefore - kwotaPlatnosci, klient.getSaldo());
    }

    @Test
    void wyplacGotowke() {
        KlientBankowy klient = new KlientBankowy("Jan", "Kowalski", "1234567890", 1000.0, null, "12345678901", "jan.kowalski@example.com", "1111222233334444", "2222333344445555", "3333444455556666");
        double saldoBefore = klient.getSaldo();
        double kwotaWyplaty = 300.0;

        klient.wyplacGotowke(kwotaWyplaty);

        assertEquals(saldoBefore - kwotaWyplaty, klient.getSaldo());
    }

    @Test
    void wyswietlHistorieOperacji() {
        KlientBankowy klient = new KlientBankowy("Jan", "Kowalski", "1234567890", 1000.0, new ArrayList<>(), "12345678901", "jan.kowalski@example.com", "1111222233334444", "2222333344445555", "3333444455556666");

        klient.wplata(500.0);
        klient.wyplata(200.0);

        klient.wyswietlHistorieOperacji();
    }
}
