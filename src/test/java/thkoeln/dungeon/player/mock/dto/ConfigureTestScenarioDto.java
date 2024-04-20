package thkoeln.dungeon.player.mock.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigureTestScenarioDto {
    private TestScenario testScenario;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "testScenario",
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FightTestScenarioSettings.class, name = "fight"),
            @JsonSubTypes.Type(value = MapExplorationTestScenarioSettings.class, name = "map_exploration")
    })
    private TestScenarioSettings testScenarioSettings;
}
