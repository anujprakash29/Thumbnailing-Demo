$(document).ready(function () {

    $("#btnSubmit").click(function (event) {
    	alert("POST");
        //stop submit the form, we will post it manually.
        event.preventDefault();

        fire_ajax_submit();

    });

});

function fire_ajax_submit() {

    var form = $('#fileUploadForm')[0];

    var data = new FormData(form);

    $("#btnSubmit").prop("disabled", true);
alert("POST");
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/uploadimage",
        data: data,
        processData: false, //prevent jQuery from automatically transforming the data into a query string
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {

            $("#result").text(data);
            console.log("SUCCESS : ", data);
            $("#btnSubmit").prop("disabled", false);

        },
        error: function (e) {

            $("#result").text(e.responseText);
            console.log("ERROR : ", e);
            $("#btnSubmit").prop("disabled", false);

        }
    });

}