package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 用来封装前端提交过来的签到数据
 * @author yanghan
 * @date 2022/5/2
 */
@ApiModel
public class CheckinFormVO implements Serializable {
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
