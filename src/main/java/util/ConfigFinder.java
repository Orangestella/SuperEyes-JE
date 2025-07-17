package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;


public final class ConfigFinder {

    /** 缓存目录：根目录/.cache/ */
    private static final Path CACHE_DIR = Paths.get(".cache");
    private static final Object CACHE_DIR_LOCK = new Object();
    /**
     * 主 API：带缓存的 locate
     * 1. 先看缓存文件是否存在 → 路径是否有效
     * 2. 无效或缺失 → 重新 locate → 写入缓存
     */
    public static @NotNull Path locate(@NotNull ConfigFindStrategy strategy) throws IOException {
        Path cacheFile = cacheFile(strategy.fileName());
        // 读缓存
        if (Files.isRegularFile(cacheFile)) {
            Path cached = Paths.get(Files.readString(cacheFile, StandardCharsets.UTF_8).trim());
            if (Files.isRegularFile(cached)) {
                return cached;
            }
            // 缓存失效，删除
            Files.deleteIfExists(cacheFile);
        }

        // 未命中缓存，走完整流程
        Path realPath = locateWithoutCache(strategy);

        // 写入缓存
        ensureCacheDir();
        Files.writeString(cacheFile, realPath.toAbsolutePath().toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return realPath;
    }

    /* ---------------- 原 locate 逻辑，去掉缓存 ---------------- */
    private static Path locateWithoutCache(@NotNull ConfigFindStrategy strategy) throws IOException {
        // 1. 首选路径
        Path p = strategy.preferredPath();
        if (Files.exists(p)) {
            return p;
        }

        // 2. 递归搜索
        Path found = findRecursively(strategy.searchRoot(), strategy.fileName());
        if (found != null) {
            return found;
        }

        // 3. 创建默认
        Files.createDirectories(p.getParent());
        Files.writeString(p, strategy.defaultContent(), StandardCharsets.UTF_8);
        return p;
    }

    /* ---------------- 工具方法 ---------------- */

    @Contract(pure = true)
    private static @NotNull Path cacheFile(String fileName) {
        return CACHE_DIR.resolve(fileName + ".path");
    }

    private static void ensureCacheDir() throws IOException {
        synchronized (CACHE_DIR_LOCK) {
            if (!Files.isDirectory(CACHE_DIR)) {
                Files.createDirectories(CACHE_DIR);
            }
        }
    }

    private static @Nullable Path findRecursively(Path dir, String name) throws IOException {
        if (!Files.isDirectory(dir)) return null;
        try (var s = Files.list(dir)) {
            return s.filter(path -> {
                if (Files.isDirectory(path)) {
                    try {
                        Path r = findRecursively(path, name);
                        return r != null;
                    } catch (IOException ignore) {
                    }
                }
                return path.getFileName().toString().equals(name);
            }).findFirst().orElse(null);
        }
    }
}