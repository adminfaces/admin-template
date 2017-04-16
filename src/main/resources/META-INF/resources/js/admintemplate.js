/* hide info,warn messages */
$(document).on("pfAjaxComplete", function () {
    var $messages = $("div[id$='info-messages']");

    if ($messages.length) {
        var wordCount = $messages.text().split(/\W/).length;
        var readingTimeMillis = 2500 + (wordCount * 200);

        setTimeout(function () {
            $messages.slideUp();
        }, readingTimeMillis);
    }
});

$(document).ready(function () {
    var $messages = $("div[id$='info-messages']");

    if ($messages.length) {
        var wordCount = $messages.text().split(/\W/).length;
        var readingTimeMillis = 2500 + (wordCount * 200);

        setTimeout(function () {
            $messages.slideUp();
        }, readingTimeMillis);
    }
});

/* Active menu management */

$(document).ready(function () {
    activateMenu(window.location.pathname, false);
});

function stripTrailingSlash(str) {
    if (str && str.substr(-1) == '/') {
        return str.substr(0, str.length - 1);
    }
    return str;
}

function saveCurrentActivatedUrl(url) {
    if (typeof(Storage) !== "undefined") {
        //console.log("saving :" + url);
        localStorage.setItem("activatedMenuUrl", url);
    }

}

/* set active style in menu based on current url */
function activateMenu(url, activated) {
    var activePage = stripTrailingSlash(url);
    $('.sidebar-menu li a').each(function () {
        var currentPage = stripTrailingSlash($(this).attr('href'));
        //console.log("activePage:" + activePage +" currentPage:" + currentPage);
        if (activePage == currentPage) {
            $(this).parent().addClass('active');
            activated = true;
        }
    });

    //submenus
    $('.sidebar-menu li ul a').each(function () {
        var currentPage = stripTrailingSlash($(this).attr('href'));
        //console.log("sub-activePage:" + activePage +" sub-currentPage:" + currentPage);
        if (activePage == currentPage) {
            $(this).parent().addClass('active');
            $(this).parent().parent().parent().addClass('active');
            activated = true;
        }
    });

    if (!activated && localStorage.getItem("activatedMenuUrl")) {
        //if not activated set latest activated url
        activateMenu(localStorage.getItem("activatedMenuUrl"), true);
    } else {
        saveCurrentActivatedUrl(url);
    }

}

function searchMenu(criteria) {
    if (criteria != null && criteria.length >= 2) {
        criteria = criteria.toLowerCase();
        $('.sidebar-menu li.treeview').each(function () {
            var childMatch = false;
            $(this).find("ul li span").each(function () {
                var childText = $(this).html();
                if (childText.toLowerCase().indexOf(criteria) == -1) {
                    $(this).parent().hide();
                } else {
                    childMatch = true;
                    $(this).parent().show();
                    $(this).addClass('active');
                }
            });
            if (childMatch) {
                $(this).addClass('active');
            } else {
                $(this).removeClass('active');
            }
        });//end treeview search

        $('.sidebar-menu li').each(function () {
            $(this).not(".treeview").each(function () {
                var elementText = $(this).html();
                if (elementText.toLowerCase().indexOf(criteria) == -1) {
                    $(this).hide();
                } else {
                    $(this).show();
                }
            });

        });

    } else {
        $('.sidebar-menu li').each(function () {
            if ($(this)) {
                $(this).show();
                if ($(this).hasClass('active')) {
                    $(this).removeClass('active');
                }

                if ($(this).hasClass('menu-open')) {
                    $(this).removeClass('menu-open');
                }

                if ($(this).parent()) {
                    $(this).parent().show();
                }
                $(this).children().each(function () {
                    $(this).show();
                    if ($(this).hasClass('active')) {
                        $(this).removeClass('active');
                    }

                    if ($(this).hasClass('menu-open')) {
                        $(this).removeClass('menu-open');
                    }


                });
            }

        })
    }
}

function setBodyClass(clazz) {
    if (!$(document.body).hasClass(clazz)) {
        $(document.body).addClass(clazz)
    }
}

function removeBodyClass(clazz) {
    if ($(document.body).hasClass(clazz)) {
        $(document.body).removeClass(clazz)
    }
}


function collapseSidebar() {
    if (!$(document.body).hasClass('sidebar-collapse')) {
        $(document.body).addClass('sidebar-collapse')
    }
}

function expandSidebar() {
    if ($(document.body).hasClass('sidebar-collapse')) {
        $(document.body).removeClass('sidebar-collapse')
    }
}

function toggleSidebar() {
    if ($(document.body).hasClass('sidebar-collapse')) {
        $(document.body).removeClass('sidebar-collapse')
    } else {
        $(document.body).addClass('sidebar-collapse')
    }
}

function toggleSidebarMini() {
    if ($(document.body).hasClass('sidebar-mini')) {
        $(document.body).removeClass('sidebar-mini')
    } else {
        $(document.body).addClass('sidebar-mini')
    }
}

function setSidebarMini() {
    if (!$(document.body).hasClass('sidebar-mini')) {
        $(document.body).addClass('sidebar-mini')
    }
}

function removeSidebarMini() {
    if ($(document.body).hasClass('sidebar-mini')) {
        $(document.body).removeClass('sidebar-mini')
    }
}

function showBar() {
    if (isMobile()) {
        document.getElementById('loader').style.display = 'inline';
    } else {
        PF('statusDialog').show();
    }
}

function hideBar() {
    if (isMobile()) {
        document.getElementById('loader').style.display = 'none';
    } else {
        PF('statusDialog').hide();
    }
}

function isMobile() {
    var mq = window.matchMedia("(max-width: 767px)");
    return (typeof mq != 'undefined' && mq.matches);
}


function activateScrollToTop() {
    if (isMobile() && window.pageYOffset > 400) {
        $('#scrollTop').show(500);
    } else {
        $('#scrollTop').hide(500);
    }
}


//slideoutjs integration
$(document).ready(function () {
    if (isMobile()) {
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

