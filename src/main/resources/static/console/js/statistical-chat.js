!function ($) {
    $(document).on("click", "ul.nav li.parent > a > span.icon", function () {
        $(this).find('em:first').toggleClass("glyphicon-minus");
    });
    $(".sidebar span.icon").find('em:first').addClass("glyphicon-plus");
}(window.jQuery);

$(window).on('resize', function () {
    if ($(window).width() > 768) $('#sidebar-collapse').collapse('show')
})
$(window).on('resize', function () {
    if ($(window).width() <= 767) $('#sidebar-collapse').collapse('hide')
});

$(function () {
    showDefaultChat();
});

/**
 * 图标样式监听事件
 */
$('.option_select').on("change", function() {
    var selectValue = $('#chat_toggle').val();
    if (0 == selectValue) {
        $("#crops_bar").css("display","block");
        $("#crops_pie").css("display","none");
        $("#crops_line").css("display","none");
        $("#machinery_bar").css("display","block");
        $("#machinery_pie").css("display","none");
        $("#machinery_line").css("display","none");
        showDefaultChat();
    }
    if (1 == selectValue) {
        $("#crops_bar").css("display","none");
        $("#crops_pie").css("display","block");
        $("#crops_line").css("display","none");
        $("#machinery_bar").css("display","none");
        $("#machinery_pie").css("display","block");
        $("#machinery_line").css("display","none");
        showPieChat();
    }
    if (2 == selectValue) {
        $("#crops_bar").css("display","none");
        $("#crops_pie").css("display","none");
        $("#crops_line").css("display","block");
        $("#machinery_bar").css("display","none");
        $("#machinery_pie").css("display","none");
        $("#machinery_line").css("display","block");
        showLineChat();
    }
});

function getBarOption(titleText, names, values) {
    var barOption = {
        title: {
            text: titleText
        },
        //鼠标移入时显示提示信息
        tooltip: {},
        legend: {
            data:['订单量'],
            left: '55%',
            top: "1%"
        },
        xAxis: {
            // data: 数组类型
            data: names
        },
        yAxis: {},
        series: [{
            name: '订单量',
            type: 'bar',
            // data: 数组类型
            data: values
        }]
    };
    return barOption;
}

function showDefaultChat() {
    // 农作物分类订单统计分析图表
    var cropsOrders = $('#_cropsOrders').val();
    if (null != cropsOrders && "" != cropsOrders && undefined != cropsOrders) {
        // 基于准备好的dom，初始化echarts实例——柱状图
        var cropsBarChart = echarts.init(document.getElementById('crops_bar'));
        // 将json字符串转换成json对象
        var cropsOrdersJson = JSON.parse(cropsOrders);
        var nameArray = [];
        var numberArray = [];
        $.each(cropsOrdersJson, function(i) {
            nameArray.push(i);//json的key
            numberArray.push(cropsOrdersJson[i]);//json的value
        });
        // 根据图表的配置项和数据显示图标
        var cropsBarOption = getBarOption('农作物分类订单统计分析柱状图', nameArray, numberArray);
        cropsBarChart.setOption(cropsBarOption);
    }
    //农机分类订单分析图标
    var machineryOrder = $('#_machineryOrders').val();
    if (null != machineryOrder && "" != machineryOrder && undefined != machineryOrder) {
        var machineryBarChart = echarts.init(document.getElementById('machinery_bar'));
        var machineryOrderJson = JSON.parse(machineryOrder);
        var machineryOrderNames = [];
        var machineryOrderValues = [];
        $.each(machineryOrderJson, function(i) {
            machineryOrderNames.push(i);//json的key
            machineryOrderValues.push(machineryOrderJson[i]);//json的value
        });
        // 根据图表的配置项和数据显示图标
        var machineryBarOption = getBarOption('农机分类订单统计分析柱状图', machineryOrderNames, machineryOrderValues);
        machineryBarChart.setOption(machineryBarOption);
    }
};

function getPieOption(titleText, names, nameAndValue, _left, _top) {
    var pieOption = {
        title : {
            text: titleText,
            x: 'center'
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
        series : [
            {
                name: '访问来源',
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
    var cropsOrders = $('#_cropsOrders').val();
    if (null != cropsOrders && "" != cropsOrders && undefined != cropsOrders) {
        //初始化图标div
        var cropsPieChart = echarts.init(document.getElementById('crops_pie'));
        var cropsOrderJson = JSON.parse(cropsOrders);
        var cropsOrderNames = [];
        var cropsOrderArray = [];
        $.each(cropsOrderJson, function (i) {
            cropsOrderNames.push(i);//json的key
            var cropsOrderNameAndValue = {};
            cropsOrderNameAndValue.name = i;
            cropsOrderNameAndValue.value = cropsOrderJson[i];
            cropsOrderArray.push(cropsOrderNameAndValue);
        });
        var _left = '52%'; var _top = '50%';
        var option = getPieOption('农作物分类订单统计分析饼状图', cropsOrderNames, cropsOrderArray, _left, _top);
        cropsPieChart.setOption(option);
    }
    //农机分类订单统计饼状图
    var machineryOrder = $('#_machineryOrders').val();
    //农机分类订单列表不为空
    if (null != machineryOrder && "" != machineryOrder && undefined != machineryOrder) {
        //初始化图标div
        var machineryPieChart = echarts.init(document.getElementById('machinery_pie'));
        var machineryOrderJson = JSON.parse(machineryOrder);
        var machineryOrderNames = [];
        var machineryOrders = [];
        $.each(machineryOrderJson, function (i) {
            machineryOrderNames.push(i);//json的key
            var machineryOrderNameAndValue = {};
            machineryOrderNameAndValue.name = i;
            machineryOrderNameAndValue.value = machineryOrderJson[i];
            machineryOrders.push(machineryOrderNameAndValue);
        });
        var _left = '62%'; var _top = '50%';
        var option = getPieOption('农机分类订单统计分析饼状图', machineryOrderNames, machineryOrders, _left, _top);
        machineryPieChart.setOption(option);
    }
}

function getLineOption(titleText, names, values) {
    var lineOption = {
        title: {
            text: titleText
        },
        legend: {
            data:['订单量'],
            left: '55%',
            top: "1%"
        },
        tooltip : {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: names
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            name:'订单量',
            type:'line',
            data: values,
            areaStyle: {}
        }]
    };
    return lineOption;
}

function showLineChat() {
    // 农作物分类订单统计分析折线图
    var cropsOrders = $('#_cropsOrders').val();
    if (null != cropsOrders && "" != cropsOrders && undefined != cropsOrders) {
        // 基于准备好的dom，初始化echarts实例——柱状图
        var cropsLineChart = echarts.init(document.getElementById('crops_line'));
        // 将json字符串转换成json对象
        var cropsOrdersJson = JSON.parse(cropsOrders);
        var cropsOrderNameArray = [];
        var cropsOrderNumberArray = [];
        $.each(cropsOrdersJson, function (i) {
            cropsOrderNameArray.push(i);//json的key
            cropsOrderNumberArray.push(cropsOrdersJson[i]);//json的value
        });
        var option = getLineOption("农作物分类订单统计分析折线图", cropsOrderNameArray, cropsOrderNumberArray);
        cropsLineChart.setOption(option);
    }
    // 农机分类订单统计分析折线图
    var machineryOrders = $('#_machineryOrders').val();
    if (null != machineryOrders && "" != machineryOrders && undefined != machineryOrders) {
        // 基于准备好的dom，初始化echarts实例——柱状图
        var machineryLineChart = echarts.init(document.getElementById('machinery_line'));
        // 将json字符串转换成json对象
        var machineryOrdersJson = JSON.parse(machineryOrders);
        var machineryOrderNameArray = [];
        var machineryOrderNumberArray = [];
        $.each(machineryOrdersJson, function (i) {
            machineryOrderNameArray.push(i);//json的key
            machineryOrderNumberArray.push(machineryOrdersJson[i]);//json的value
        });
        var option = getLineOption("农机分类订单统计分析折线图", machineryOrderNameArray, machineryOrderNumberArray);
        machineryLineChart.setOption(option);
    }
};

$('.ranges_1 ul').remove();
$('#daterange-btn').daterangepicker({
        ranges: {
            '最近一年': [moment().subtract(364, 'days'), moment()],
            '最近半年': [moment().subtract(179, 'days'), moment()],
            '最近三个月': [moment().subtract(89, 'days'), moment()],
            '最近一个月': [moment().subtract(29, 'days'), moment()],
            '最近一周': [moment().subtract(6, 'days'), moment()]
        },
        startDate: moment(),
        endDate: moment()
    },
    function (start, end, label) {
        //label:通过它来知道用户选择的是什么，传给后台进行相应的展示
        if (label == '最近一年') {
            $('#daterange-btn span').html(start.format('YYYY/MM/DD') + '-' + end.format('YYYY/MM/DD'));
        } else if (label == '最近半年') {
            $('#daterange-btn span').html(start.format('YYYY/MM/DD') + '-' + end.format('YYYY/MM/DD'));
        } else if (label == '最近三个月') {
            $('#daterange-btn span').html(start.format('YYYY/MM/DD') + '-' + end.format('YYYY/MM/DD'));
        } else if (label == '最近一个月') {
            $('#daterange-btn span').html(start.format('YYYY/MM/DD') + '-' + end.format('YYYY/MM/DD'));
        } else if (label == '最近一周') {
            $('#daterange-btn span').html(start.format('YYYY/MM/DD') + '-' + end.format('YYYY/MM/DD'));
        }
    }
);