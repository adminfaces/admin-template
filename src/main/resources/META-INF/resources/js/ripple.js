if (isMobile()) {
    var rippleElements = '.ripplelink,button.ui-button,.ui-selectlistbox-item,.ui-multiselectlistbox-item,.ui-selectonemenu,.ui-selectcheckboxmenu,' +
        '.ui-autocomplete,.ui-autocomplete-item,.ui-splitbutton-menubutton,.ui-splitbutton,.input-group,.ui-paginator-page,.ui-paginator-next,.ui-paginator-prev,' +
        '.ui-chkbox-icon,.ui-radiobutton-icon,.ui-link,.form-control,.btn,.ui-sortable-column';

    $(function () {
        $(document.body).off('mousedown.ripple', rippleElements)
            .on('mousedown.ripple', rippleElements, null, function (e) {
                var element = $(this);
                $(".ripple").remove();
                var posX = element.offset().left,
                    posY = element.offset().top,
                    width = element.width(),
                    height = element.height();

                element.prepend("<span class='ripple'></span>");


                if (width >= height) {
                    height = width;
                } else {
                    width = height;
                }
                var x = e.pageX - posX - width / 2;
                var y = e.pageY - posY - height / 2;

                $(".ripple").css({
                    width: width,
                    height: height,
                    top: y + 'px',
                    left: x + 'px'
                }).addClass("rippleEffect");
            });
    });

}