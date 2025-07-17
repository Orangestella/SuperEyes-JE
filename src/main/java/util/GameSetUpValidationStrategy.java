package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameSetUpValidationStrategy {

    /* ---------- 期望的全部键 ---------- */
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
    private static final long  UINT_MAX = 4_294_967_295L;
    private static final double DOUBLE_MAX = Double.MAX_VALUE;

    /**
     * 验证成功返回 Map<String,Object>，失败返回 null
     */
    public static @Nullable Map<String, Object> validate(Path path) {
        if (!Files.isRegularFile(path)) return null;

        JsonNode root;
        try {
            root = MAPPER.readTree(Files.newBufferedReader(path));
        } catch (IOException e) {
            return null;
        }

        /* 1. 键集合必须完全一致 */
        Set<String> actualKeys = root.properties()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        if (!actualKeys.equals(EXPECTED_KEYS)) return null;

        /* 2. 逐节点校验 —— 任一失败立即 null */
        boolean ok = EXPECTED_KEYS.stream()
                .allMatch(key -> validateNode(root.get(key), key));
        if (!ok) return null;

        /* 3. 全部通过，一次性转成 Map */
        try {
            return MAPPER.convertValue(root, new TypeReference<>() {});
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /* ---------- 单节点校验 ---------- */
    private static boolean validateNode(JsonNode node, String key) {
        if (node == null || node.isNull()) return false;

        /* Region：两元素递增正整数数组，元素 ≤ UINT_MAX */
        if (key.endsWith("Region")) {
            if (!node.isArray() || node.size() != 2) return false;
            long a = longValue(node.get(0));
            long b = longValue(node.get(1));
            return a < b && between(a, 1, UINT_MAX) && between(b, 1, UINT_MAX);
        }

        /* 正整数 1..UINT_MAX */
        if (Set.of("totalScalingFactorLimitation",
                "getDamageToGetPoint",
                "makeDamageToGetPoint",
                "carriedSkillsNumber").contains(key)) {
            return between(longValue(node), 1, UINT_MAX);
        }

        /* portraitsTransferMilestone：递减正整数数组，元素 ≤ UINT_MAX */
        if ("portraitsTransferMilestone".equals(key)) {
            if (!node.isArray()) return false;
            List<Long> list = MAPPER.convertValue(node, new TypeReference<>() {});
            if (list.isEmpty()) return false;
            long prev = list.get(0);
            if (!between(prev, 1, UINT_MAX)) return false;
            for (int i = 1; i < list.size(); i++) {
                long cur = list.get(i);
                if (cur >= prev || !between(cur, 1, UINT_MAX)) return false;
                prev = cur;
            }
            return true;
        }

        /* 0.00‥1.00，两位小数 */
        if (Set.of("initialStrikeProbability",
                "strikeStepBase",
                "highestStrikeProbability").contains(key)) {
            BigDecimal bd = decimalValue(node, 2);
            return bd != null && between(bd.doubleValue(), 0.00, 1.00);
        }

        /* Factor 结尾：>0 < DOUBLE_MAX，一位小数 */
        if (key.endsWith("Factor")) {
            BigDecimal bd = decimalValue(node, 1);
            return bd != null && between(bd.doubleValue(), 0, DOUBLE_MAX);
        }

        /* 字符串：非空且长度 ≤ 2048 */
        if (Set.of("font", "savePath", "charactersPath").contains(key)) {
            return node.isTextual() && !node.asText().isBlank()
                    && node.asText().length() <= 2048;
        }

        return false;
    }

    /* ---------- 工具 ---------- */
    private static long longValue(@NotNull JsonNode n) {
        return n.isIntegralNumber() ? n.longValue() : -1;
    }

    private static @Nullable BigDecimal decimalValue(@NotNull JsonNode n, int scale) {
        if (!n.isNumber()) return null;
        BigDecimal bd = new BigDecimal(n.asText()).stripTrailingZeros();
        return bd.scale() <= scale ? bd : null;
    }

    private static boolean between(long v, long min, long max) {
        return v >= min && v <= max;
    }

    private static boolean between(double v, double min, double max) {
        return v >= min && v <= max;
    }
}