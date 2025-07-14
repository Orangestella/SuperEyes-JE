package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class PortraitLoader {

    private Path path;
    private Map<String, String> map = Collections.emptyMap();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public PortraitLoader(Path path) {
        this.path = path;
        reload();
    }

    /** 重新读取 JSON 文件并填充映射 */
    public final void reload() {
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("Portrait config file not found: " + path.toAbsolutePath());
        }
        try {
            this.map = MAPPER.readValue(
                    Files.newBufferedReader(path),
                    new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load portrait config from " + path, e);
        }
    }

    /** 根据 state 和 position 获取 portrait */
    public Path getPortrait(String state, String position) {
        String key = state + "_" + position;
        String relative = map.get(key);
        if (relative == null) {
            throw new IllegalArgumentException("No portrait mapping for key: " + key);
        }
        // 以配置文件所在目录为基准解析相对路径
        return path.getParent().resolve(relative).normalize();
    }

    /* ---------- getter / setter ---------- */

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
        reload();
    }
}