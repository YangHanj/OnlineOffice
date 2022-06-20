package iee.yh.onlineoffice.common.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yanghan
 * @date 2022/5/2
 */
@Data
public class HighListDTO {
        //类型
        private String type;
        //省份 or 直辖市
        private String province;
        //城市 or 区
        private String city;
        //区县 or 街道
        private String county;

        private String area_name;
        //社区
        private List<String> communitys ;

}
