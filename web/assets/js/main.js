$(document).ready(function() {


	$("#main-menu li").children(".sub").stop(true, true).css("display", "none");
	$("#main-menu li").has(".sub").hover(function(){
		$(this).addClass("current").children(".sub").fadeIn();
	}, function() {
		$(this).removeClass("current").children(".sub").stop(true, true).css("display", "none");
	});

	$("form label").inFieldLabels();
        
	$('.tab-map a').click(function() {
	  $('.tab-image').removeClass('active');
	  $('.tab-map').addClass('active');
	  $('.tab-2').show();
	  $('.tab-1').hide();
	});
        
        $('.tab-image a').click(function() {
	  $('.tab-map').removeClass('active');
	  $('.tab-image').addClass('active');
	  $('.tab-2').hide();
	  $('.tab-1').show();
	});
        
        $('.dummy-select').click(function() {
	  $('.dummy-select ul').show();
	});
});