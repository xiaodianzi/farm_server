var mapData = function statisticalData() {
    var jsonData;
    var ajaxUrl = "/plansolve/manger/web/statistical/query/region";
    $.ajax({
        url: ajaxUrl,
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            //   console.log('服务端mapDate='+data);
            jsonData = data;
        }
    });
    return jsonData;
};

var cropsData = function statisticalData() {
    var jsonData;
    var ajaxUrl = "/plansolve/manger/web/statistical/query/crops";
    $.ajax({
        url: ajaxUrl,
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            jsonData = data;
        }
    });
    return jsonData;
};

var machineryData = function statisticalData() {
    var jsonData;
    var ajaxUrl = "/plansolve/manger/web/statistical/query/machinery";
    $.ajax({
        url: ajaxUrl,
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            jsonData = data;
        }
    });
    return jsonData;
};

var userData = function statisticalData() {
    var jsonData;
    var ajaxUrl = "/plansolve/manger/web/statistical/query/users";
    $.ajax({
        url: ajaxUrl,
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            jsonData = data;
        }
    });
    return jsonData;
};

var ordersByCrop = function statisticalData(crop) {
    var jsonData;
    var ajaxUrl = "/plansolve/manger/web/statistical/query/crop/timeline";
    $.ajax({
        url: ajaxUrl,
        data: {"crop": crop},
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            //   console.log('服务端mapDate='+data);
            jsonData = data;
        }
    });
    return jsonData;
};

var ordersByMachinery = function statisticalData(machinery) {
    var jsonData;
    var ajaxUrl = "/plansolve/manger/web/statistical/query/machinery/timeline";
    $.ajax({
        url: ajaxUrl,
        data: {"machinery": machinery},
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            //   console.log('服务端mapDate='+data);
            jsonData = data;
        }
    });
    return jsonData;
};

var ordersByWorkmode = function statisticalData(workmode) {
    var jsonData;
    var ajaxUrl = "/plansolve/manger/web/statistical/query/workmode/timeline";
    $.ajax({
        url: ajaxUrl,
        data: {"workmode": workmode},
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            //   console.log('服务端mapDate='+data);
            jsonData = data;
        }
    });
    return jsonData;
};

var ordersByOrderstate = function statisticalData(orderstate) {
    var jsonData;
    var ajaxUrl = "/plansolve/manger/web/statistical/query/orderstate/timeline";
    $.ajax({
        url: ajaxUrl,
        data: {"orderstate": orderstate},
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            //   console.log('服务端mapDate='+data);
            jsonData = data;
        }
    });
    return jsonData;
};

$(function () {
    getJsonDataForMap();
    showPieChat();
    getUserDataForLineChart();
    //实时更新地图统计信息
    setInterval(function () {
        getJsonDataForMap();
    }, 600000);//1分钟
});

//获取区域订单统计信息数据
function getJsonDataForMap() {
    //map的dom元素
    var dom = document.getElementById("container");
    var myChart = echarts.init(dom, 'vintage');
    var regionData = mapData();
    var length = regionData.length - 1;
    var total = eval('regionData[' + length + '].totalNumber');
    var mapOption = {
        title: {
            text: '订单行政区域分布图',
            subtext: '订单量总计' + total,
            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: ['iphone3', 'iphone4', 'iphone5']
        },
        visualMap: {
            min: 0,
            max: 5000,
            left: 'left',
            top: 'bottom',
            text: ['高', '低'],        // 文本，默认为数值文本
            calculable: true
        },
        toolbox: {
            show: true,
            orient: 'vertical',
            left: 'right',
            top: 'center',
            feature: {
                mark: {show: true}
            }
        },
        series: [{
            name: '订单量',
            type: 'map',
            mapType: 'china',
            roam: false,
            data: regionData,
            label: {
                normal: {
                    show: false
                },
                emphasis: {
                    show: true
                }
            }
        }
        ]
    };
    if (mapOption && typeof mapOption === "object") {
        myChart.setOption(mapOption, true);
    }
}

function getPieOption(titleText, names, nameAndValue, _left, _top, total) {
    var pieOption = {
        title: {
            text: titleText,
            subtext: '订单量总计' + total,
            x: '25%'
        },
        //鼠标移入时显示提示信息
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            // data: ['直接访问','邮件营销','联盟广告','视频广告','搜索引擎','分享推荐']
            data: names
        },
        series: [
            {
                name: '订单量',
                type: 'pie',
                radius: '50%',
                center: [_left, _top],
                //data数据类型为json格式的数组
                data: nameAndValue
            }
        ]
    }
    return pieOption;
}

function showPieChat() {
    // 农作物分类订单统计分析图表
    var cropsOrders = cropsData();
    var length = cropsOrders.length - 1;
    var total = eval('cropsOrders[' + length + '].totalNumber');
    if (null != cropsOrders && "" != cropsOrders && undefined != cropsOrders) {
        //初始化图标div
        var cropsPieChart = echarts.init(document.getElementById('crops_pie'), 'vintage');
        //  var cropsOrderJson = JSON.parse(cropsOrders);
        var cropsOrderNames = [];
        var cropsOrderArray = [];
        $.each(cropsOrders, function (i) {
            cropsOrderNames.push(cropsOrders[i].name);//json的key
            var cropsOrderNameAndValue = {};
            cropsOrderNameAndValue.name = cropsOrders[i].name;
            cropsOrderNameAndValue.value = cropsOrders[i].value;
            cropsOrderArray.push(cropsOrderNameAndValue);
        });
        var _left = '38%';
        var _top = '55%';
        var option = getPieOption('订单农作物类型饼图', cropsOrderNames, cropsOrderArray, _left, _top, total);
        cropsPieChart.setOption(option);
    }
    //农机分类订单统计饼状图
    var machineryOrder = machineryData();
    var length = machineryOrder.length - 1;
    var total = eval('machineryOrder[' + length + '].totalNumber');
    //农机分类订单列表不为空
    if (null != machineryOrder && "" != machineryOrder && undefined != machineryOrder) {
        //初始化图标div
        var machineryPieChart = echarts.init(document.getElementById('machinery_pie'));
        var machineryOrderNames = [];
        var machineryOrders = [];
        $.each(machineryOrder, function (i) {
            machineryOrderNames.push(machineryOrder[i].name);//json的key
            var machineryOrderNameAndValue = {};
            machineryOrderNameAndValue.name = machineryOrder[i].name;
            machineryOrderNameAndValue.value = machineryOrder[i].value;
            machineryOrders.push(machineryOrderNameAndValue);
        });
        var _left = '38%';
        var _top = '55%';
        var option = getPieOption('订单农机类型饼图', machineryOrderNames, machineryOrders, _left, _top, total);
        machineryPieChart.setOption(option);
    }
}

function showTimeLineChat(responseUserData, text, subtext, name) {
    //用户折线图的dom元素
    var dom = document.getElementById("user_line");
    var myChart = echarts.init(dom, 'vintage');
    var base = +new Date('2017-12-31');
    var oneDay = 24 * 3600 * 1000;
    var date = [];
    var data = [];
    var total = eval('responseUserData.totalNumber');
    //计算指定日期距今的天数
    var datePoint = new Date('2018-01-01'); //开始时间
    var nowDate = new Date(); //结束时间
    var dateTime = nowDate.getTime() - datePoint.getTime(); //时间差的毫秒数
    var days = Math.ceil(dateTime / (24 * 3600 * 1000)) + 1;
    for (var i = 1; i < days; i++) {
        var now = new Date(base += oneDay);
        var dateStr = [now.getFullYear(), now.getMonth() + 1, now.getDate()].join('-');
        date.push(dateStr);
        var validData = responseUserData['' + dateStr + ''];
        if (validData) {
            data.push(validData);
        } else {
            data.push(0);
        }
    }
    var timeLineOption = {
        tooltip: {
            trigger: 'axis',
            position: function (pt) {
                return [pt[0], '10%'];
            }
        },
        title: {
            left: 'center',
            text: text,
            subtext: subtext
        },
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: date
        },
        yAxis: {
            type: 'value',
            boundaryGap: [0, '100%']
        },
        dataZoom: [{
            type: 'inside',
            start: 0,
            end: 10
        }, {
            start: 0,
            end: 10,
            handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
            handleSize: '80%',
            handleStyle: {
                color: '#fff',
                shadowBlur: 3,
                shadowColor: 'rgba(0, 0, 0, 0.6)',
                shadowOffsetX: 2,
                shadowOffsetY: 2
            }
        }],
        series: [
            {
                name: name,
                type: 'line',
                smooth: true,
                symbol: 'none',
                sampling: 'average',
                itemStyle: {
                    color: 'rgb(255, 70, 131)'
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                        offset: 0,
                        color: 'rgb(255, 158, 68)'
                    }, {
                        offset: 1,
                        color: 'rgb(255, 70, 131)'
                    }])
                },
                data: data
            }
        ]
    };
    if (timeLineOption && typeof timeLineOption === "object") {
        myChart.setOption(timeLineOption, true);
    }
}

//获取用户量的统计信息
function getUserDataForLineChart() {
    var responseUserData = userData();
    var total = eval('responseUserData.totalNumber');
    var text = "平台用户量时间轴分布图";
    var subtext = '用户总计' + total + '人';
    var name = "新增用户";
    showTimeLineChat(responseUserData, text, subtext, name);
}

//农作物类型统计时间轴分布图
$('#_crop').on("change", function () {
    var selectValue = $('#_crop').val();
    var title = $('#_crop').find("option:selected").text();
    if (0 != selectValue) {
        var responseOrdersData = ordersByCrop(selectValue);
        var total = eval('responseOrdersData.totalNumber');
        var text = title + "订单量时间轴分布图";
        var subtext = title + '订单量总计' + total + '单';
        var name = "新增" + title + "订单";
        showTimeLineChat(responseOrdersData, text, subtext, name);
    }
});

//农机类型统计时间轴分布图
$('#_machinery').on("change", function () {
    var selectValue = $('#_machinery').val();
    var title = $('#_machinery').find("option:selected").text();
    if (0 != selectValue) {
        var responseOrdersData = ordersByMachinery(selectValue);
        var total = eval('responseOrdersData.totalNumber');
        var text = title + "订单量时间轴分布图";
        var subtext = title + '订单量总计' + total + '单';
        var name = "新增" + title + "订单";
        showTimeLineChat(responseOrdersData, text, subtext, name);
    }
});

//作业方式统计时间轴分布图
$('#_workmode').on("change", function () {
    var selectValue = $('#_workmode').val();
    var title = $('#_workmode').find("option:selected").text();
    if (0 != selectValue) {
        var responseOrdersData = ordersByWorkmode(selectValue);
        var total = eval('responseOrdersData.totalNumber');
        var text = title + "订单量时间轴分布图";
        var subtext = title + '订单量总计' + total + '单';
        var name = "新增" + title + "订单";
        showTimeLineChat(responseOrdersData, text, subtext, name);
    }
});

//订单状态统计时间轴分布图
$('#_orderstate').on("change", function () {
    var selectValue = $('#_orderstate').val();
    var title = $('#_orderstate').find("option:selected").text();
    if (0 != selectValue) {
        var responseOrdersData = ordersByOrderstate(selectValue);
        var total = eval('responseOrdersData.totalNumber');
        var text = title + "订单量时间轴分布图";
        var subtext = title + '订单量总计' + total + '单';
        var name = "新增" + title + "订单";
        showTimeLineChat(responseOrdersData, text, subtext, name);
    }
});

//点击恢复平台用户量统计时间轴分布图
$('#user_line').on("dblclick", function () {
    getUserDataForLineChart();
});

