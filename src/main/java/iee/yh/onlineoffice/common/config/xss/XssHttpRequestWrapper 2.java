package iee.yh.onlineoffice.common.config.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yanghan
 * @date 2022/4/21
 */
public class XssHttpRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        if (!StrUtil.hasEmpty(parameter))
            parameter = HtmlUtil.filter(parameter);
        return parameter;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        if (parameterValues != null && parameterValues.length > 0){
            for (int i = 0; i < parameterValues.length; i++) {
                String parameter = parameterValues[i];
                if (!StringUtils.isEmpty(parameter))
                    parameter = HtmlUtil.filter(parameter);
                parameterValues[i] = parameter;
            }
        }
        return parameterValues;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        Map<String, String[]> map = new LinkedHashMap<>();
        if (parameterMap != null && parameterMap.size() > 0){
            for (String key : parameterMap.keySet()) {
                String[] strings = parameterMap.get(key);
                for (int i = 0; i < strings.length; i++) {
                    String parameter = strings[i];
                    if (!StringUtils.isEmpty(parameter))
                        parameter = HtmlUtil.filter(parameter);
                    strings[i] = parameter;
                }
                map.put(key,strings);
            }
        }
        return map;
    }

    @Override
    public String getHeader(String name) {
        String header = super.getHeader(name);
        if (StrUtil.hasEmpty(header)){
            header = HtmlUtil.filter(name);
        }
        return header;
    }
}