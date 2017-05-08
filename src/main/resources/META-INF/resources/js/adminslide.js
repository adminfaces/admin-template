//slideoutjs integration
$(document).ready(function () {
    if (isMobile() && !$(document.body).hasClass("layout-top-nav")) {
        var slideout = new Slideout({
            'panel': document.getElementById('content'),
            'menu': document.getElementById('sidebar'),
            'padding': 230,
            'tolerance': 70
        });

        $("a[data-toggle='offcanvas']").on('click', function () {
            if ($("body").hasClass('sidebar-open')) {
                slideout.close();
                document.getElementById('sidebar').style.display = 'none';
                document.getElementById('content').style.transform = 'initial';

            } else {
                slideout.open();
                document.getElementById('sidebar').style.display = 'block';
                document.getElementById('content').style.transform = '230px';
            }
        });

        slideout.on('translatestart', function () {
            document.getElementById('sidebar').style.display = 'block';
            setBodyClass('sidebar-open');
        });


        slideout.on('close', function () {
            slideoutClose();
        });

        $(".content-wrapper").click(function () {
            if (!$("body").hasClass("sidebar-open") && document.getElementById('content').style.transform !== 'initial') {
                document.getElementById('content').style.transform = 'initial';
            }
        });

    }

    function slideoutClose() {
        document.getElementById('sidebar').style.display = 'none';
        removeBodyClass('sidebar-open');
    }

    function slideoutOpen() {
        document.getElementById('sidebar').style.display = 'block';
        removeBodyClass('sidebar-open');
    }


});
