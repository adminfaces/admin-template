//slideoutjs integration
$(document).ready(function () {
    initSlideout();
});

$(window).on('resize', function () {
    initSlideout();
});


function initSlideout() {
    $("a.sidebar-toggle").on('click', function () {
        setTimeout(function () {
            if (!isMobile() && document.getElementById('sidebar')) {
                document.getElementById('content').style.transform = 'initial';
                document.getElementById('sidebar').style.display = 'block';
            }
        }, 30);
    });

    if (isMobile() && !$(document.body).hasClass("layout-top-nav") && document.getElementById('sidebar')) {
        var sidebar = $('#sidebar');
        var slideout = new Slideout({
            'panel': document.getElementById('content'),
            'menu': document.getElementById('sidebar'),
            'padding': 230,
            'tolerance': 70
        });

        $("a[data-toggle='push-menu']").on('click', function () {
            if ($("body").hasClass('sidebar-open')) {
                slideout.close();
                document.getElementById('sidebar').style.display = 'none';
                document.getElementById('content').style.transform = 'initial';

            } else {
                adjustSidebarPosition();
                slideout.open();
                document.getElementById('sidebar').style.display = 'block';
                document.getElementById('content').style.transform = '230px';
            }
        });

        slideout.on('translatestart', function () {
            setBodyClass('sidebar-open');
            sidebar.show(500);
        });


        slideout.on('translateend', function () {
            adjustSidebarPosition();
        });

        slideout.on('close', function () {
            slideoutClose();
        });

        slideout.on('beforeclose', function () {
            document.getElementById('sidebar').style.display = 'none';
        });

        $(".content-wrapper").click(function () {
            if (!$("body").hasClass("sidebar-open") && document.getElementById('content').style.transform !== 'initial') {
                document.getElementById('content').style.transform = 'initial';
                document.getElementById('sidebar').style.display = 'none';
                initSlideout();
            }
        });
    }
    else if (document.getElementById('sidebar')){
        document.getElementById('content').style.transform = 'initial';
        document.getElementById('sidebar').style.display = 'block';
    }
}

function slideoutClose() {
    removeBodyClass('sidebar-open');
}

function slideoutOpen() {
    var sidebar = $('#sidebar');
    sidebar.show(500);
    removeBodyClass('sidebar-open');
}
