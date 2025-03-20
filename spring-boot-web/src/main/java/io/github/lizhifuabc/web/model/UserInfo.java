package io.github.lizhifuabc.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.lizhifuabc.web.security.SensitiveData;
import io.github.lizhifuabc.web.security.SensitiveDataSerializer;

/**
 * 用户信息实体类
 * 演示数据脱敏功能
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class UserInfo {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    @JsonSerialize(using = SensitiveDataSerializer.class)
    @SensitiveData(type = SensitiveData.SensitiveType.NAME)
    private String name;
    
    /**
     * 手机号
     */
    @JsonSerialize(using = SensitiveDataSerializer.class)
    @SensitiveData(type = SensitiveData.SensitiveType.MOBILE)
    private String mobile;
    
    /**
     * 邮箱
     */
    @JsonSerialize(using = SensitiveDataSerializer.class)
    @SensitiveData(type = SensitiveData.SensitiveType.EMAIL)
    private String email;
    
    /**
     * 身份证号
     */
    @JsonSerialize(using = SensitiveDataSerializer.class)
    @SensitiveData(type = SensitiveData.SensitiveType.ID_CARD)
    private String idCard;
    
    /**
     * 银行卡号
     */
    @JsonSerialize(using = SensitiveDataSerializer.class)
    @SensitiveData(type = SensitiveData.SensitiveType.BANK_CARD)
    private String bankCard;
    
    /**
     * 地址
     */
    @JsonSerialize(using = SensitiveDataSerializer.class)
    @SensitiveData(type = SensitiveData.SensitiveType.ADDRESS)
    private String address;

    // Getter 和 Setter 方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}