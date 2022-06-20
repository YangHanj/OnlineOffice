package iee.yh.onlineoffice.controller;

import iee.yh.onlineoffice.common.result.R;
import iee.yh.onlineoffice.common.task.MessageTask;
import iee.yh.onlineoffice.common.util.JwtUtils;
import iee.yh.onlineoffice.common.vo.DeleteMessageRefByIdFormVO;
import iee.yh.onlineoffice.common.vo.SearchMessageByIdFormVO;
import iee.yh.onlineoffice.common.vo.SearchMessageByPageFormVO;
import iee.yh.onlineoffice.common.vo.UpdateUnreadMessageFormVO;
import iee.yh.onlineoffice.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 *                                      消息模块设计思路
 *
 * MonogoDB记录表：1、Message 2、Message_Ref（两个表一对多，一个Message对应多个Message_ref）
 *      Message：该表用来记录所有发送的消息
 *      Message_ref：该表用来记录所有消息与接收者关系
 *                   该表中有两个重要字段：readFlag：用来标识该消息是否被阅读，默认为false
 *                                     LastFlag：用来表示该消息是否为新消息，默认为true
 * 当登陆小程序之后，vue的onshow函数发出ajax请求（/refreshMessage），获取当前用户有多少消息未读，以及有多个个新消息。
 * 进入index.vue页面之后，前端轮询发送（/refreshMessage），获取最新的消息状态。
 *
 * 用户可以点击消息通知界面，发送（/searchMessageByPage）来查看消息列表，该界面每页显示20个记录，采用倒序的方式
 *
 * 点击某一个单独的消息，跳转页面，调用onshow函数发送("/searchMessageById")查询该消息的详细记录，再次自动调用onload
 * 函数来标记该消息已被阅读（发送ajax：/updateUnreadMessage）
 *
 * 在消息详情页面也可以进行消息的删除，该删除是删除Message_ref的记录，因为一条消息可能对应多个接收者，所以不能删除
 * 源消息，只删除对应用户的接收记录即可。
 *
 * @author yanghan
 * @date 2022/5/8
 */
@RestController
@RequestMapping("/message")
@Api("消息模块接口")
public class MessageController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageTask messageTask;

    @PostMapping("/searchMessageByPage")
    @ApiOperation("分页查询消息")
    public R SearchMessageByPage(@Valid @RequestBody SearchMessageByPageFormVO searchMessageByPageFormVO,
                                 @RequestHeader("token") String token){
        //从token中解析userId
        int userId = jwtUtils.getUserId(token);
        //获取起始页码
        Integer page = searchMessageByPageFormVO.getPage();
        //获取每页记录个数
        Integer length = searchMessageByPageFormVO.getLength();
        //起始记录
        long start = (page - 1) * length;
        List<HashMap> list = messageService.searchMessageByPage(userId, start, length);
        return R.ok().put("result",list);
    }

    @PostMapping("/searchMessageById")
    @ApiOperation("根据消息Id查询消息")
    public R SearchMessageById(@Valid @RequestBody SearchMessageByIdFormVO searchMessageByIdFormVO){
        HashMap map = messageService.searchMessageById(searchMessageByIdFormVO.getId());
        return R.ok().put("result",map);
    }

    @PostMapping("/updateUnreadMessage")
    @ApiOperation("未读消息变已读")
    public R updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessageFormVO updateUnreadMessageFromVO){
        Long aLong = messageService.updateUnreadMessage(updateUnreadMessageFromVO.getId());
        return R.ok().put("result",aLong == 1 ? true : false);
    }

    @PostMapping("/deleteMessageRefById")
    @ApiOperation("删除消息")
    public R DeleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdFormVO deleteMessageRefByIdFormVO){
        Long aLong = messageService.deleteMessageRefById(deleteMessageRefByIdFormVO.getId());
        return R.ok().put("result",aLong == 1 ? true : false);
    }

    @GetMapping("/refreshMessage")
    @ApiOperation("刷新消息")
    public R refreshMessage(@RequestHeader("token") String token){
        int userId = jwtUtils.getUserId(token);
        //异步接收消息
        messageTask.receiveAsync(userId+"");
        //查询接收多少消息(LastFlag = true的消息)
        long lastRows = messageService.searchLastCount(userId);
        //查询未读数据(ReadFlag = false的消息)
        long unreadRows = messageService.searchUnreadCount(userId);
        return R.ok().put("lastRows",lastRows).put("unreadRows",unreadRows);
    }

}
