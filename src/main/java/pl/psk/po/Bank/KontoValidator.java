package pl.psk.po.Bank;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.math.BigDecimal;
import java.util.regex.Pattern;

@Component
public class KontoValidator implements Validator {

    private static final String EMAIL_PATTERN = "s\\d{5}@student\\.tu\\.kielce\\.pl";

    @Override
    public boolean supports(Class<?> clazz) {
        return KlientBankowy.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        KlientBankowy klient = (KlientBankowy) target;
        String numerRachunku = klient.getNumerRachunku();
        String email = klient.getEmail();
        String numerPESEL = klient.getNumerPESEL();

        if (!sprawdzPoprawnoscIBAN(numerRachunku)) {
            errors.rejectValue("numerRachunku", "invalid.iban", "Nieprawidłowy numer IBAN");
        }
        if (!sprawdzPoprawnoscEmail(email)) {
            errors.rejectValue("email", "invalid.email", "Nieprawidłowy adres e-mail");
        }
    }

    private boolean sprawdzPoprawnoscIBAN(String iban) {
        iban = iban.replaceAll("\\s+", "");

        if (iban.length() != 28) {
            return false;
        }

        iban = iban.substring(4) + iban.substring(0, 4);

        StringBuilder numerCyfrowy = new StringBuilder();
        for (char c : iban.toCharArray()) {
            if (Character.isDigit(c)) {
                numerCyfrowy.append(c);
            } else if (Character.isAlphabetic(c)) {
                numerCyfrowy.append(Character.getNumericValue(c));
            } else {
                return false;
            }
        }

        BigDecimal liczba = new BigDecimal(numerCyfrowy.toString());
        return liczba.remainder(BigDecimal.valueOf(97)).intValue() == 1;
    }

    private boolean sprawdzPoprawnoscEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }
}
