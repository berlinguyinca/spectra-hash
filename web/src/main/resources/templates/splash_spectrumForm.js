
$(document).ready(function(){
  $('#code').hide();
  $("button").click(function() {
              var entry = $("#data").val();
              var ions = [];

              entry = entry.trim();

              if(/^(\d+(\.\d+)?:\d+\s)*(\d+(\.\d+)?:\d+)$/.test(entry)){

              }
              else if(/^(\d+(\.\d+)?\s\d+;)*(\d+(\.\d+)?\s\d+)$/.test(entry)){
                entry = entry.replace(/\s/g, ':')
                entry = entry.replace(/\;/g, ' ')
              }
              else if(/^(\d+(\.\d+)?\s\d+\n)*(\d+(\.\d+)?\s\d+)$/.test(entry)){
                entry = entry.replace(/ /g, ':')
                entry = entry.replace(/\n/g, ' ')
              }
              else{
                $('#response').val("Incorrect Format\nEnter data in correct formatting")
                $('#code').show();
                return;
              }

              var mass = entry.match(/\d+(\.\d+)?:/g);
              var intensity = entry.match(/:\d+/g);

              for ( var i = 0; i < mass.length; i++ ) {
                mass[i] = mass[i].replace(/\:/g, '')
                intensity[i] = intensity[i].replace(/\:/g, '')
                var ion = {};
                ion["mass"] = mass[i]
                ion.intensity = intensity[i]
                ions.push(ion)
              }

              $.ajax({
                url:"https://splash.fiehnlab.ucdavis.edu/splash/it",
                type: "POST",
                data: JSON.stringify({"ions":	ions,
                "type": "MS"}),
                contentType:"application/json; charset=utf-8",
                dataType:"text",
                success: function(response) {
                  console.log(arguments);
                  //alert("SPLASH code: " + response);
                  $('#response').val(response);
                  $('#code').show();
                },
                error: function(xhr, status, errorThrown){
                  console.log(arguments);
                  //alert("Error:\n" + status + " " + errorThrown);
                  //var message = "error " + status + " " + errorThrown;
                  $('#response').val("Error:\nDifficulty connecting to server. Try again later." );
                  $('#code').show();
                }
               });
          });
 });
