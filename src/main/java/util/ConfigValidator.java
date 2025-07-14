package util;

import java.nio.file.Path;

public class ConfigValidator {
    private final AbstractValidationStrategy validationStrategy;

    public ConfigValidator(AbstractValidationStrategy strategy) {
        this.validationStrategy = strategy;
    }

    public AbstractValidationStrategy getValidationStrategy() {
        return validationStrategy;
    }

    public boolean validate(Path path) {
        return validationStrategy.validate(path);
    }
}