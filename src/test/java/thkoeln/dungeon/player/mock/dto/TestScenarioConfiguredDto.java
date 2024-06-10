package thkoeln.dungeon.player.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestScenarioConfiguredDto {
    UUID gameId;
    TestScenario testScenario;
}
