$(function() {
	var enabledLogins = [];
	$('.login-page-type').each(function(i, element) {
		enabledLogins.push(element);
	});
	for(var i=0; i<enabledLogins.length; i++) {
		if(i==0) {
			$(enabledLogins[i]).show();
		} else {
			$(enabledLogins[i]).hide();
		} 
	}

	$('.login-page-menu').each(function(i, element) {
		$(element).click(function(event) {
			$('.login-page-type').hide();
			var idToShow = $(event.target).data('login-type');
			console.log("Id to show: " + idToShow);
			$(idToShow).show();
		}); 
	});
});