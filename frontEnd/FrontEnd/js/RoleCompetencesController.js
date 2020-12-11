class RoleCompetencesController {


    /**
     * Constructor
     *
     * @param endPoint
     */
    constructor(endPoint) {
        this.viewEndPoint = endPoint + "/role-competencies";
        this.editEndPoint = endPoint + "/edit-role-competencies";
        this.competencesViewEndpoint = endPoint + "/competencies";
        this.selectedRowID = -1;
        this.doEdit = false;

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
        /* Call the microservice and evaluate data and result status */
        $.getJSON(this.viewEndPoint, function (data) {
            controller.renderGUI(data);
        }).done(function () {
            // controller.renderAlert('Data charged successfully.', true);

        }).fail(function () {
            controller.renderAlert('Error while charging data. Retry in a few second.', false);
        });

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
            let list = "<ul>";
            row = row.replace(/{ID}/ig, obj.role.id);
            row = row.replace(/{Name}/ig, obj.role.name);
            row = row.replace(/{Description}/ig, obj.role.description);
            $.each(obj.competences, function (index, obj) {
                list = list + "<li>" + obj.name + "</li>"
            });
            list = list + "</ul>";
            row = row.replace(/{Competences}/ig, list);

            let editIcon = '<button class="btn btn-outline-primary btn-sm"  data-toggle="modal"\n' +
                '                data-target="#edit-modal" onclick="controller.doEdit=true">'
                +'<i class="fas fa-edit mr-2"></i>'+
                '        </button>';
            row = row.replace(/{Edit}/ig, editIcon);
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
     * Open Model, download Json data and render.
     */
    viewEdit() {
        let controller = this
        $.getJSON(controller.competencesViewEndpoint, function (data) {
            controller.addCheckBoxes(data);
        }).done(function () {
            // Request Data for selected row
            $.getJSON(controller.viewEndPoint, {id: controller.selectedRowID}, function (data) {
                $('#edit-id').val(data.role.id);
                $('#edit-name').val(data.role.name);
                controller.markCompetencesAlreadyPossessed(data.competences);
            }).done(function () {
                $('#edit-modal').modal('show');
            });
        });
    }

    /**
     * Adds a checkbox for each element of the Json data
     *
     * @param data  a JSON representation of data
     */
    addCheckBoxes(data) {
        $('#table-competence td').remove();
        let staticHtml = $("#competence-template").html();
        $.each(data, function (index, obj) {
            let elem = staticHtml;
            elem = elem.replace(/{Competence}/ig,
                "<div class='container d-inline-block'>" +
                "<input type='checkbox' class='checkbox' name='id-competence' value='" + obj.id + "'>\t" +
                "<strong>" + obj.name + ":</strong>" +
                "<td>" + obj.description + "</td>" +
                "</div>");
            $('#competence-rows').append(elem);
        });
    }

    /**
     * Cycles over all the competences possessed and marks as 'checked' the related checkbox element
     *
     * @param competence JSON representation of data
     */
    markCompetencesAlreadyPossessed(competence) {

        $.each(competence, function (index, obj) {
            let checkboxes = document.getElementsByClassName("checkbox");
            Array.prototype.filter.call(checkboxes, function (element) {
                if ($(element).val() === obj.id) {
                    $(element).prop('checked', 'true');
                }
            }, false)
        });
    }

    /**
     * Send edit request and show result alert.
     */
    edit() {
        let controller = this;
        let data = $('#edit-form').serialize();
        console.log(data)

        $.post(this.editEndPoint, data, function () {
            // waiting
        }).done(function () {
            // show alert
            controller.renderAlert('Competences updated successfully', true);
            // success

            controller.fillTable();
            controller.unselect();
        }).fail(function () {
            controller.renderAlert('Error while editing. Try again.', false);
        });
    }


    /**
     * Function method for handle click on
     */
    handleClickOnRow() {

        // set new selected id
        controller.setSelectedRowID($(this).find("td:first").html());

        // ability modify and edit button
        $('#edit-button').prop('disabled', false);

        // set highlights row
        $('.selected').removeClass('selected');
        $(this).addClass("selected");

        if(controller.doEdit === true){
            controller.doEdit = false;
            controller.viewEdit();
        }

    }


    /**
     * Function used when no one row have to be selected (e.g., when a role is successfully created or edited).
     */
    unselect() {
        $('#edit-button').prop('disabled', true);
        $('.selected').removeClass('selected');
        controller.setSelectedRowID(-1);
    }
}
