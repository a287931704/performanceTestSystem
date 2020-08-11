$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'test/stressSlave/list',
        datatype: "json",
        colModel: [
            {label: '节点ID', name: 'slaveId', width: 30, key: true},
            {label: '名称', name: 'slaveName', width: 80, sortable: false},
            {label: '绑定用例', name: 'caseName', width: 80, sortable: false},
            {label: 'IP地址', name: 'ip', width: 50, sortable: false},
            {label: 'Jmeter端口', name: 'jmeterPort', width: 30, sortable: false},
            {label: '用户名', name: 'userName', width: 30, sortable: false},
            // {label: '密码', name: 'passwd', width: 50, sortable: false},
            {label: 'ssh端口', name: 'sshPort', width: 30, sortable: false},
            {
                label: '状态', name: 'status', width: 30, formatter: function (value, options, row) {
                if (value === 0) {
                    return '<span class="label label-default">禁用</span>';
                } else if (value === 1) {
                    return '<span class="label label-success">启用</span>';
                } else if (value === 2) {
                    return '<span class="label label-warning">进行中</span>';
                } else if (value === 3) {
                    return '<span class="label label-danger">异常</span>';
                }
            }
            },
            {label: '安装路径', name: 'homeDir', width: 100, sortable: false}
            // {label: '权重(%)', name: 'weight', width: 30, sortable: false}
        ],
        viewrecords: true,
        height: $(window).height() - 150,
        rowNum: 50,
        rowList: [10, 30, 50, 100, 200],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    //用于用例绑定slave下拉框显示的jqgrid
    $("#jqGrid1").jqGrid({
        url: baseURL + 'test/stress/list',
        datatype: "json",
        colModel: [
            {label: '用例ID', name: 'caseId', key: true},
            {label: '名称', name: 'caseName', sortable: false}
        ],
        viewrecords: true,
        // height: $(window).height() - 180,
        // width: 500,
        rowNum: 500,
        onSelectRow : function(id) {
            $("#customerName").val(id);
            $("#projectSelectDiv").hide();
            $("#customerName")[0].dispatchEvent(new Event('input'));
        },
        // rowList: [10, 30, 50, 100, 200],
        // rownumbers: true,
        // rownumWidth: 25,
        autowidth: true,
        multiselect: false,
        pager: "#jqGridPager1",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid1").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        q: {
            slaveName: null
        },
        showList: true,
        isClone: false,
        isOk: true,
        title: null,
        stressTestSlave: {}
    },
    methods: {
        query: function () {
            if (vm.q.slaveName != null) {
                vm.reload();
            }
        },
        abc: function(event) {
            alert(1);
        },
        add: function () {
            vm.showList = false;
            vm.isClone = false;
            vm.isOk = true;
            vm.title = "新增";
            vm.stressTestSlave = {
                status: 0,
                ip: "127.0.0.1",
                jmeterPort: 1099,
                sshPort: 22,
                homeDir: "/home/vmuser/jm/apache-jmeter-4.0",
                userName: "vmuser",
                weight: "100"
            };
        },
        autoGet: function () {
            var url = "test/stressSlave/autoGet";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                success: function (r) {
                    if (r.code === 0) {
                        // alert('操作成功', function(){
                        vm.reload();
                        // });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        update: function () {
            var slaveId = getSelectedRow();
            if (slaveId == null) {
                return;
            }

            $.get(baseURL + "test/stressSlave/info/" + slaveId, function (r) {
                vm.showList = false;
                vm.isClone = false;
                vm.isOk = true;
                vm.title = "修改";
                vm.stressTestSlave = r.stressTestSlave;
            });
        },
        clone: function () {
            var slaveId = getSelectedRow();
            if (slaveId == null) {
                return;
            }

            $.get(baseURL + "test/stressSlave/info/" + slaveId, function (r) {
                vm.showList = false;
                vm.isClone = true;
                vm.isOk = false;
                vm.title = "复制";
                vm.stressTestSlave = r.stressTestSlave;
            });
        },
        saveOrUpdate: function () {
            if (vm.validator()) {
                return;
            }

            var url = vm.stressTestSlave.slaveId == null ? "test/stressSlave/save" : "test/stressSlave/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.stressTestSlave),
                success: function (r) {
                    if (r.code === 0) {
                        // alert('操作成功', function(){
                        vm.reload();
                        // });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        cloneAndSave: function () {
            if (vm.validator()) {
                return;
            }

            var url = "test/stressSlave/save";
            vm.stressTestSlave.slaveId = null;
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.stressTestSlave),
                success: function (r) {
                    if (r.code === 0) {
                        // alert('操作成功', function(){
                        vm.reload();
                        // });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        del: function () {
            var slaveIds = getSelectedRows();
            if (slaveIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "test/stressSlave/delete",
                    contentType: "application/json",
                    data: JSON.stringify(slaveIds),
                    success: function (r) {
                        if (r.code == 0) {
                            alert('操作成功', function () {
                                vm.reload();
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        batchUpdateStatus: function (value) {
            var msgStr = "进行中...";
            if (0 == value) {
                msgStr = "正在禁用中...";
            } else {
                msgStr = "正在启用中...";
            }
            var slaveIds = getSelectedRows();
            if (slaveIds == null) {
                return;
            }

            $.ajax({
                type: "POST",
                url: baseURL + "test/stressSlave/batchUpdateStatus",
                // contentType: "application/json",
                // data: JSON.stringify(postData),
                data: {"slaveIds": slaveIds, "status": value},
                success: function (r) {
                    if (r.code == 0) {
                        alert(msgStr, function () {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        batchUpdateStatusForce: function (value) {
            var slaveIds = getSelectedRows();
            if (slaveIds == null) {
                return;
            }

            $.ajax({
                type: "POST",
                url: baseURL + "test/stressSlave/batchUpdateStatusForce",
                // contentType: "application/json",
                // data: JSON.stringify(postData),
                data: {"slaveIds": slaveIds, "status": value},
                success: function (r) {
                    if (r.code == 0) {
                        alert('启用成功，请自行启动节点服务并确认端口连通！', function () {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        batchRestart: function () {
            var slaveIds = getSelectedRows();
            if (slaveIds == null) {
                return;
            }

            $.ajax({
                type: "POST",
                url: baseURL + "test/stressSlave/batchRestart",
                // contentType: "application/json",
                // data: JSON.stringify(postData),
                data: {"slaveIds": slaveIds},
                success: function (r) {
                    if (r.code == 0) {
                        alert('开始执行', function () {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        batchReload: function () {
            $.ajax({
                type: "POST",
                url: baseURL + "test/stressSlave/batchReload",
                contentType: "application/json",
                data: "",
                success: function (r) {
                    if (r.code == 0) {
                        alert('校准完成，前后台状态同步一致！', function () {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'slaveName': vm.q.slaveName},
                page: page
            }).trigger("reloadGrid");
        },
        getConfirm: function (event) {
            // 控制下拉框显示
            var display =$('#projectSelectDiv');
            if(display.is(':hidden')){//如果node是隐藏的则显示node元素，否则隐藏
                display.show();
            }else{
                display.hide();
            }
        },

        validator: function () {
            if (isBlank(vm.stressTestSlave.slaveName)) {
                alert("节点名称不能为空");
                return true;
            }

            if (isBlank(vm.stressTestSlave.ip)) {
                alert("节点IP不能为空");
                return true;
            }

            if (isBlank(vm.stressTestSlave.jmeterPort)) {
                alert("节点Jmeter端口不能为空");
                return true;
            }

            if (isBlank(vm.stressTestSlave.homeDir)) {
                alert("节点Jmeter安装路径不能为空");
                return true;
            }

            if (!isValidIP(vm.stressTestSlave.ip)) {
                alert("IP格式不合法!");
                return true;
            }

            if (!isDigits(vm.stressTestSlave.jmeterPort)) {
                alert("节点Jmeter端口号不合法!");
                return true;
            }

            if (isBlank(vm.stressTestSlave.sshPort)) {
                alert("节点ssh端口不能为空");
                return true;
            }

            if (!isDigits(vm.stressTestSlave.sshPort)) {
                alert("节点ssh端口号不合法!");
                return true;
            }

            if (!isDigits(vm.stressTestSlave.weight)) {
                alert("权重输入不合法，请输入1-99999的整数!");
                return true;
            }
        }
    }
});