package pl.psk.po.Bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operacja {
    private String rodzajOperacji; // Rodzaj operacji (np. wpłata, wypłata, przelew)
    private double kwota; // Kwota operacji
    private LocalDateTime dataOperacji; // Data i czas wykonania operacji
}
