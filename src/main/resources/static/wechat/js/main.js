
$('body').on('click','[data-cmd="toggleModal"]',function(e){
    e.preventDefault();
    var ts = $(this) ;
    var target = ts.attr('href');
    $(target).toggle();
});

//设置轮播样式
var w = $(window).width();
var images = $('.banner-img a');
images.width(w);
$('.banner-img').prepend( images.clone() );

function resetBannerWidth(){
    var imgs = $('.banner-img a');
    $('.banner-img').width(w*imgs.length);
}

$(function () {
    resetBannerWidth() ;
    startTimer();
}) ;

// 绑定事件
var bannerDataCount = 0 ;
var bannerDataDistance = 0 ;
var timer = null ;
touch.on('.banner', 'swipeleft swiperight', function(ev){
    var type = ev.type ;
    var len = images.length ;
    if(type == 'swipeleft'){
        if(bannerDataCount == len - 1 ){
            bannerDataCount = 0 ;
            bannerDataDistance = 0 ;
        }else{
            bannerDataDistance += w;
            bannerDataCount  ++ ;
        }
    }else{
        if(bannerDataCount == 0){
            bannerDataCount = len - 1 ;
            bannerDataDistance = len * w ;
        }else{
            bannerDataDistance -= w ;
            bannerDataCount -- ;
        }
    }

    var realDis = - bannerDataDistance ;
    $('.banner-img').css({
        'transform':'translateX('+ realDis + 'px)',
        'transition':'.4s'
    });

    $('.dot').removeClass('active');
    $('.dot').eq(bannerDataCount).addClass('active');
});

function startTimer(){
    //轮播
    timer = setInterval(function(){
        if( bannerDataCount == images.length - 1){
            bannerDataDistance = 0 ;
            bannerDataCount = 0
        }else{
            bannerDataDistance += w ;
            bannerDataCount ++ ;
        }
        var realDis = - bannerDataDistance ;
        $('.banner-img').css({
            'transform':'translateX('+ realDis + 'px)',
            'transition':'.4s'
        });

        $('.dot').removeClass('active');
        $('.dot').eq(bannerDataCount).addClass('active');

    },5000);
}

$('.banner').on('mouseover',function(){
    clearInterval(timer);
});

$('.banner').on('mouseout',function(){
    startTimer();
});


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


//重置宽度
function startScroll() {
    var inner = $('.down-inner');
    var imgs = inner.find('.show');
    var len = imgs.length ;
    var baseW = imgs.eq(0).width();
    var ml = 20 ;

    if(w > 768){
        inner.width( len*320 - 20 );
    }else{
        ml = (w-30)*4/100 ;
        imgs.each(function (i) {
           if(i != 0){
               $(this).css('margin-left' , ml + 'px');
           }
           $(this).width(baseW);
        });
        inner.width( baseW * (len + ml) - ml );
    }

    var downCount = 0 ;
    var downDistance = 0 ;
    touch.on('.down-container', 'swipeleft swiperight', function(ev){
        var type = ev.type ;
        if(type == 'swipeleft'){
            if(downCount == len - 2 ){
                return false ;
            }else{
                downDistance += baseW + ml ;
                downCount  ++ ;
            }
        }else{
            if(downCount == 0){
                return false ;
            }else{
                downDistance -= baseW + ml ;
                downCount -- ;
            }
        }

        var realDis = - downDistance ;
        inner.css({
            'transform':'translateX('+ realDis + 'px)',
            'transition':'.4s'
        });
    });


    $('[data-cmd="scrollLeft"]').on('click',function(){
        if(downCount == len - 2 ){
            return false ;
        }else{
            downDistance += baseW + ml ;
            downCount  ++ ;
        }
        var realDis = - downDistance ;
        inner.css({
            'left':realDis + 'px',
            'transition':'.4s'
        });
    });

    $('[data-cmd="scrollRight"]').on('click',function(){
        if(downCount == 0){
            return false ;
        }else{
            downDistance -= baseW + ml ;
            downCount -- ;
        }
        var realDis = - downDistance ;
        inner.css({
            'left':realDis + 'px',
            'transition':'.4s'
        });
    });
}

$(function () {
    startScroll();
});


$('[data-cmd="showWrapper"]').on('click',function () {
    $('.wrapper').show();
});

$('[data-cmd="hidden"]').on('click',function () {
    $('[data-cmd="hidden"]').hidden();

});

$('[data-cmd="chooseLetter"] >.item').on('click',function () {
    var ts = $(this) ;
    ts.addClass("active").siblings().removeClass("active");
    $('[data-cmd="letterShow"]').show().fadeOut(5000);
    var test = ts.text();
    $('[data-cmd="letterShow"]').text(test);
});