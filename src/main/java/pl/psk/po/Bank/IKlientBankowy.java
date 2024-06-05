package pl.psk.po.Bank;

public interface IKlientBankowy {
    void wplata(double kwota);
    void wyplata(double kwota);
    void wyswietlHistorieOperacji();
    void transferMiedzyKontowy(String numerRachunkuDocelowego, double kwota);
    void wykonajPlatnoscKarta(String numerKarty, double kwota);
    void wyplacGotowke(double kwota);
    double sprawdzZadluzenieKarty(String numerKarty);
}