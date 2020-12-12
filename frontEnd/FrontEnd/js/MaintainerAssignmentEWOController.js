class MaintainerAssignmentEWOController {

    /**
     * Constructor
     *
     * @param endPoint
     * @param type
     * @param id
     */
    constructor(endPoint, id) {

        this.viewDailyEndPoint = endPoint + "/availability-daily";
        this.viewActivityEndPoint = endPoint + "/activity";
        this.assignEndPoint = endPoint + "/assign";

        this.day;
        this.activityId = id;
        this.activityTime = 0;
        this.timeToAssign = 0;
        this.maintainerId = 0;
        this.slotID = [];
        this.minutes = [];
    }

    /**
     * Fetch JSON data from the service, then call a function for rendering the View
     *
     * @use renderGUI()
     */
    fillTable() {
        let controller = this;

        /* Call the microservice and evaluate data and result status */
        $.getJSON(this.viewActivityEndPoint, {id: controller.activityId}, function (data) {
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

        let activity = data.id + " - " + data.site.name + " - "
            + data.typology.name;

        this.activityTime = data.estimatedInterventionTime;

        let day = data.day;
        this.day = day;
        $('#select-day').val(this.getDay(day));
        $('#select-week').val(data.week);
        $('#select-year').val(data.year);
        $("#select-activity").val(activity);

        if (data.workspace.id !== "0") {
            $("#select-workspace").val(data.workspace.description);
        }
        $('#skills-list li').remove();
        $.each(data.skill, function (index, obj) {
            $('#skills-list').append("<li class='list-group-item py-0'>" + obj.name + "</li>");
        });

        let dailyData = {
            'activity-id': controller.activityId,
            'day': day
        };
        controller.renderTableDaily(dailyData);

        /* When empty address-book */
        if (data.length === 0) {
            $("tfoot").html('<tr><th colspan="12">No records</th></tr>');
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
     * Fill the table of the daily availability with the data requested to the servlet
     * @param data the data of the servlet request
     */
    renderTableDaily(data) {
        let controller = this;

        // If table not empty
        $('#table-daily-availability td').remove();
        $('#table-daily-availability th').remove();

        // Get the html template for table rows
        let staticHtml = $("#table-daily-availability-template").html();
        let header = $("#daily-header-template").html();
        let slotsHeader = "";

        this.timeToAssign = this.activityTime;
        $('#timeRequired').val(this.activityTime);

        $.getJSON(this.viewDailyEndPoint, data, function (data) {

            $('#daily-label').text('AVAILABILITY ' + data.name);
            $.each(data, function (index, obj) {
                let slotsData = "";
                /* Bind obj data to the template, then append to table body */
                let row = staticHtml;
                controller.maintainerId = obj.id;
                slotsData = slotsData + "<td class='invisible'> " + obj.id + "</td>";
                slotsData = slotsData + "<td> " + obj.name + "</td>";
                slotsData = slotsData + "<td> " + obj.competence_compliance + "</td>";

                $.each(obj.availability, function (index, obj) {
                    slotsHeader = slotsHeader + "<th>" + obj.description + "</th>";
                    slotsData = slotsData + "<td class='avail'>" + obj.minutes + 'min' + "</td>";
                });
                header = header.replace(/{Slots}/ig, slotsHeader);
                row = row.replace(/{Slots}/ig, slotsData);

                $('#daily-header').append(header);
                header = "";
                $('#table-daily-availability-rows').append(row);
            });

            if (data.length === 0) {
                $("tfoot").html('<tr><th colspan="12">No records</th></tr>');
            } else {
                $("tfoot tr:first").fadeOut(100, function () {
                    $("tfoot tr:first").remove();
                })
            }
        }).done(function () {
            controller.coloring("avail");
        });

        $('#table-daily-availability tbody').on("click", "td", null, controller.handleClickOnDayRow);
        this.minutes = [];
        this.slotID = [];
        $('#timeToAssign').val(controller.activityTime);
        $('#timeAssigned').val(0);
    }

    /**
     * Function for handling the click on daily table row
     */
    handleClickOnDayRow() {

        // set new selected id
        controller.maintainerId = $(this).closest("tr").find("td:first").html().trim();
        let row_index = $(this).parent().index('tr');
        let slot = $(this).index('tr:eq(' + row_index + ') td') - 2;

        if (slot > 0) {
            let minAvailable = parseInt($(this).text(), 10);

            $('#send-button').prop('disabled', true);
            $('#undo-button').prop('disabled', false);


            if (controller.timeToAssign > minAvailable) {

                controller.slotID.push(slot);
                controller.minutes.push(minAvailable);

                $(this).text("0min");
                $(this).val(0);
                controller.coloring('avail')
                controller.timeToAssign -= minAvailable;

            } else {
                // assign all the available minutes of the slot 'till the completion of the estimated time
                controller.slotID.push(slot);
                controller.minutes.push(controller.timeToAssign);

                $(this).text(minAvailable - controller.timeToAssign + "min");
                $(this).val(minAvailable - controller.timeToAssign);
                controller.coloring('avail')

                controller.timeToAssign = 0;
            }

            $('#timeToAssign').val(controller.timeToAssign);
            $('#timeAssigned').val(controller.activityTime - controller.timeToAssign);

            if (controller.timeToAssign == 0) {
                $('#send-button').prop('disabled', false);
                $('#table-daily-availability tbody').off('click');
            }
        }
    }

    /**
     * Function that redirects onto the previous page
     */
    back() {
        window.location.replace('EWOSelection.html');
    }

    /**
     * Function that allows to undo the availability choices by recharging the table
     */
    undo() {
        $('#send-button').prop('disabled', true);
        $('#undo-button').prop('disabled', true);

        let data = {
            'activity-id': controller.activityId,
            'day': controller.day
        }
        controller.renderTableDaily(data)
    }

    /**
     * Call the assign service and post the selected data. If the assignation is confermed, shows an alert and redirects
     */
    send() {

        let slots = ",";
        $.each(this.slotID, function (index, obj) {
            slots = slots + "&slot-id=" + obj;
        });
        slots = slots.substring(1, slots.length)

        let minutes = ",";
        $.each(this.minutes, function (index, obj) {
            minutes = minutes + "&minutes=" + obj;
        });
        minutes = minutes.substring(1, minutes.length)

        let data = 'activity-id=' + controller.activityId + '&maintainer-id=' + controller.maintainerId + '&day=' + controller.day
            + slots + minutes;


        $.post(controller.assignEndPoint, data, function (data) {

        }).done(function () {
            alert("Activity Assigned Correctely");
            controller.back();
        });


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
     * function that converts the percentage into an array rgba
     * @param perc - the percentage
     * @returns {(number)[]} - the rgba array
     */
    perc2color(perc) {
        var r, g, b = 0;
        if (perc < 50) {
            r = 255;
            g = Math.round(5.1 * perc);
        } else {
            g = 255;
            r = Math.round(510 - 5.10 * perc);
        }
        return [r, g, b, 0.7];
    }


    /**
     * Function that colors the table's data row according to its css class
     * @param cssClass - the css Class of the data to color
     */
    coloring(cssClass) {
        let controller = this;
        let slots = document.getElementsByClassName(cssClass);
        Array.prototype.filter.call(slots, function (slot) {
            let color;
            if (cssClass === "WeekAvail") {
                color = controller.perc2color(parseInt($(slot).text(), 10));
            } else {
                let min = (parseInt($(slot).text(), 10));
                color = controller.perc2color(min * 100 / 60);
            }
            $(slot).css('background-color', 'rgba(' + color[0] + ',' + color[1] + ',' + color[2] + ',' + color[3] + ')')
        }, false)
    }
}
