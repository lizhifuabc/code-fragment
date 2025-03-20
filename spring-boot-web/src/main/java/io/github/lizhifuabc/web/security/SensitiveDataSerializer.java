package io.github.lizhifuabc.web.security;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;

/**
 * 敏感数据序列化器
 * 用于在JSON序列化时对敏感数据进行脱敏处理
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class SensitiveDataSerializer extends JsonSerializer<String> implements ContextualSerializer {
    
    private SensitiveData.SensitiveType type;

    public SensitiveDataSerializer() {
    }

    public SensitiveDataSerializer(SensitiveData.SensitiveType type) {
        this.type = type;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        
        switch (type) {
            case MOBILE:
                gen.writeString(SensitiveDataUtils.maskMobile(value));
                break;
            case BANK_CARD:
                gen.writeString(SensitiveDataUtils.maskBankCard(value));
                break;
            case ID_CARD:
                gen.writeString(SensitiveDataUtils.maskIdCard(value));
                break;
            case NAME:
                gen.writeString(SensitiveDataUtils.maskName(value));
                break;
            case EMAIL:
                gen.writeString(SensitiveDataUtils.maskEmail(value));
                break;
            case ADDRESS:
                gen.writeString(SensitiveDataUtils.maskAddress(value, 10));
                break;
            default:
                gen.writeString(value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property == null) {
            return prov.findNullValueSerializer(null);
        }
        
        SensitiveData sensitiveData = property.getAnnotation(SensitiveData.class);
        if (sensitiveData == null) {
            sensitiveData = property.getContextAnnotation(SensitiveData.class);
        }
        
        if (sensitiveData != null && property.getType().getRawClass() == String.class) {
            return new SensitiveDataSerializer(sensitiveData.type());
        }
        
        return prov.findValueSerializer(property.getType(), property);
    }
}