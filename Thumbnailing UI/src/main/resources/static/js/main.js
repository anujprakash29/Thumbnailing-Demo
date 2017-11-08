
var settings = {
  "async": true,
  "crossDomain": true,
  "url": "http://localhost:8080/imagelist",
  "method": "GET",
  "headers": {}
}

$.ajax(settings).done(function (data) {
  	console.log(data);

	$.each( data.images, function( i, image ) {
		$("#thumbs").append('<a href="'+image.imageurl+'"> <img src="'+image.thumburl+'" id="'+image.filename+'"/></a>');
	
	});
});