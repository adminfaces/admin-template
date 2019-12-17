/* Active menu management */

$(document).on("pfAjaxComplete", function () {
    activateSidebarComponent();
});

$(document).ready(function () {
    activateSidebarComponent();
    activateMenu(window.location.pathname, false);
    activateMobileBar();
});

$(window).on('resize', function () {
    adjustSidebarPosition();
});

function activateRippleIcons() {
    $(document.body).on('mousedown touchstart', '.ui-messages .ui-icon, .ui-growl-item .ui-icon, span.ui-tree-toggler, span.ui-treetable-toggler, div.ui-row-toggler, span.ui-icon-calendar, div.ui-selectcheckboxmenu-trigger span.ui-icon-triangle-1-s, span.ui-icon-circle-close, .ui-panel-titlebar span.ui-icon, .ui-dialog-titlebar span.ui-icon, .ui-paginator span.ui-icon, .ui-autocomplete-dropdown span.ui-icon-triangle-1-s, .ui-selectonemenu-trigger span.ui-icon-triangle-1-s, .ui-spinner-button .ui-icon', null, function (e) {
        $(this).addClass("icon-ripple");
    }).on('mouseup mouseleave touchend touchcancel', '.ui-messages .ui-icon, .ui-growl-item .ui-icon, span.ui-tree-toggler, span.ui-treetable-toggler, div.ui-row-toggler, span.ui-icon-calendar, div.ui-selectcheckboxmenu-trigger span.ui-icon-triangle-1-s, span.ui-icon-circle-close, .ui-panel-titlebar span.ui-icon, .ui-dialog-titlebar span.ui-icon, .ui-paginator span.ui-icon, .ui-autocomplete-dropdown span.ui-icon-triangle-1-s, .ui-selectonemenu-trigger span.ui-icon-triangle-1-s, .ui-spinner-button .ui-icon', null, function (e) {
        $(this).removeClass("icon-ripple");
    });

    $(document.body).on('mousedown touchstart', 'a.ui-rowgroup-toggler', null, function (e) {
        $(this).find('.ui-rowgroup-toggler-icon').addClass("icon-ripple");
    }).on('mouseup mouseleave touchend touchcancel', 'a.ui-rowgroup-toggler', null, function (e) {
        $(this).find('.ui-rowgroup-toggler-icon').removeClass("icon-ripple");
    });
}

function stripTrailingSlash(str) {
    if (str && str.substr(-1) == '/') {
        return str.substr(0, str.length - 1);
    }
    return str;
}

function saveCurrentActivatedUrl(url) {
    if (typeof (Storage) !== "undefined") {
        //console.log("saving :" + url);
        localStorage.setItem("activatedMenuUrl", url);
    }
}

/* set active style in menu based on current url */
function activateMenu(url, activated) {
    var activePage = stripTrailingSlash(url);
    $('.sidebar-menu li a, ul.navbar-nav li a').each(function () {
        var currentPage = stripTrailingSlash($(this).attr('href'));
        //console.log("activePage:" + activePage +" currentPage:" + currentPage);
        if (activePage == currentPage) {
            $(this).parent().addClass('active');
            if(isMobile() && $('#enableMobileHeader').length) {
               createMobileHeader($(this).html());
            }
            activated = true;
        } else {
            $(this).parent().removeClass('active');
        }
    });

    //submenus
    $('.sidebar-menu li ul a').each(function () {
        var currentPage = stripTrailingSlash($(this).attr('href'));
        //console.log("sub-activePage:" + activePage +" sub-currentPage:" + currentPage);
        if (activePage == currentPage) {
            $(this).parentsUntil( "ul.sidebar-menu", "li.treeview" ).each(function () {
                $(this).addClass('active');
            });
            activated = true;
        }
    });

    if (!isLayoutTop() && !activated && localStorage.getItem("activatedMenuUrl")) {
        //if not activated set latest activated url
        activateMenu(localStorage.getItem("activatedMenuUrl"), true);
    } else {
        saveCurrentActivatedUrl(url);
    }

}

function createMobileHeader(activeMenu) {
    $('.mobile-header > h4').html(activeMenu);
    if ($('.mobile-header > h4 span.label').length) {
        $('.mobile-header > h4 span.label').remove();
    }
    updateMobileHeaderVisibility();
}

function activateMobileBar() {
    if (isMobile()) {
        $('ul.sidebar-menu a.ui-link, ul.navbar-nav li a.ui-link:not(#layout-setup), ol.breadcrumb a').click(function () {
            $(this).prop('disabled', true);
            showBar();
        });
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

function searchLeftMenu(criteria) {
    $('#menu-search').show();
    $('#menu-search li.dropdown').addClass('open');
    var menuResults = $('#menu-search ul.dropdown-menu[role="menu"]');
    $('#menu-search li.dropdown li').remove();

    if (criteria != null && criteria.length >= 1) {
        criteria = criteria.toLowerCase();
        var match = false;
        $('ul.sidebar-menu.tree li:not(.treeview, .header)').each(function () {
            var linkText = $(this).find('a span');
            if (linkText && typeof linkText.html() !== 'undefined' && linkText.html().toLowerCase().indexOf(criteria) !== -1) {
                var menuItem = $(this).clone();
                menuItem.removeClass('active');
                menuResults.append("<li>").append(menuItem).append("</li>");
                match = true;
            }
        });
        //console.log($('#menu-search ul.dropdown-menu[role="menu"]').html());
        if (!match) {
            $('#menu-search li.dropdown').removeClass('open');
            $('#menu-search').hide();
        }
    } else {
        $('#menu-search li.dropdown').removeClass('open');
        $('#menu-search').hide();
    }
}


function searchTopMenu(criteria) {
    $('#menu-search').show();
    $('#menu-search li.dropdown').addClass('open');
    var menuResults = $('#menu-search ul.dropdown-menu[role="menu"]');
    $('#menu-search li.dropdown li').remove();

    if (criteria != null && criteria.length >= 1) {
        criteria = criteria.toLowerCase();
        var match = false;
        $('ul.nav.navbar-nav ul.dropdown-menu[role="menu"] > li').each(function () {
            var linkText = $(this).find('a span:not(.label)');
            if (linkText && linkText.html().toLowerCase().indexOf(criteria) !== -1) {
                menuResults.append("<li>").append($(this).clone()).append("</li>");
                match = true;
            }
        });
        if (!match) {
            $('#menu-search li.dropdown').removeClass('open');
        }
    } else {
        $('#menu-search li.dropdown').removeClass('open');
    }
}

function hideMenuResults() {
    setTimeout(function () {
        $('#menu-search li.dropdown').removeClass('open');
        $('#menu-search').hide();
    }, 150);
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
    if (!$(document.body).hasClass('sidebar-collapse') && !$(document.body).hasClass('layout-top-nav')) {
        $(document.body).addClass('sidebar-collapse')
    }
}

function expandSidebar() {
    if ($(document.body).hasClass('sidebar-collapse')) {
        $(document.body).removeClass('sidebar-collapse');
    }
}

function toggleSidebar() {
    if ($(document.body).hasClass('sidebar-collapse')) {
        $(document.body).removeClass('sidebar-collapse')
    } else if (!$(document.body).hasClass('layout-top-nav')) {
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

function setStaticNavbar() {
    var nav = $('.navbar');
    if (nav.hasClass('navbar-fixed-top')) {
        nav.removeClass('navbar-fixed-top');
        nav.addClass('navbar-static-top');
    }
}

function setFixedNavbar() {
    var nav = $('.navbar');
    if (nav.hasClass('navbar-static-top')) {
        nav.removeClass('navbar-static-top');
        nav.addClass('navbar-fixed-top');
    }
}

var scrollPosition = 0;
var scrollTimerNav, lastScrollFireTimeNav = 0;

function activateAutoShowNavbarOnScrollUp() {
    var nav = $('.navbar');
    if (isMobile() && window.pageYOffset > 150) {
    	if(nav.hasClass('navbar-static-top')) {
    		nav.css('display','none');
    	}
        updateMobileHeaderVisibility();
        setFixedNavbar();
        var currentScrollPositionNav = $(this).scrollTop();
        if (currentScrollPositionNav > (scrollPosition - 50)) {
            nav.fadeOut(500);
        } else {
            nav.fadeIn(500);
            adjustSidebarPosition();
        }
        scrollPosition = currentScrollPositionNav;
    } else {
        nav.show(400);
        setStaticNavbar();
        adjustSidebarPosition();
        updateMobileHeaderVisibility();
    }
}

function updateMobileHeaderVisibility() {
    if(isMobile() && window.pageYOffset > 150) {
        $('.mobile-header').css('display','block');
    } else {
        $('.mobile-header').css('display','none');
    }
}


function adjustSidebarPosition() {
    if (isMobile()) {
        var sidebar = $('#sidebar');
        if (window.pageYOffset > 150) {
            var sidebarOffset = window.pageYOffset - 100 + "px";
            sidebar.css("top", sidebarOffset);
            sidebar.css("z-index", 900);
        } else {
            sidebar.css("top", 0);
            sidebar.css("z-index", 1);
        }
    } else {
        $('#sidebar').css("top", 0);
        setStaticNavbar();
    }
}

$(document).ready(function () {
    setTimeout(function () {
        adminMaterial();
    }, 250);
    materialSwitch();
});

$(document).on("pfAjaxComplete", function () {
    setTimeout(function () {
        adminMaterial();
    }, 250);
    materialSwitch();
});

function adminMaterial() {
    materialInputs();
    materialCheckboxMenu();
    // every time an input is focused/blur verify if it has value if true then add 'focused' class on material div
    // when material div if focused then (material) label will float
    $('div.material input.ui-inputfield, div.material textarea.ui-inputtextarea').on('focus blur', function (e) {
        $(this).parents('div.material').toggleClass('focused', (e.type === 'focus' || this.value.length > 0));
    }).trigger('blur');

    $('div.ui-material input.ui-inputfield, div.ui-material textarea.ui-inputtextarea').on('focus blur', function (e) {
        $(this).parents('div.ui-material').toggleClass('focused', (e.type === 'focus' || this.value.length > 0));
    }).trigger('blur');

    //add focused class on material div each time a checkbox (from checkbox menu) is clicked
    //if there are items on checkboxmenu then it is considered focused and (material) label will be removed
    $(document).on('click', 'div.ui-selectcheckboxmenu-panel div.ui-chkbox', function (e) {
        materialCheckboxMenu();
    });

    //when checkbox menu is blur then decide if it 'focused' based on size of items
    $(document).on('blur', 'div.material div.ui-selectcheckboxmenu', function (e) {
        materialCheckboxMenu();
    });

    $(document).on('blur', 'div.ui-material div.ui-selectcheckboxmenu', function (e) {
        materialCheckboxMenu();
    });

    $(document).on("click", "div.material.icon-left i", function () {
        $(this).next().focus();
    });

    $(document).on("click", "div.ui-material.icon-left i", function () {
        $(this).next().focus();
    });

    $(document).on("click", "span.ui-calendar.ui-trigger-calendar,span.ui-autocomplete", function () {
        $(this).next().addClass('material-focus');
    });

    $(document).on('blur', "span.ui-calendar.ui-trigger-calendar, span.ui-autocomplete", function () {
        $(this).next().removeClass('material-focus');
    });

}


function materialCheckboxMenu() {
    $('div.material div.ui-selectcheckboxmenu').parents('div.material').toggleClass('focused', $('div.material div.ui-selectcheckboxmenu span.ui-selectcheckboxmenu-token-label').length > 0);
    $('div.ui-material div.ui-selectcheckboxmenu').parents('div.ui-material').toggleClass('focused', $('div.ui-material div.ui-selectcheckboxmenu span.ui-selectcheckboxmenu-token-label').length > 0);
}

function materialInputs() {
    $('div.material input.ui-inputfield, div.material textarea.ui-inputtextarea').each(function () {
        $(this).parents('div.material').toggleClass('focused', this.value.length > 0);
    });

    $('div.ui-material input.ui-inputfield, div.ui-material textarea.ui-inputtextarea').each(function () {
        $(this).parents('div.ui-material').toggleClass('focused', this.value.length > 0);
    });
}

function materialSwitch() {
    $('div.ui-inputswitch').each(function () {
        if ($('.ui-inputswitch-on.ui-state-active').width() > 0) {
            $(this).addClass('ui-inputswitch-active');
        } else {
            $(this).removeClass('ui-inputswitch-active');
        }
    });
}

$(document).on("click", "div.ui-inputswitch", function () {
    materialSwitch();
});


/**
 *
 * removes 'will-change' attribute of content wrapper when primefaces sidebar component is used
 * and user is on a mobele device (small screen)
 *
 *  This is needed because of conflict with slideoutjs
 */
function activateSidebarComponent() {
    if (isMobile()) {
        var content = $('#content');

        if (!content.hasClass('slideout-panel')) {
            return; //if slideout is not enabled then do nothing
        }

        if ($('div.ui-sidebar-active').length > 0) {
            content.css('will-change', 'unset');
            content.css('position', 'unset');
        } else {
            content.css('will-change', 'transform');
            content.css('position', 'relative');
        }
    }
}

/**
 * Get a prestored setting
 *
 * @param String name Name of of the setting
 * @returns String The value of the setting | null
 */
function get(name) {
    if (typeof (Storage) !== 'undefined') {
        return localStorage.getItem(name)
    } else {
        console.warn('LocalStorage not available for your browser. Layout customization will not work.')
    }
}

/**
 * Store a new settings in the browser
 *
 * @param String name Name of the setting
 * @param String val Value of the setting
 * @returns void
 */
function store(name, val) {
    if (typeof (Storage) !== 'undefined') {
        localStorage.setItem(name, val)
    } else {
        console.warn('LocalStorage not available for your browser. Layout customization will not work.')
    }
}

function isLayoutTop() {
    return $('body').hasClass('layout-top-nav');
}

//calendar locales, adding only languages supported in AdminFaces: https://adminfaces.github.io/site/docs/latest/#internationalization

PrimeFaces.locales['pt'] = {
    closeText : 'Fechar',
    prevText : 'Anterior ',
    nextText : 'Próximo ',
    currentText : 'Hoje ',
    monthNames : [ 'Janeiro ', 'Fevereiro ', 'Março ', 'Abril ', 'Maio ', 'Junho ', 'Julho ', 'Agosto ', 'Setembro ', 'Outubro ', 'Novembro ', 'Dezembro ' ],
    monthNamesShort : [ 'Jan ', 'Fev ', 'Mar ', 'Abr ', 'Mai ', 'Jun ', 'Jul ', 'Ago ', 'Set ', 'Out ', 'Nov ', 'Dez ' ],
    dayNames : [ 'Domingo ', 'Segunda ', 'Terça ', 'Quarta ', 'Quinta ', 'Sexta ', 'Sábado ' ],
    dayNamesShort : [ 'Dom ', 'Seg ', 'Ter ', 'Qua ', 'Qui ', 'Sex ', 'Sáb ' ],
    dayNamesMin : [ 'D', 'S', 'T', 'Q', 'Q', 'S', 'S' ],
    weekHeader : 'Semana ',
    firstDay : 0,
    isRTL : false,
    showMonthAfterYear : false,
    yearSuffix : '',
    timeOnlyTitle : 'Só Horas ',
    timeText : 'Tempo ',
    hourText : 'Hora ',
    minuteText : 'Minuto ',
    secondText : 'Segundo ',
    ampm : false,
    month : 'Mês ',
    week : 'Semana ',
    day : 'Dia ',
    allDayText : 'Todo o Dia '
};

PrimeFaces.locales['es'] = {
    closeText: 'Cerrar',
    prevText: 'Anterior',
    nextText: 'Siguiente',
    monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
    monthNamesShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
    dayNames: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
    dayNamesShort: ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'],
    dayNamesMin: ['D', 'L', 'M', 'X', 'J', 'V', 'S'],
    weekHeader: 'Semana',
    firstDayOfWeek: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Sólo hora',
    timeText: 'Tiempo',
    hourText: 'Hora',
    minuteText: 'Minuto',
    secondText: 'Segundo',
    currentText: 'Fecha actual',
    ampm: false,
    month: 'Mes',
    week: 'Semana',
    day: 'Día',
    allDayText: 'Todo el día'
};

PrimeFaces.locales ['ar'] = {
    closeText: 'إغلق',
    prevText: 'إلى الخلف',
    nextText: 'إلى الأمام',
    currentText: 'بداية',
    monthNames: ['ديسمبر', 'نوفمبر', 'أكتوبر', 'سبتمبر', 'أغسطس', 'يوليو', 'يونيو', 'مايو', 'ابريل', 'مارس', 'فبراير', 'يناير' ],
    monthNamesShort: ['ديسمبر', 'نوفمبر', 'أكتوبر', 'سبتمبر', 'أغسطس', 'يوليو', 'يونيو', 'مايو', 'ابريل', 'مارس', 'فبراير', 'يناير' ],
    dayNames: ['يوم الأحد‎', 'يوم الإثنين‎', 'يوم الثلاثاء‎', '‏يوم الأَرْبعاء‎', '‏يوم الخَمِيس‎', 'يوم الجُمْعَة‎‎', 'يوم السَّبْت'],
    dayNamesShort: ['الأحد‎', 'الإثنين‎', 'الثلاثاء‎', 'الأَرْبعاء‎', 'الخَمِيس‎', 'الجُمْعَة‎‎', 'السَّبْت'],
    dayNamesMin: ['الأحد‎', 'الإثنين‎', 'الثلاثاء‎', 'الأَرْبعاء‎', 'الخَمِيس‎', 'الجُمْعَة‎‎', 'السَّبْت'],
    weekHeader: 'الأسبوع',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix:'' ,
    timeOnlyTitle: 'الوقت فقط' ,
    timeText: 'الوقت' ,
    hourText: 'ساعة',
    minuteText: 'دقيقة',
    secondText: 'ثانية',
    ampm: false,
    month: 'الشهر',
    week: 'الأسبوع',
    day: 'اليوم',
    allDayText: 'سا ئراليوم'
};

PrimeFaces.locales['zh_CN'] = {
    closeText: '关闭',
    prevText: '上个月',
    nextText: '下个月',
    currentText: '今天',
    monthNames: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
    monthNamesShort: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
    dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
    dayNamesShort: ['日','一','二','三','四','五','六'],
    dayNamesMin: ['日','一','二','三','四','五','六'],
    weekHeader: '周',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: true,
    yearSuffix: '', // 年
    timeOnlyTitle: '仅时间',
    timeText: '时间',
    hourText: '时',
    minuteText: '分',
    secondText: '秒',
    ampm: false,
    month: '月',
    week: '周',
    day: '日',
    allDayText : '全天'
};

PrimeFaces.locales ['de'] = {
    closeText: 'Schließen',
    prevText: 'Zurück',
    nextText: 'Weiter',
    monthNames: ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember' ],
    monthNamesShort: ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez' ],
    dayNames: ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'],
    dayNamesShort: ['Son', 'Mon', 'Die', 'Mit', 'Don', 'Fre', 'Sam'],
    dayNamesMin: ['S', 'M', 'D', 'M ', 'D', 'F ', 'S'],
    weekHeader: 'Woche',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix:'',
    timeOnlyTitle: 'Nur Zeit',
    timeText: 'Zeit',
    hourText: 'Stunde',
    minuteText: 'Minute',
    secondText: 'Sekunde',
    currentText: 'Aktuelles Datum',
    ampm: false,
    month: 'Monat',
    week: 'Woche',
    day: 'Tag',
    allDayText: 'Ganzer Tag'
};