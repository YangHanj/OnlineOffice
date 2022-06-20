package iee.yh.onlineoffice.controller;

import iee.yh.onlineoffice.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yanghan
 * @date 2022/4/21
 */
@Api("测试Web接口")
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/sayHello")
    @ApiOperation("简单测试方法")
    public R testDemo(){
        return R.ok().put("message","Hello");
    }
}
