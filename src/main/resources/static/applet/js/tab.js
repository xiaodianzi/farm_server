/*tab切换*/
$('[data-cmd="switchTab"] >.item').on('click',function(){
    var ts = $(this) ;
    ts.siblings().removeClass('active');
    ts.addClass('active');
    var index = ts.index();
    var content = ts.parents('.tab-header').next().find('.item');
    content.removeClass('active');
    content.eq(index).addClass('active');
});