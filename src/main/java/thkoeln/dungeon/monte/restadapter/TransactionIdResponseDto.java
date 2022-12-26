package thkoeln.dungeon.monte.restadapter;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

/**
 * DTO for the response type that GameService sends back as an answer to all kinds of
 * commands: Just with a transactionId in it.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TransactionIdResponseDto {
    private UUID transactionId = null;

    public boolean isValid() {
        return getTransactionId() != null;
    }
}
