package util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomDamageManager {

    private final double initialProbability;
    private final double stepBase;
    private final double randomFactor;
    private final double highestStrikeProbability;

    private double currentProbability;

    public RandomDamageManager(double initialProbability,
                               double stepBase,
                               double randomFactor,
                               double highestStrikeProbability) {
        if (initialProbability < 0 || initialProbability > 1)
            throw new IllegalArgumentException("initialProbability must be in [0,1]");
        if (stepBase < 0)
            throw new IllegalArgumentException("stepBase must be non-negative");
        if (randomFactor < 0)
            throw new IllegalArgumentException("randomFactor must be non-negative");
        if (highestStrikeProbability < 0 || highestStrikeProbability > 1)
            throw new IllegalArgumentException("highestStrikeProbability must be in [0,1]");

        this.initialProbability = initialProbability;
        this.stepBase = stepBase;
        this.randomFactor = randomFactor;
        this.highestStrikeProbability = highestStrikeProbability;
        this.currentProbability = initialProbability;
    }

    /** 返回本次判定使用的概率（已加随机扰动并封顶） */
    public double getProbability() {
        double noise = 1.0 + (ThreadLocalRandom.current().nextDouble(-randomFactor, randomFactor));
        double p = currentProbability * noise;
        return clampAndRound(Math.min(p, highestStrikeProbability));
    }

    /** 进行一次是否“暴击”的判定，并更新内部概率 */
    public boolean isStrike() {
        double p = getProbability();
        boolean strike = ThreadLocalRandom.current().nextDouble() < p;
        if (strike) {
            currentProbability = initialProbability;
        } else {
            step();
        }
        return strike;
    }

    /** 非暴击时，让概率增长一步（带随机扰动） */
    private void step() {
        if (currentProbability >= highestStrikeProbability) {
            return;
        }
        double noise = 1.0 + ThreadLocalRandom.current().nextDouble(-randomFactor, randomFactor);
        double stepSize = stepBase * noise;
        currentProbability = clampAndRound(Math.min(currentProbability + stepSize, highestStrikeProbability));
    }

    /** 保留两位小数 */
    private static double clampAndRound(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
