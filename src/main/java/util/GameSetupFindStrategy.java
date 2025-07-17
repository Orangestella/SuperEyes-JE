package util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class GameSetupFindStrategy implements ConfigFindStrategy {
    @Override
    public Path preferredPath() {
        return Paths.get("config/GameSetUp.json");
    }

    @Override
    public Path searchRoot() {
        return Paths.get("").toAbsolutePath();
    }

    @Override
    public String defaultContent() {
        return """
            {
                "scalingFactorRegion": [0.5,1.5],
                "totalScalingFactorLimitation": 3,
                "initialSkillPoint": 3,
                "skillCostRegion": [0,8],
                "playerSkillPointRegion": [0,8],
                "carriedHpRegion": [0,500],
                "carriedDefenseRegion": [0,0.2],
                "damageRegion": [0, 2000],
                "recoveryRegion": [0, 2000],
                "getDamageToGetPoint": 350,
                "makeDamageToGetPoint": 400,
                "carriedSkillsNumber": 4,
                "portraitsTransferMilestone": [0.5, 0.2, 0],
                "initialStrikeProbability": 0.1,
                "strikeStepBase": 0.15,
                "randomFactorLimitation": 0.05,
                "highestStrikeProbability": 0.9,
                "strikeFactor": 1.5,
                "font": "assets\\fonts\\msyhbd.ttc",
                "savePath": "save",
                "logPath": "log",
                "charactersPath": "config\\characters",
                "skillsPath": "config\\skills"
            }
        """;
    }
}
