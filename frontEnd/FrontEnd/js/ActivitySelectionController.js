class ActivitySelectionController {

    /**
     * Constructor
     *
     * @param endPoint
     * @param type
     */
    constructor(endPoint, type) {
        this.type = type;

        this.viewEndPoint = endPoint + "/activity";
        this.editEndPoint = endPoint + "/edit-activity";
        this.smpEndPoint = endPoint + "/view-smp";
        this.materialsEndPoint = endPoint + "/material";

        this.selectedRowID = -1;
        this.doSelect = false;
    }

    setSelectedRowID(id) {
        this.selectedRowID = id;
    }

    /**
     * Fetch JSON data from the service, then call a function for rendering the View
     *
     * @use renderGUI()
     */
    fillTable() {

        let controller = this;
        let week = $('#week-selection').val();
        let year = $('#year-selection').val();
        let type = this.type;

        if(week === "0" || year === ""){
            controller.renderAlert('Error. Select both year and week before continuing', false);
            return;
        }


        /* Call the microservice and evaluate data and result status */
        $.getJSON(this.viewEndPoint, {week:week, year:year, type:this.type} ,function (data) {
            controller.renderGUI(data);
        }).done(function () {
           // controller.renderAlert('Data charged successfully.', true);
        }).fail(function () {
            controller.renderAlert('Error while charging data. Retry in a few second.', false);
        });
        // Caricamento
    }

    /**
     * Render the given JSON data into GUI static design
     *
     * @param data a JSON representation of data
     */
    renderGUI(data) {
        // If table not empty
        $('#table td').remove();

        // Get the html template for table rows
        let staticHtml = $("#table-template").html();

        /* Bind obj data to the template, then append to table body */
        $.each(data, function (index, obj) {

            let row = staticHtml;
            row = row.replace(/{ID}/ig, obj.id);
            row = row.replace(/{Site}/ig, obj.site.name);
            row = row.replace(/{Typology}/ig, obj.typology.name);
            row = row.replace(/{Time}/ig, obj.estimatedInterventionTime);

            let selectIcon = '<button class="btn btn-outline-primary btn-sm"  data-toggle="modal"\n' +
                '                data-target="#select-modal" onclick="controller.doSelect=true">'+
                '        Select </button>';
            row = row.replace(/{Select}/ig, selectIcon);

            $('#table-rows').append(row);
        });
        /* When empty address-book */
        if (data.length === 0) {
            $("tfoot").html('<tr><th colspan="4">No records</th></tr>');
        } else {
            $("tfoot tr:first").fadeOut(100, function () {
                $("tfoot tr:first").remove();
            })
        }
    }

    /**
     * Render an alert banner with the message status.
     *
     * @param message: the message to show.
     * @param success: true if is a success banner, false if is a fail banner.
     */
    renderAlert(message, success) {
        let alert;
        if (success) {
            alert = $('#success-alert-template');
        } else {
            alert = $('#fail-alert-template');
        }
        const html = alert.html().replace(/{message}/ig, message)
        // Add banner and remove it after 5 seconds.
        $(html).prependTo('#response-alert-section')
            .delay(5000)
            .queue(function () {
                $(this).remove();
            });
    };

    /**
     * Open select Modal, download Json data and render.
     */
    viewSelect() {
        let controller = this
        controller.addMaterialsChoices();
        $.get(controller.viewEndPoint, {id: controller.selectedRowID}, function (data) {
            let activity;
            activity = data.id  + " - " + data.site.name + " - " + data.typology.name + " - " + data.estimatedInterventionTime +"'";
            $('#select-id').val(data.id);
            $('#select-type').val(controller.type);
            $('#select-site').val(data.site.id);
            $('#select-procedure').val(data.maintenanceProcedures.id);
            $('#select-typology').val(data.typology.id);
            $('#select-time').val(data.estimatedInterventionTime);
            $('#select-interruptibility').val(data.interruptibility);


            $('#select-day').val(controller.getDay(data.day));
            $('#select-day-number').val(data.day)
            $('#select-week').val(data.week);
            $('#select-year').val(data.year);
            $("#select-activity").val(activity);

            $('#select-description').val(data.description);

            if (data.workspace.id != 0) {
                $('#select-notes').val(data.workspace.description);
            }

            $('#select-skills li').remove();
            $.each(data.skill, function (index, obj) {
                $('#select-skills').append("<li >"+obj.name+"</li>");
            });

            if (data.maintenanceProcedures.smp !== "0"){
                $('#smp-icon').prop('disabled', false);
                $('#smp-icon').val(data.maintenanceProcedures.smp);
                $('#smp-name').text(data.maintenanceProcedures.name);
            }else{
                $('#smp-icon').prop('disabled', true);
                $('#smp-name').text('No SMP File Attached');
            }

            controller.markMaterialsAlreadyPossessed(data.materials);
        }).done(function () {

        });
        // Charging
    }



    /**
     * Adds a checkbox for each element of the Json data
     *
     */
    addMaterialsChoices() {

        let controller = this;
        $('#table-material td').remove();
        $.getJSON(controller.materialsEndPoint, function (data) {
            let staticHtml = $("#material-template").html();
            $.each(data, function (index, obj) {
                let elem = staticHtml;
                elem = elem.replace(/{Material}/ig,
                    "<div class='container d-inline-block'>" +
                    "<input type='checkbox' class='checkbox' name='materials' value='" + obj.id + "'>\t" +
                    "<strong>" + obj.name + ":</strong>" +
                    "<td>" + obj.description + "</td>" +
                    "</div>");
                $('#material-rows').append(elem);
            });
        });
    }

    /**
     * Marks as checked all the checkboxes of the materials already possessed
     * @param materials json containing all the materials possessed
     */
    markMaterialsAlreadyPossessed(materials) {
        $.each(materials, function (index, obj) {
            let checkboxes = document.getElementsByClassName("checkbox");
            Array.prototype.filter.call(checkboxes, function (element) {
                if ($(element).val() === obj.id) {
                    $(element).prop('checked', 'true');
                }
            }, false);
        });
    }

    /**
     * Send edit request. If it succeeds, redirect to the next page for the assignment.
     *
     */
    forward(){
        let controller = this;
        $.post(this.editEndPoint, $('#select-form').serialize(), function(data){

        }).done(function(){
            let data = "?activity-id=" + controller.selectedRowID + "&type=" + controller.type;
            window.location.replace('MaintainerAssignment.html' + data)
        });

    }

    /**
     * Perform a get request for the selected file
     */
    smp_request(){
        window.open(this.smpEndPoint + '?id=' + $('#smp-icon').val() , '_blank');
    }

    /**
     * Adds an option for each week of the year
     */
    addWeekChoices() {
        for (let i = 1; i < 53; i++) {
            let row = "<option name='week' value='" + i + "'>" + i + "</option>";
            $('#week-selection').append(row);
        }
    }

    /**
     * Given the number of the day of the week, returns its name
     *
     * @param number the number of the day of the week
     * @returns {string} the name of the day of the week
     */
    getDay(number) {
        switch (number) {
            case "1":
                return "Monday";
            case "2":
                return "Tuesday";
            case "3":
                return "Wednesday";
            case "4":
                return "Thursday";
            case "5":
                return "Friday";
            case "6":
                return "Saturday";
            case "7":
                return "Sunday";
            default:
                return "";
        }
    }

    /**
     * Function method for handle click on
     */
    handleClickOnRow() {
        // set new selected id
        controller.setSelectedRowID($(this).find("td:first").html());

        // set highlights row
        $('.selected').removeClass('selected');
        $(this).addClass("selected");

        if (controller.doSelect === true) {
            controller.doSelect = false;
            controller.viewSelect();
        }

    };

    /**
     * Function used when no one row have to be selected (e.g., when a role is successfully created or edited).
     */
    unselect() {
        $('.selected').removeClass('selected');
        controller.setSelectedRowID(-1);
    };


}
