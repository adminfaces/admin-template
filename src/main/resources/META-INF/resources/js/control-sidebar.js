$(function () {

    'use strict'

    /**
     * Get access to plugins
     */

    $('[data-toggle="control-sidebar"]').controlSidebar()
    $('[data-toggle="push-menu"]').pushMenu()


    var $pushMenu = $('[data-toggle="push-menu"]').data('lte.pushmenu')
    var $controlSidebar = $('[data-toggle="control-sidebar"]').data('lte.controlsidebar')


    function changeLayout(cls) {
        $('body').toggleClass(cls)
        $controlSidebar.fix()
    }

    function updateSidebarSkin(sidebarSkin) {
        var $sidebar = $('.control-sidebar');
        var sidebarSkinCkbox = $('#sidebar-skin span.ui-chkbox-icon');
        if (sidebarSkin === 'control-sidebar-light') {
            $sidebar.removeClass('control-sidebar-dark');
            sidebarSkinCkbox.addClass('ui-icon-blank');
            sidebarSkinCkbox.removeClass('ui-icon-check');
            sidebarSkinCkbox.parent().removeClass('ui-state-active');
        } else {
            $sidebar.removeClass('control-sidebar-light');
            sidebarSkinCkbox.addClass('ui-icon-check');
            sidebarSkinCkbox.removeClass('ui-icon-blank');
            sidebarSkinCkbox.parent().addClass('ui-state-active');
        }

        $sidebar.addClass(sidebarSkin);

        store('layout.sidebar-skin', sidebarSkin);
    }

    function updateBoxedLayout(boxedLayout) {
        if (isMobile()) { //boxed layout is not compatible with mobile screens neither fixed layout
            disableControlSidebarOption('#boxed-layout');
            store('layout.boxed', false);
        } 

        if (boxedLayout === true || boxedLayout === 'true') {
            if (!$('body').hasClass('layout-boxed')) {
                $('body').addClass('layout-boxed');
            }
            if (!PF('boxedLayout').input.is(':checked')) {//it will not be checked when the value comes from browser local storage 
                PF('boxedLayout').toggle();
            }
            
            enableControlSidebarOption('#boxed-layout');
            disableControlSidebarOption('#fixed-layout');
        } else {
            enableControlSidebarOption('#fixed-layout');
            if ($('body').hasClass('layout-boxed')) {
                $('body').removeClass('layout-boxed');
            }
            if (PF('boxedLayout').input.is(':checked')) {//update input when value comes from local storage
                PF('boxedLayout').toggle();
            }
        }
        store('layout.boxed', boxedLayout);

    }

    function updateFixedLayout(fixedLayout) {
        if (isMobile()) { //fixed layout not compatible with small screens (admin-template already has behaviour for navbar when on mobile) neither boxed layout
            disableControlSidebarOption('#fixed-layout');
            store('layout.fixed', false);
            return;
        }  
        if (fixedLayout === true || fixedLayout === 'true') {
            if (!$('body').hasClass('fixed')) {
                $('body').addClass('fixed');
            }
            if (!PF('fixedLayout').input.is(':checked')) {//it will not be checked when the value comes from browser local storage 
                PF('fixedLayout').toggle();
            }
            enableControlSidebarOption('#fixed-layout');
            disableControlSidebarOption('#boxed-layout');
        } else {
            enableControlSidebarOption('#boxed-layout');
            if ($('body').hasClass('fixed')) {
                $('body').removeClass('fixed');
            }
            if (PF('fixedLayout').input.is(':checked')) {//update input when value comes from local storage
                PF('fixedLayout').toggle();
            }
        }
        store('layout.fixed', fixedLayout);
    }

    function updateSidebarCollapded(sidebarCollapsed) {
        if (isMobile() || isLayoutTop()) { //fixed layout not compatible with small screens (admin-template already has behaviour for navbar when on mobile) neither boxed layout
            disableControlSidebarOption('#sidebar-collapsed');
            store('layout.sidebar-collapsed', false);
            return;
        }  
        if (sidebarCollapsed === true || sidebarCollapsed === 'true') {
            if (!$('body').hasClass('sidebar-collapse')) {
                $('body').addClass('sidebar-collapse');
            }
            if (!PF('sidebarCollapsed').input.is(':checked')) {//it will not be checked when the value comes from browser local storage 
                PF('sidebarCollapsed').toggle();
            }
        } else {
            if ($('body').hasClass('sidebar-collapse')) {
                $('body').removeClass('sidebar-collapse');
            }
            if (PF('sidebarCollapsed').input.is(':checked')) {//update input when value comes from local storage
                PF('sidebarCollapsed').toggle();
            }
        }
        store('layout.sidebar-collapsed', sidebarCollapsed);

    }

    function updateSidebarExpand(expandOnHover) {
        if(isMobile() || isLayoutTop()) {
            disableControlSidebarOption('#sidebar-expand-hover');
            store('layout.sidebar-expand-hover', false);
            return;
        }
        
        if (expandOnHover === true || expandOnHover === 'true') {
           if (!PF('sidebarExpand').input.is(':checked')) {//it will not be checked when the value comes from browser local storage 
                PF('sidebarExpand').toggle();
            }
            $pushMenu.expandOnHover();
            collapseSidebar();
        } else {
            if (PF('sidebarExpand').input.is(':checked')) {
                PF('sidebarExpand').toggle();
            }
            var sidebarCollapsed = get('layout.sidebar-collapsed');
            if(sidebarCollapsed !== true && sidebarCollapsed !== "true") {
                expandSidebar();
            }
            $('[data-toggle="push-menu"]').data('lte.pushmenu', null); //not working, see https://github.com/almasaeed2010/AdminLTE/issues/1843#issuecomment-379550396
            $('[data-toggle="push-menu"]').pushMenu({expandOnHover: false});
            $pushMenu = $('[data-toggle="push-menu"]').data('lte.pushmenu');
        }

        store('layout.sidebar-expand-hover', expandOnHover);

    }
    
    function updateTemplate() {
        var isDefaultTemplate = PF('toggleLayout').input.is(':checked');
        if(isDefaultTemplate === true || isDefaultTemplate === "true") {
            if(isLayoutTop()) {
                $('body').removeClass('layout-top-nav');
            }
        } else {
            if(!isLayoutTop()) {
                $('body').addClass('layout-top-nav');
            }
        }
        store('layout.default-template',isDefaultTemplate);
    }
    
   function updateSidebarToggle(sidebarControlOpen) {
        var sidebarOpenCkbox = $('#control-sidebar-toggle span.ui-chkbox-icon');
        if (sidebarControlOpen === true || sidebarControlOpen === 'true') {
            sidebarOpenCkbox.addClass('ui-icon-check');
            sidebarOpenCkbox.removeClass('ui-icon-blank');
            sidebarOpenCkbox.parent().addClass('ui-state-active');
            $('.control-sidebar').addClass('control-sidebar-open');
            $('body').addClass('control-sidebar-open');
        } else {
            sidebarOpenCkbox.addClass('ui-icon-blank');
            sidebarOpenCkbox.removeClass('ui-icon-check');
            sidebarOpenCkbox.parent().removeClass('ui-state-active');
            $('.control-sidebar').removeClass('control-sidebar-open')
            $('body').removeClass('control-sidebar-open');
        }

        store('layout.sidebar-control-open', sidebarControlOpen);

    }
    
    function loadSkin() {
        var skin = get('layout.skin');
        if (skin && !$('body').hasClass(skin)) {
            $('#btn-'+skin).click();
        }
    }
    
    function loadTemplate() {
         var isDefaultTemplate = get('layout.default-template');
         if (isDefaultTemplate === "true" && isLayoutTop()) {
             PF('toggleLayout').toggle();
         } else if(isDefaultTemplate === "false" && !isLayoutTop()) {
             PF('toggleLayout').toggle();
         }
         
    }

    function disableControlSidebarOption(id) {
        var optionSelector = id.concat(", ").concat(id).concat(" span.ui-chkbox-icon, ").concat(id).concat("-label");
        $(optionSelector).addClass('ui-state-disabled');
    }
    
    function enableControlSidebarOption(id) {
        var optionSelector = id.concat(" ,").concat(id).concat(" span.ui-chkbox-icon, ").concat(id).concat("-label");
        $(optionSelector).removeClass('ui-state-disabled');
    }
    
    function restoreDefaults() {
        store('layout.skin', null);
        store('layout.default-template', null);
        store('layout.sidebar-expand-hover', null);
        store('layout.sidebar-control-open', null);
        store('layout.fixed', null);
        store('layout.boxed', null);
        store('layout.sidebar-collapsed', null);
        store('layout.sidebar-skin', null);
        loadLayoutDefaults();
    }

    /**
     * Retrieve stored settings and apply them to the template
     *
     * @returns void
     */
    function setup() {

        var sidebarSkin = get('layout.sidebar-skin');

        if (!sidebarSkin) {
            sidebarSkin = 'control-sidebar-dark';
        }

        updateSidebarSkin(sidebarSkin);

        updateSidebarToggle(get('layout.sidebar-control-open'));


        var boxedLayout = get('layout.boxed');

        if(boxedLayout === null || boxedLayout === "null") {
            boxedLayout = PF('boxedLayout').input.is(':checked');
        }  
        
        updateBoxedLayout(boxedLayout);

        var fixedLayout = get('layout.fixed');
        
        
        if(fixedLayout === null || fixedLayout === "null") {
            fixedLayout = PF('fixedLayout').input.is(':checked');
        }
        
        updateFixedLayout(fixedLayout);
        
        var sidebarCollapsed = get('layout.sidebar-collapsed');
        
        
        if(sidebarCollapsed === null || sidebarCollapsed === "null") {
            sidebarCollapsed = PF('sidebarCollapsed').input.is(':checked');
        }
        
        updateSidebarCollapded(sidebarCollapsed);
        
        var expandOnHover = get('layout.sidebar-expand-hover');
        
        if(expandOnHover === null || expandOnHover === "null") {
            expandOnHover = PF('sidebarExpand').input.is(':checked');
        }

        updateSidebarExpand(expandOnHover);


        $('#sidebar-skin').on('click', function () {
            var sidebarSkin;
            if ($('.control-sidebar').hasClass('control-sidebar-dark')) {
                sidebarSkin = 'control-sidebar-light';
            } else {
                sidebarSkin = 'control-sidebar-dark';
            }
            setTimeout(function () {
                updateSidebarSkin(sidebarSkin);
            }, 20);
        });

        $('#boxed-layout .ui-chkbox-box, #boxed-layout-label').on('click', function () {
            var boxedLayout = $('body').hasClass('layout-boxed');
            setTimeout(function () {
                updateBoxedLayout(!boxedLayout); //negate current value in order to update it's state from boxed to not boxed and vive versa
                updateFixedLayout(get('layout.fixed'));
            }, 20);
        });

        $('#fixed-layout .ui-chkbox-box, #fixed-layout-label').on('click', function () {
            var fixedLayout = $('body').hasClass('fixed');
            setTimeout(function () {
                updateFixedLayout(!fixedLayout); //negate it's current value so we can change it's state
                updateBoxedLayout(get('layout.boxed'));
            }, 20);
        });
        
        $('#sidebar-collapsed .ui-chkbox-box, #sidebar-collapsed-label').on('click', function () {
            var sidebarCollapsed = $('body').hasClass('sidebar-collapse');
            setTimeout(function () {
                updateSidebarCollapded(!sidebarCollapsed);//negate because we want to toogle its state from collpased to not collapsed and vice versa
            }, 20);
        });

        $('#control-sidebar-toggle .ui-chkbox-box, #control-sidebar-toggle-label').on('click', function () {
            setTimeout(function () {
                changeLayout('control-sidebar-open');
                updateSidebarToggle($('body').hasClass('control-sidebar-open'));
            }, 20);

        });


        $('#sidebar-expand-hover .ui-chkbox-box, #sidebar-expand-hover-label').on('click', function () {
            var expandOnHover = PF('sidebarExpand').input.is(':checked')
            setTimeout(function () {
                updateSidebarExpand(expandOnHover);
            }, 20);
        });


        $('#content').click(function () {
            $('.control-sidebar').removeClass('control-sidebar-open');
        });
        
        $('#toggle-menu-layout .ui-chkbox-box, #toggle-menu-layout').on('click', function () {
            setTimeout(function () {
                updateTemplate();
            }, 20);
        });
        
        $('#restore-defaults a').on('click', function () {
            setTimeout(function () {
                restoreDefaults();
            }, 20);
        });
        
        loadTemplate();

        loadSkin();

    }


    $(document).on("pfAjaxComplete", function () {
        setTimeout(function () {
            setup();
        }, 20);
    });


    $(document).ready(function () {
        setTimeout(function () {
            setup();
        }, 20);
    });


});