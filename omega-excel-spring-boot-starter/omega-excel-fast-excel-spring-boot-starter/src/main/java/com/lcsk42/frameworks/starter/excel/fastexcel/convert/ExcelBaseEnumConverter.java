package com.lcsk42.frameworks.starter.excel.fastexcel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import com.lcsk42.frameworks.starter.convention.enums.BaseEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * Excel 枚举接口转换器
 */
public class ExcelBaseEnumConverter implements Converter<BaseEnum<?>> {

    @SuppressWarnings("rawtypes")
    @Override
    public Class<BaseEnum> supportJavaTypeKey() {
        return BaseEnum.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 转换为 Java 数据（读取 Excel）
     */
    @Override
    public BaseEnum<?> convertToJavaData(ReadCellData<?> cellData,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        return BaseEnum.fromDescription(cellData.getStringValue(),
                contentProperty.getField().getType());
    }

    /**
     * 转换为 Excel 数据（写入 Excel）
     */
    @Override
    public WriteCellData<String> convertToExcelData(BaseEnum<?> value,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>(StringUtils.EMPTY);
        }
        return new WriteCellData<>(value.getDescription());
    }
}
