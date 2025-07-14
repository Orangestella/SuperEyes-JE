package util;

import java.nio.file.Path;

public class CharacterValidationStrategy implements AbstractValidationStrategy{
    @Override
    public boolean validate(Path path) {
        return false;
    }
}
