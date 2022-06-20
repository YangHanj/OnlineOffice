package iee.yh.onlineoffice.common.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yanghan
 * @date 2022/5/2
 */
@Data
public class MiddleListDTO {

        private String type;

        private String province;

        private String city;

        private String county;

        private String area_name;

        private List<String> communitys ;
}
