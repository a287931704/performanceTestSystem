package io.renren.modules.test.controller;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.annotation.SysLog;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.test.entity.StressTestSlaveEntity;
import io.renren.modules.test.service.StressTestSlaveService;
import io.renren.modules.test.utils.StressTestUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分布式节点管理
 */
@RestController
@RequestMapping("/test/stressSlave")
public class StressTestSlaveController {
    @Autowired
    private StressTestSlaveService stressTestSlaveService;

    @Autowired
    private StressTestUtils stressTestUtils;

    /**
     * 分布式节点列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("test:stress:slaveList")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(StressTestUtils.filterParms(params));
        List<StressTestSlaveEntity> stressTestList = stressTestSlaveService.queryList(query);
        int total = stressTestSlaveService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(stressTestList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 性能测试分布式节点信息
     */
    @RequestMapping("/info/{slaveId}")
    @RequiresPermissions("test:stress:slaveInfo")
    public R info(@PathVariable("slaveId") Long slaveId) {
        StressTestSlaveEntity stressTestSlave = stressTestSlaveService.queryObject(slaveId);

        return R.ok().put("stressTestSlave", stressTestSlave);
    }

    /**
     * 保存性能测试分布式节点
     */
    @SysLog("保存性能测试分布式节点信息")
    @RequestMapping("/save")
    @RequiresPermissions("test:stress:slaveSave")
    public R save(@RequestBody StressTestSlaveEntity stressTestSlave) {
        ValidatorUtils.validateEntity(stressTestSlave);

        stressTestSlaveService.save(stressTestSlave);

        return R.ok();
    }

    /**
     * 保存性能测试分布式节点
     */
    @SysLog("保存性能测试分布式节点信息")
    @RequestMapping("/autoGet")
    @RequiresPermissions("test:stress:autoGet")
    public R autoGet() {
        String result = StressTestUtils.doGet("http://bee-backend.baozun.com/external/outside/k8s/nodeinfo/?ProjectGroup=performance-team&AppServerName=jmeter-server&Env=sandbox");
        ArrayList new_slave = StressTestUtils.getNewSlave(result);
        for(Object slave_str: new_slave){
            StressTestSlaveEntity stressTestSlave = new StressTestSlaveEntity();
            stressTestSlave.setSlaveName("AutoGetPerformanceMachine");
            stressTestSlave.setJmeterPort("1099");
            stressTestSlave.setSshPort("22");
            stressTestSlave.setUserName("vmuser");
            stressTestSlave.setPasswd("vmuser@test");
            stressTestSlave.setHomeDir("/opt/project/apache-jmeter-4.0");
            stressTestSlave.setWeight("100");
            stressTestSlave.setIp(slave_str.toString());
            ValidatorUtils.validateEntity(stressTestSlave);
            stressTestSlaveService.save(stressTestSlave);
        }

        return R.ok();
    }

    /**
     * 修改性能测试分布式节点信息
     */
    @SysLog("修改性能测试分布式节点信息")
    @RequestMapping("/update")
    @RequiresPermissions("test:stress:slaveUpdate")
    public R update(@RequestBody StressTestSlaveEntity stressTestSlave) {
        ValidatorUtils.validateEntity(stressTestSlave);

        stressTestSlaveService.update(stressTestSlave);

        return R.ok();
    }

    /**
     * 删除性能测试分布式节点
     */
    @SysLog("删除性能测试分布式节点")
    @RequestMapping("/delete")
    @RequiresPermissions("test:stress:slaveDelete")
    public R delete(@RequestBody Long[] slaveIds) {
        stressTestSlaveService.deleteBatch(slaveIds);

        return R.ok();
    }


    /**
     * 切换性能测试分布式节点状态
     */
    @SysLog("切换性能测试分布式节点状态")
    @RequestMapping("/batchUpdateStatus")
    @RequiresPermissions("test:stress:slaveStatusUpdate")
    public R batchUpdateStatus(@RequestParam(value = "slaveIds[]") List<Long> slaveIds,
                               @RequestParam(value = "status") Integer status) {
        for (Long slaveId : slaveIds) {
            stressTestSlaveService.updateBatchStatus(slaveId, status);
        }
        return R.ok();
    }

    /**
     * 手工强制切换性能测试分布式节点状态
     */
    @SysLog("强制切换性能测试分布式节点状态")
    @RequestMapping("/batchUpdateStatusForce")
    @RequiresPermissions("test:stress:slaveStatusUpdateForce")
    public R batchUpdateStatusForce(@RequestParam(value = "slaveIds[]") List<Long> slaveIds,
                                    @RequestParam(value = "status") Integer status) {
        stressTestSlaveService.updateBatchStatusForce(slaveIds, status);
        return R.ok();
    }

    /**
     * 重启已经启动的性能测试分布式节点（停止状态的分布式节点不变）
     */
    @SysLog("重启已经启动的性能测试分布式节点")
    @RequestMapping("/batchRestart")
    @RequiresPermissions("test:stress:slaveRestart")
    public R batchRestart(@RequestParam(value = "slaveIds[]") List<Long> slaveIds) {

        for (Long slaveId : slaveIds) {
            stressTestSlaveService.restartSingle(slaveId);
        }
        return R.ok();
    }

    /**
     * 校准当前各节点状态（以前台页面状态为准，校准后台进程）
     */
    @SysLog("校准当前各节点状态")
    @RequestMapping("/batchReload")
    public R batchReload() {
        stressTestSlaveService.batchReloadStatus();
        return R.ok();
    }
}
