package util;

import java.nio.file.Path;

public interface AbstractValidationStrategy {
    boolean validate(Path path);
}
