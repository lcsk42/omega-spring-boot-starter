package com.lcsk42.frameworks.starter.excel.fastexcel.util;

import cn.idev.excel.FastExcelFactory;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.lcsk42.frameworks.starter.common.util.LocalDateTimeUtil;
import com.lcsk42.frameworks.starter.convention.exception.ServiceException;
import com.lcsk42.frameworks.starter.excel.fastexcel.convert.ExcelBaseEnumConverter;
import com.lcsk42.frameworks.starter.excel.fastexcel.convert.ExcelBigNumberConverter;
import com.lcsk42.frameworks.starter.excel.fastexcel.convert.ExcelListConverter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelUtil {
    /**
     * 导出
     *
     * @param list 导出数据集合
     * @param fileName 文件名
     * @param clazz 导出数据类型
     * @param response 响应对象
     */
    public static <T> void export(List<T> list, String fileName, Class<T> clazz,
            HttpServletResponse response) {
        export(list, fileName, "Sheet1", Collections.emptySet(), clazz, response);
    }

    /**
     * 导出
     *
     * @param list 导出数据集合
     * @param fileName 文件名
     * @param sheetName 工作表名称
     * @param excludeColumnFieldNames 排除字段
     * @param clazz 导出数据类型
     * @param response 响应对象
     */
    public static <T> void export(List<T> list, String fileName, String sheetName,
            Set<String> excludeColumnFieldNames,
            Class<T> clazz, HttpServletResponse response) {
        try {

            String exportFileName = "%s_%s.xlsx".formatted(fileName,
                    LocalDateTimeUtil.now().format(LocalDateTimeUtil.PURE_DATETIME_PATTERN));

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                    .filename(exportFileName, StandardCharsets.UTF_8).build().toString());
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");

            FastExcelFactory.write(response.getOutputStream(), clazz).autoCloseStream(false)
                    // 自动适配宽度
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    // 自动转换大数值
                    .registerConverter(new ExcelBigNumberConverter())
                    // 自动转换枚举
                    .registerConverter(new ExcelBaseEnumConverter())
                    // 自动转换 List
                    .registerConverter(new ExcelListConverter()).sheet(sheetName)
                    .excludeColumnFieldNames(excludeColumnFieldNames).doWrite(list);
        } catch (Exception e) {
            log.error("Export excel occurred an error: {}. fileName: {}.", e.getMessage(), fileName,
                    e);
            response.reset();
            throw new ServiceException("导出 Excel 出现错误");
        }
    }
}
