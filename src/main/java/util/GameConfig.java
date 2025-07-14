package util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public record GameConfig(
        List<Double> scalingFactorRegion,
        int totalScalingFactorLimitation,
        int initialSkillPoint,
        List<Integer> skillCostRegion,
        List<Integer> playerSkillPointRegion,
        List<Integer> carriedHpRegion,
        List<Double> carriedDefenseRegion,
        List<Integer> damageRegion,
        List<Integer> recoveryRegion,
        int getDamageToGetPoint,
        int makeDamageToGetPoint,
        int carriedSkillsNumber,
        List<Double> portraitsTransferMilestone,
        double initialStrikeProbability,
        double strikeStepBase,
        double randomFactorLimitation,
        double highestStrikeProbability,
        double strikeFactor,
        String font,
        String savePath,
        String logPath,
        String charactersPath,
        String skillsPath
) {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static GameConfig load(Path file) throws IOException {
        return MAPPER.readValue(file.toFile(), GameConfig.class);
    }
}
