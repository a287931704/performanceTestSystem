package io.renren.modules.app.controller;


import com.feilong.core.net.ParamUtil;
import com.feilong.security.oneway.MD5Util;
import io.renren.common.utils.R;
import io.renren.modules.app.annotation.Login;
import io.renren.modules.app.annotation.LoginUser;
import io.renren.modules.app.entity.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.renren.modules.test.utils.StressTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * APP测试接口
 *
 * @author smooth
 * @email 287931704@qq.com
 * @date 2017-03-23 15:47
 */
@RestController
@RequestMapping("/app")
@Api("APP测试接口")
public class ApiTestController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Login
    @GetMapping("userInfo")
    @ApiOperation("获取用户信息")
    public R userInfo(@LoginUser UserEntity user){
        return R.ok().put("user", user);
    }

    @Login
    @GetMapping("userId")
    @ApiOperation("获取用户ID")
    public R userInfo(@RequestAttribute("userId") Integer userId){
        return R.ok().put("userId", userId);
    }

    @GetMapping("notToken")
    @ApiOperation("忽略Token验证测试")
    public R notToken(){
        return R.ok().put("msg", "无需token也能访问。。。");
    }

    @GetMapping("getToken")
    @ApiOperation("返回接口所需token")
    public String getTokenInfo(){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("timestamp", String.valueOf(Calendar.getInstance().getTimeInMillis()));
        paramMap.put("noncestr", "mWnkNhr0KFjfSU4ujFKNekbYyPyeBeE7");
        String prestr = ParamUtil.toNaturalOrderingQueryString(paramMap);
        String mysign = MD5Util.encode(prestr + "zjXMTKXzZOxyKOmwX794yu0DnoS4J42Y", StandardCharsets.UTF_8.name());
        paramMap.put("sign", mysign);
        JSONObject jsonObj = new JSONObject(paramMap);
        return jsonObj.toString();
    }

    @Autowired
    private StressTestUtils stressTestUtils;

    @GetMapping("execpython/{timeline}")
    @ApiOperation("返回jira中BUG季度报告数据")
    public Object execPython(@PathVariable("timeline") String timeline) {
        logger.debug("----------------------------" + timeline);
        String filePath = stressTestUtils.getScriptExecuteFileHomeKey();
        File file = new File(filePath + File.separator + "jiraExport_Summary.py");
        logger.debug(file.getAbsolutePath());
        if (file.exists()) {
            System.out.println("可以读取到非项目中脚本");
        } else {
            System.out.println("不可以读取到非项目中脚本");
            return "不可以读取到非项目中脚本";
        }
//        List<String> strList = new ArrayList<String>();
        StringBuffer sBuffer = new StringBuffer();
        try {
            Runtime runTime = Runtime.getRuntime();

            Process process = runTime.exec("python " + filePath + File.separator + "jiraExport_Summary.py" + " " + timeline);
//            Process process = runTime.exec("python3 /service/script/test.py");
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            InputStreamReader ir1 = new InputStreamReader(process.getErrorStream());
            LineNumberReader input = new LineNumberReader(ir);
            LineNumberReader input1 = new LineNumberReader(ir1);
            String line;
            process.waitFor();
            while ((line = input.readLine()) != null) {
                sBuffer.append(line);
                sBuffer.append("\r\n");
            }
            while ((line = input1.readLine()) != null) {
                sBuffer.append(line);
                sBuffer.append("\r\n");
            }
            System.out.println("启动脚本");
            sBuffer.append("脚本执行完成");
        } catch (Exception e) {
            e.printStackTrace();
            return "执行python脚本失败";
        }

        return sBuffer;
    }
}
