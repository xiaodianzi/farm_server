function queryMembers($) {
    var $table = $('#table_server');
    //先销毁表格
    $table.bootstrapTable('destroy');
    //再创建
    $table.bootstrapTable({
        url: $('#to_url').val(),
        method: 'get',
        dataType: "json",
        striped: true,
        undefinedText: "没有查询到任何的数据",
        pagination: true,
        paginationLoop:true,
        showToggle: "true",
        showColumns: "true",
        pageNumber: 1,
        showPaginationSwitch:true,
        pageSize: 10,
        pageList: [10, 20, 50, 100],
        paginationPreText: '&lt;&lt;',
        paginationNextText: '&gt;&gt;',
        search: true,
        data_local: "zh-US",
        sidePagination: "server",
        queryParams: function (params) {
            var keyword = $('#keyword').val();
            return {
                offset: params.offset,
                limit: params.limit,
                keyword: keyword
            };
        },
        responseHandler: function(res) {
            return {
                "total": res.total,
                "rows": res.rows
            };
        }
    });
}