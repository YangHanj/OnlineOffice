package iee.yh.onlineoffice.common.constant;

import iee.yh.onlineoffice.common.dto.HighListDTO;
import iee.yh.onlineoffice.common.dto.MiddleListDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用该类来封装当天全国疫情的中高风险地区
 * @author yanghan
 * @date 2022/5/2
 */
@Component
@Data
public class COVID19_City {
    private List<HighListDTO> highlist;
    private List<MiddleListDTO> Middlelist;
}
