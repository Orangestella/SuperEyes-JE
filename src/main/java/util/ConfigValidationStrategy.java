package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.Map;

public class ConfigValidationStrategy implements AbstractValidationStrategy {

    private static final Set<String> EXPECTED_KEYS = Set.of(
            "scalingFactorRegion",
            "totalScalingFactorLimitation",
            "initialSkillPoint",
            "skillCostRegion",
            "playerSkillPointRegion",
            "carriedHpRegion",
            "carriedDefenseRegion",
            "damageRegion",
            "recoveryRegion",
            "getDamageToGetPoint",
            "makeDamageToGetPoint",
            "carriedSkillsNumber",
            "portraitsTransferMilestone",
            "initialStrikeProbability",
            "strikeStepBase",
            "randomFactorLimitation",
            "highestStrikeProbability",
            "strikeFactor",
            "font",
            "savePath",
            "charactersPath"
    );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean validate(Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }

        try {
            Map<String, Object> root = MAPPER.readValue(
                    Files.newBufferedReader(path),
                    new TypeReference<Map<String, Object>>() {});

            return root.keySet().equals(EXPECTED_KEYS);
        } catch (IOException e) {
            return false;
        }
    }
}
