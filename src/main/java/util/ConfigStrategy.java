package util;

import java.nio.file.Path;

public interface ConfigStrategy {
    /** 首选路径（通常放在某个子目录） */
    Path preferredPath();

    /** 备选搜索目录（根目录递归） */
    Path searchRoot();

    /** 默认 JSON 内容 */
    String defaultContent();

    /** 文件名 */
    default String fileName() {
        return preferredPath().getFileName().toString();
    }
}