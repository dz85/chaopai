
	jQuery(document).ready(function() {

					jQuery(document).ready(function() {
jQuery('.fp-slides .inner').cycle({
	fx: 'fade',
	timeout: 3000,
	delay: 0,
	speed: 1000,
	next: '.fp-next',
	prev: '.fp-prev',
	pager: '.fp-pager',
	continuous: 0,
	sync: 1,
	pause: 1000,
	pauseOnPagerHover: 1,
	cleartype: true,
	cleartypeNoBg: true
});
});
		jQuery('ul.menus li:first').css('border-width', '0px');

		jQuery('#mainmenu ul.menus li:first a').css('border-width', '0px');

		jQuery('ul.menus').css('overflow', 'visible');

			

		jQuery("#header .children").addClass('sub-menu');

		jQuery("#header .sub-menu").each(function() {

			jQuery(this).html("<div class='transparent'></div><div class='inner'>"+jQuery(this).html()+"</div>");

		});

			

						jQuery(function(){ 
jQuery('ul.menus').superfish({ 
animation: {width:'show'},					
							autoArrows:  false,
               dropShadows: false, 
               speed: 200,
               delay: 800                });
           });
					jQuery('textarea#comment').each(function(){

			jQuery(this).attr('name','73d9a');

		});

		jQuery('.feedback input').each(function(){

			jQuery(this).attr('name','73d9a['+jQuery(this).attr('name')+']');

		});

		jQuery('.feedback textarea').each(function(){

			jQuery(this).attr('name','73d9a['+jQuery(this).attr('name')+']');

		});

	});


  $('.samble-img').find("a").live('click', function () {
    $('#myModal').modal();
    $("#mode-img")[0].src="/upload/"+this.id;
  })






