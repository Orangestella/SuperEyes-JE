package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

public record FilterResult(
        Path file,               // 配置文件路径
        boolean selectable,      // 是否可选
        List<String> reasons)    // 不可选原因，空列表=完全合法
{
    @Contract("_ -> new")
    public static @NotNull FilterResult ok(Path file) {
        return new FilterResult(file, true, List.of());
    }

    @Contract("_, _ -> new")
    public static @NotNull FilterResult reject(Path file, List<String> reasons) {
        return new FilterResult(file, false, reasons);
    }
}
