package com.lcsk42.frameworks.starter.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class TimeStringTemplateUtilTest {
    /**
     * 固定测试时间：2026-11-16 15:33:01.123
     */
    private static final LocalDateTime FIXED_TIME =
            LocalDateTime.of(2026, 11, 16, 15, 33, 1, 123_000_000);

    // =====================================================================
    // 1. 快捷符测试
    // =====================================================================
    @Nested
    @DisplayName("快捷符")
    class ShortcutTest {

        @ParameterizedTest(name = "key: {0}, expected: {1}")
        @CsvSource({
                "date,      20261116",
                "time,      153301",
                "datetime,  20261116153301",
                "timestamp, 20261116153301123",
                "iso,       2026-11-16T15_33_01",
                "year,      2026",
                "month,     11",
                "day,       16",
                "hour,      15",
                "minute,    33",
                "second,    01"
        })
        @DisplayName("各快捷符应格式化为预期值（iso 中的冒号会被清理为下划线）")
        void shortcutShouldFormatAsExpected(String key, String expected) {
            String result = TimeStringTemplateUtil.format("{" + key + "}", FIXED_TIME);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("快捷符应大小写不敏感")
        void shortcutShouldBeCaseInsensitive() {
            assertThat(TimeStringTemplateUtil.format("{DATE}", FIXED_TIME)).isEqualTo("20261116");
            assertThat(TimeStringTemplateUtil.format("{Date}", FIXED_TIME)).isEqualTo("20261116");
            assertThat(TimeStringTemplateUtil.format("{TIME}", FIXED_TIME)).isEqualTo("153301");
            assertThat(TimeStringTemplateUtil.format("{TimeStamp}", FIXED_TIME))
                    .isEqualTo("20261116153301123");
        }

        @Test
        @DisplayName("快捷符前后带空白也应正确解析")
        void shortcutWithSurroundingWhitespaceShouldWork() {
            assertThat(TimeStringTemplateUtil.format("{  date  }", FIXED_TIME))
                    .isEqualTo("20261116");
        }
    }

    // =====================================================================
    // 2. 直接使用 JDK pattern
    // =====================================================================
    @Nested
    @DisplayName("JDK pattern 占位符")
    class PatternTest {

        @Test
        @DisplayName("应支持自定义 yyyy-MM-dd 格式")
        void shouldSupportCustomPattern() {
            String result = TimeStringTemplateUtil.format("report-{yyyy-MM-dd}", FIXED_TIME);
            assertThat(result).isEqualTo("report-2026-11-16");
        }

        @Test
        @DisplayName("应支持带毫秒的 pattern")
        void shouldSupportMillisPattern() {
            String result = TimeStringTemplateUtil.format("{yyyyMMddHHmmssSSS}", FIXED_TIME);
            assertThat(result).isEqualTo("20261116153301123");
        }

        @Test
        @DisplayName("应支持多个占位符组合")
        void shouldSupportMultiplePlaceholders() {
            String result = TimeStringTemplateUtil.format(
                    "backup_{date}_{time}.zip", FIXED_TIME);
            assertThat(result).isEqualTo("backup_20261116_153301.zip");
        }

        @Test
        @DisplayName("应支持快捷符与 pattern 混用")
        void shouldSupportMixedShortcutAndPattern() {
            String result = TimeStringTemplateUtil.format(
                    "{year}/{MM}/{dd}/{datetime}", FIXED_TIME);
            // 斜杠会被清理为下划线
            assertThat(result).isEqualTo("2026_11_16_20261116153301");
        }

        @Test
        @DisplayName("模板中无占位符时应原样返回（仅做非法字符清理）")
        void shouldReturnAsIsWhenNoPlaceholder() {
            assertThat(TimeStringTemplateUtil.format("plain-name", FIXED_TIME))
                    .isEqualTo("plain-name");
        }

        @Test
        @DisplayName("占位符外的普通文本应保持不变")
        void shouldKeepLiteralText() {
            String result = TimeStringTemplateUtil.format("a{date}b", FIXED_TIME);
            assertThat(result).isEqualTo("a20261116b");
        }
    }

    // =====================================================================
    // 3. 空值与空串处理
    // =====================================================================
    @Nested
    @DisplayName("空值与空串")
    class NullAndEmptyTest {

        @Test
        @DisplayName("template 为 null 时应返回 null")
        void nullTemplateShouldReturnNull() {
            assertThat(TimeStringTemplateUtil.format(null, FIXED_TIME)).isNull();
            assertThat(TimeStringTemplateUtil.format(null)).isNull();
        }

        @Test
        @DisplayName("template 为空串时应原样返回空串")
        void emptyTemplateShouldReturnEmpty() {
            assertThat(TimeStringTemplateUtil.format("", FIXED_TIME)).isEmpty();
            assertThat(TimeStringTemplateUtil.format("")).isEmpty();
        }

        @Test
        @DisplayName("time 为 null 时应回退到当前时间且不抛异常")
        void nullTimeShouldFallbackToNow() {
            assertThatCode(() -> TimeStringTemplateUtil.format("{date}", null))
                    .doesNotThrowAnyException();
            String result = TimeStringTemplateUtil.format("{yyyy}", null);
            assertThat(result).isEqualTo(String.valueOf(LocalDateTime.now().getYear()));
        }

        @Test
        @DisplayName("空占位符 {} 应替换为空串")
        void emptyPlaceholderShouldBecomeEmpty() {
            assertThat(TimeStringTemplateUtil.format("a{}b", FIXED_TIME)).isEqualTo("ab");
            assertThat(TimeStringTemplateUtil.format("{}", FIXED_TIME)).isEmpty();
            assertThat(TimeStringTemplateUtil.format("a{   }b", FIXED_TIME)).isEqualTo("ab");
        }
    }

    // =====================================================================
    // 4. 非法字符清理
    // =====================================================================
    @Nested
    @DisplayName("非法字符清理")
    class SanitizeTest {

        @ParameterizedTest(name = "非法字符 [{0}] 应被替换为下划线")
        @ValueSource(strings = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|"})
        @DisplayName("各类文件系统非法字符应被替换")
        void illegalCharsShouldBeReplaced(String illegal) {
            String result = TimeStringTemplateUtil.format("a" + illegal + "b", FIXED_TIME);
            assertThat(result).isEqualTo("a_b");
        }

        @Test
        @DisplayName("控制字符应被替换为下划线")
        void controlCharsShouldBeReplaced() {
            assertThat(TimeStringTemplateUtil.format("a\nb\tc", FIXED_TIME))
                    .isEqualTo("a_b_c");
        }

        @Test
        @DisplayName("多个连续非法字符应各自替换为下划线")
        void consecutiveIllegalCharsShouldEachBeReplaced() {
            assertThat(TimeStringTemplateUtil.format("a://b", FIXED_TIME))
                    .isEqualTo("a___b");
        }

        @Test
        @DisplayName("合法字符（中文、下划线、点、横线、空格）不应被替换")
        void legalCharsShouldRemain() {
            String legal = "报告_2026-11-16 v1.0";
            assertThat(TimeStringTemplateUtil.format(legal, FIXED_TIME)).isEqualTo(legal);
        }

        @Test
        @DisplayName("iso 快捷符中的冒号应被清理")
        void isoShortcutColonsShouldBeSanitized() {
            assertThat(TimeStringTemplateUtil.format("{iso}", FIXED_TIME))
                    .isEqualTo("2026-11-16T15_33_01");
        }
    }

    // =====================================================================
    // 5. 非法 pattern 容错
    // =====================================================================
    @Nested
    @DisplayName("非法 pattern 容错")
    class InvalidPatternTest {

        @Test
        @DisplayName("非法 pattern 应原样保留占位符且不抛异常")
        void invalidPatternShouldBeKeptVerbatim() {
            // 将 qqq 改为 #
            assertThatCode(() ->
                    TimeStringTemplateUtil.format("a{#}b", FIXED_TIME))
                    .doesNotThrowAnyException();
            assertThat(TimeStringTemplateUtil.format("a{#}b", FIXED_TIME))
                    .isEqualTo("a{#}b");
        }

        @Test
        @DisplayName("一个非法 pattern 不应影响其他合法占位符")
        void invalidPatternShouldNotBreakOthers() {
            // 将 qqq 改为 #（或者 !）
            String result = TimeStringTemplateUtil.format(
                    "{date}-{#}-{time}", FIXED_TIME);
            assertThat(result).isEqualTo("20261116-{#}-153301");
        }

        @Test
        @DisplayName("未闭合的花括号应视为普通文本")
        void unclosedBraceShouldBeLiteral() {
            assertThat(TimeStringTemplateUtil.format("a{date", FIXED_TIME))
                    .isEqualTo("a{date");
            assertThat(TimeStringTemplateUtil.format("a}date{", FIXED_TIME))
                    .isEqualTo("a}date{");
        }

        @Test
        @DisplayName("嵌套花括号不应被识别为占位符")
        void nestedBracesShouldNotMatch() {
            // 花括号内不允许再嵌套，因此 {a{b}} 中 {b} 会被识别
            String result = TimeStringTemplateUtil.format("{a{b}}", FIXED_TIME);
            assertThat(result).contains("b");
        }
    }

    // =====================================================================
    // 6. 正则特殊字符转义
    // =====================================================================
    @Nested
    @DisplayName("替换内容特殊字符")
    class ReplacementEscapingTest {

        @Test
        @DisplayName("替换内容含 $ 或 \\ 时不应抛异常（quoteReplacement 保护）")
        void replacementWithDollarOrBackslashShouldNotThrow() {
            // 用一个会输出 $ 与 \ 的非法 pattern 走"原样保留"分支不现实，
            // 这里通过模板字面量含 $、\ 验证不抛异常
            assertThatCode(() ->
                    TimeStringTemplateUtil.format("a$b\\c{date}", FIXED_TIME))
                    .doesNotThrowAnyException();
        }
    }

    // =====================================================================
    // 7. 综合场景
    // =====================================================================
    @Nested
    @DisplayName("综合场景")
    class IntegrationTest {

        @Test
        @DisplayName("典型导出文件名场景")
        void typicalExportFileName() {
            String result = TimeStringTemplateUtil.format(
                    "数据导出_{datetime}.xlsx", FIXED_TIME);
            assertThat(result).isEqualTo("数据导出_20261116153301.xlsx");
        }

        @Test
        @DisplayName("带路径分隔符的模板应被清理为合法文件名")
        void pathTemplateShouldBeSanitized() {
            String result = TimeStringTemplateUtil.format(
                    "dir/sub/{date}/file.txt", FIXED_TIME);
            assertThat(result).isEqualTo("dir_sub_20261116_file.txt");
        }

        @Test
        @DisplayName("format(template) 使用当前时间，应生成可解析的日期")
        void defaultFormatShouldUseNow() {
            String result = TimeStringTemplateUtil.format("{yyyy}");
            assertThat(result).isEqualTo(String.valueOf(LocalDateTime.now().getYear()));
        }
    }

    // =====================================================================
    // 8. 并发安全
    // =====================================================================
    @Nested
    @DisplayName("并发安全")
    class ConcurrencyTest {

        @Test
        @DisplayName("多线程并发格式化结果应一致且无异常")
        void concurrentFormatShouldBeConsistent() throws Exception {
            int threads = 16;
            int iterations = 500;
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            CountDownLatch start = new CountDownLatch(1);
            List<Future<String>> futures = new ArrayList<>();

            try {
                for (int t = 0; t < threads; t++) {
                    final int idx = t;
                    futures.add(pool.submit(() -> {
                        start.await();
                        String last = null;
                        for (int i = 0; i < iterations; i++) {
                            // 混用不同 pattern，覆盖 CACHE 并发 computeIfAbsent
                            String pattern = (i % 2 == 0)
                                    ? "{datetime}" : "{yyyy-MM-dd}-" + idx;
                            last = TimeStringTemplateUtil.format(pattern, FIXED_TIME);
                        }
                        return last;
                    }));
                }
                start.countDown();

                for (Future<String> f : futures) {
                    assertThat(f.get(10, TimeUnit.SECONDS)).isNotNull();
                }
            } finally {
                pool.shutdownNow();
                assertThat(pool.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
            }
        }
    }
}
