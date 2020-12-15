class MaintainerAssignmentPlannedActivitiesController {

    /**
     * Constructor
     *
     * @param endPoint
     * @param type
     * @param id
     */
    constructor(endPoint, type, id) {
        this.viewWeeklyEndPoint = endPoint + "/availability-weekly";
        this.viewDailyEndPoint = endPoint + "/availability-daily";
        this.viewActivityEndPoint = endPoint + "/activity";
        this.assignEndPoint = endPoint + "/assign";

        this.day = "";
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
    initPage() {
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
            + data.typology.name + " - " + data.estimatedInterventionTime + "'";

        this.activityTime = data.estimatedInterventionTime;

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

        this.fillTableWeek();

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
    }

    /**
     * Fill the table of the weekly availability with the data requested to the servlet
     */
    fillTableWeek() {
        let controller = this;
        // If table not empty
        $('#table-weekly-availability td').remove();

        $('#select-day-row').addClass('invisible');
        // Get the html template for table rows
        let staticHtml = $("#table-weekly-availability-template").html();
        let id = searchParams.get('activity-id');
        $.getJSON(this.viewWeeklyEndPoint, {'activity-id': id}, function (data) {
            /* Bind obj data to the template, then append to table body */
            $.each(data, function (index, obj) {

                let row = staticHtml;
                row = row.replace(/{Id}/ig, obj.id);
                row = row.replace(/{Maintainer}/ig, obj.name);
                row = row.replace(/{Compliance}/ig, obj.competence_compliance);
                $.each(obj.availability, function (index, obj) {
                    switch (obj.day) {
                        case "1":
                            row = row.replace(/{Monday}/ig, obj.percentage + '%');
                            break;
                        case "2":
                            row = row.replace(/{Tuesday}/ig, obj.percentage + '%');
                            break;
                        case "3":
                            row = row.replace(/{Wednesday}/ig, obj.percentage + '%');
                            break;
                        case "4":
                            row = row.replace(/{Thursday}/ig, obj.percentage + '%');
                            break;
                        case "5":
                            row = row.replace(/{Friday}/ig, obj.percentage + '%');
                            break;
                        case "6":
                            row = row.replace(/{Saturday}/ig, obj.percentage + '%');
                            break;
                        case "7":
                            row = row.replace(/{Sunday}/ig, obj.percentage + '%');
                            break;
                    }
                });
                $('#table-weekly-availability-rows').append(row);
            });
            controller.coloring("WeekAvail");
            /* When empty address-book */
            if (data.length === 0) {
                $("tfoot").html('<tr><th colspan="12">No records</th></tr>');
            } else {
                $("tfoot tr:first").fadeOut(100, function () {
                    $("tfoot tr:first").remove();
                })
            }
        });
    }

    /**
     * Function method for handle click on table Week
     */
    handleClickOnWeekRow() {
        // set new selected id
        let maintainerId = $(this).closest("tr").find("td:first").html();
        let row_index = $(this).parent().index('tr')
        let day = $(this).index('tr:eq(' + row_index + ') td') - 2;

        if (day < 1) {
            return;
        }
        controllerPlanned.day = day;
        $('#select-day-row').removeClass('invisible');

        controllerPlanned.changeTablesVisibility()
        let data = {
            'activity-id': controllerPlanned.activityId,
            'maintainer-id': maintainerId,
            'day': day
        }
        controllerPlanned.fillTableDaily(data);
    }

    /**
     * Fill the table of the daily availability with the data requested to the servlet
     * @param data the data of the servlet request
     */
    fillTableDaily(data) {
        let controller = this;

        $('#select-day').val(controller.dayNumberToName(controller.day.toString()));
        // If table not empty
        $('#table-daily-availability td').remove();
        $('#table-daily-availability th').remove();

        // Get the html template for table rows
        let staticHtml = $("#table-daily-availability-template").html();
        let header = $("#daily-header-template").html();
        let slotsHeader = "";
        let slotsData = "";
        controller.timeToAssign = controller.activityTime;
        $('#timeRequired').val(controller.activityTime);

        $.getJSON(controller.viewDailyEndPoint, data, function (data) {
            $('#daily-label').text('AVAILABILITY ' + data.name);

            /* Bind obj data to the template, then append to table body */
            let row = staticHtml;
            controller.maintainerId = data.id;
            slotsData = slotsData + "<td class='invisible'> " + data.id + "</td>";
            slotsData = slotsData + "<td> " + data.name + "</td>";
            slotsData = slotsData + "<td> " + data.competence_compliance + "</td>";

            $.each(data.availability, function (index, obj) {
                slotsHeader = slotsHeader + "<th>" + obj.description + "</th>";
                slotsData = slotsData + "<td class='avail'>" + obj.minutes + 'min' + "</td>";
            });

            header = header.replace(/{Slots}/ig, slotsHeader);
            row = row.replace(/{Slots}/ig, slotsData);

            $('#daily-header').append(header);
            $('#table-daily-availability-rows').append(row);
        }).done(function () {
            controller.coloring("avail");
        })


        if (data.length === 0) {
            $("tfoot").html('<tr><th colspan="12">No records</th></tr>');
        } else {
            $("tfoot tr:first").fadeOut(100, function () {
                $("tfoot tr:first").remove();
            })
        }

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
        controllerPlanned.maintainerId = $(this).closest("tr").find("td:first").html().trim();

        let row_index = $(this).parent().index('tr')
        let slot = $(this).index('tr:eq(' + row_index + ') td') - 2;

        if (slot > 0) {
            let slotSelected = '#table-daily-availability-rows td:nth-child(' + (slot + 3) + ')';

            let minAvailable = parseInt($(slotSelected).text(), 10);
            $('#send-button').prop('disabled', true);
            $('#undo-button').prop('disabled', false);

            if(controllerPlanned.slotID.includes(slot)){
                return;
            }

            if (controllerPlanned.timeToAssign > minAvailable) {
                controllerPlanned.slotID.push(slot);
                controllerPlanned.minutes.push(minAvailable);

                $(slotSelected).text("0min");
                $(slotSelected).val(0);

                controllerPlanned.timeToAssign -= minAvailable;

            } else {
                // assign all the available minutes of the slot 'till the completion of the estimated time
                controllerPlanned.slotID.push(slot);
                controllerPlanned.minutes.push(controllerPlanned.timeToAssign);

                $(this).text(minAvailable - controllerPlanned.timeToAssign + "min");
                $(this).val(minAvailable - controllerPlanned.timeToAssign);

                controllerPlanned.timeToAssign = 0;
            }

            controllerPlanned.coloring('avail')
            $('#timeToAssign').val(controllerPlanned.timeToAssign);
            $('#timeAssigned').val(controllerPlanned.activityTime - controllerPlanned.timeToAssign);

            if (controllerPlanned.timeToAssign == 0) {
                $('#send-button').prop('disabled', false);
                $('#table-daily-availability tbody').off('click');
            }
        }
    }

    /**
     * Function that switches the visibility of the tables
     */
    changeTablesVisibility() {
        $('#first-div').addClass('invisible');
        $('#second-div').removeClass('invisible');
        $('#time-reminder').removeClass('invisible');
    }

    /**
     * Function that redirects onto the previous page
     */
    back() {

        window.location.replace('ActivitySelection.html');

    }

    /**
     * Function that allows to undo the availability choices by recharging the table
     */
    undo() {
        $('#send-button').prop('disabled', true);
        $('#undo-button').prop('disabled', true);

        let data = {
            'activity-id': this.activityId,
            'maintainer-id': this.maintainerId,
            'day': this.day
        }
        this.fillTableDaily(data)
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

        let data = 'activity-id=' + this.activityId + '&maintainer-id=' + this.maintainerId + '&day=' + this.day
            + slots + minutes;


        $.post(this.assignEndPoint, data, function (data) {

        }).done(function () {
            alert("Activity Assigned Correctely");
            controllerPlanned.back();
        })
    }

    /**
     * Function that allows to undo the maintainer choice by recharging the maintainer table
     */
    changeMaintainer() {
        $('#time-reminder').addClass('invisible');
        $('#second-div').addClass('invisible');
        $('#first-div').removeClass('invisible');

        $('#send-button').prop('disabled', true);
        $('#undo-button').prop('disabled', true);

        controllerPlanned.initPage();
    }

    /**
     * Given the number of the day of the week, returns its name
     *
     * @param number the number of the day of the week
     * @returns {string} the name of the day of the week
     */
    dayNumberToName(number) {
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
