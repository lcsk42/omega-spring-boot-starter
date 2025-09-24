package com.lcsk42.frameworks.starter.excel.fastexcel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Excel List 集合转换器
 *
 * <p>
 * 仅适合 List<基本类型> <=> xxx,xxx 转换
 * </p>
 */
@SuppressWarnings("rawtypes")
public class ExcelListConverter implements Converter<List> {

    @Override
    public Class supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public List convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        return List.of(StringUtils.split(stringValue, StringConstant.COMMA));
    }

    @Override
    public WriteCellData<Object> convertToExcelData(List value,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        WriteCellData<Object> writeCellData =
                new WriteCellData<>(StringUtils.join(value, StringConstant.COMMA));
        writeCellData.setType(CellDataTypeEnum.STRING);
        return writeCellData;
    }
}
