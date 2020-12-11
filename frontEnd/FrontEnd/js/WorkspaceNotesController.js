class WorkspaceNotesController {

    /**
     * Constructor
     *
     * @param endPoint
     */
    constructor(endPoint) {
        this.viewEndPoint = endPoint + "/workspaces";
        this.sitesEndPoint = endPoint + "/site"

        this.createEndPoint = endPoint + "/create-workspaces";
        this.editEndPoint = endPoint + "/edit-workspaces";
        this.deleteEndPoint = endPoint + "/delete-workspaces";
        this.selectedRowID = -1;
        this.doEdit = false;
        this.doDelete = false;
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
            $('#insert-button').prop('disabled', false);
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
            row = row.replace(/{Description}/ig, obj.description);
            let list = "<ul>";
            $.each(obj.site, function (index, obj) {
                list = list + "<li>" + obj.name + "</li>"
            })
            list = list + "</ul>"
            row = row.replace(/{Sites}/ig, list)

            let editIcon = '<button class="btn btn-outline-primary btn-sm"  data-toggle="modal"\n' +
                '                data-target="#edit-modal" onclick="controller.doEdit=true">'
                +'<i class="fas fa-edit mr-2"></i>'+
                '        </button>';
            row = row.replace(/{Edit}/ig, editIcon);

            let deleteIcon = '<button class="btn btn-outline-danger btn-sm"  data-toggle="modal"\n' +
                '                data-target="#delete-modal" onclick="controller.doDelete=true">'
                +'<i class="fas fa-trash mr-2"></i>'+
                '        </button>';
            row = row.replace(/{Delete}/ig, deleteIcon);
            $('#table-rows').append(row);
        });

        /* When empty address-book */
        if (data.length === 0) {
            $("tfoot").html('<tr><th colspan="3">No records</th></tr>');
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
        let controller = this;
        $.getJSON(controller.sitesEndPoint, function (data) {
            controller.addCheckBoxes(data, '#edit-table-site');
        }).done(function () {
            $.getJSON(controller.viewEndPoint, {id: controller.selectedRowID}, function (data) {
                $('#edit-id').val(data.id);
                $('#edit-description').val(data.description);
                controller.markAsChecked(data.site);
            }).done(function () {
                $('#edit-modal').modal('show');
            })
        });
    }

    /**
     * Add a checkbox for each element of the data
     *
     * @param data a json of elements
     * @param table the table to render
     */
    addCheckBoxes(data, table) {
        $(table + ' td').remove();
        let staticHtml = $(table + '-template').html();
        $.each(data, function (index, obj) {
            let elem = staticHtml;
            elem = elem.replace(/{Site}/ig,
                "<div class='container d-inline-block'>" +
                "<input type='checkbox' class='checkbox' name='id-site' value='" + obj.id + "'>\t" + obj.name + "</div>");
            $(table + '-rows').append(elem);
        });
    }

    /**
     * Marks as Checked all the checkbox whom site is already associated
     *
     * @param sites
     */
    markAsChecked(sites) {
        $.each(sites, function (index, obj) {
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
        if (validate('#edit-form') === false) {
            controller.renderAlert('Error: The input fields cannot be left empty. Edit rejected', false)
            return;
        }
        let data = $('#edit-form').serialize();
        $.post(this.editEndPoint, data, function () {
            // waiting
        }).done(function () {
            // show alert
            controller.renderAlert('Workspace Note edited successfully.', true);
            // success
            $('#edit-id').val('');
            $('#edit-description').val('');
            controller.fillTable();
            controller.unselect();
        }).fail(function () {
            controller.renderAlert('Error while editing. Try again.', false);
        });
    }

    /**
     * Open Model, download Json data and render.
     */
    deleteView() {
        $.get(this.viewEndPoint, {id: this.selectedRowID}, function (data) {
            $('#delete-id').val(data.id);
            $('#delete-name').html(data.name)
        }).done(function () {
            $('#delete-modal').modal('show')
        });
        // Charging
    }

    /**
     * Call delete service and get requested data with get method. Show an alert showing the response.
     */
    delete() {
        let controller = this;
        let data = $('#delete-form').serialize();
        $.get(this.deleteEndPoint, data, function () {
            // waiting
        }).done(function () {
            // show alert
            controller.renderAlert('Workspace Note successfully deleted.', true);
            // charge new data.
            controller.fillTable();
            controller.unselect();
        }).fail(function () {
            controller.renderAlert('Error while deleting. Try again.', false);
        });
    }

    /**
     * Open the insert modal and render
     */
    insertView(){
        let controller = this;
        $.getJSON(this.sitesEndPoint, function(data){
            controller.addCheckBoxes(data, '#insert-table-site')
        });
    }
    /**
     * Call insert service and get requested data with post method. Show an alert showing the response.
     */
    insert() {
        let controller = this;

        if (validate('#insert-form') === false) {
            controller.renderAlert('Error: Not all fields have been entered correctly. Please try again', false)
            return;
        }

        let data = $('#insert-form').serialize();

        $.post(this.createEndPoint, data, function () { // waiting for response-

        }).done(function () { // success response-
            // Set success alert.
            controller.renderAlert('Workspace Note successfully entered.', true);
            // Reset modal form.
            $('#add-description').val('');
            // charge new data.
            controller.fillTable();
            controller.unselect();
        }).fail(function () { // fail response
            controller.renderAlert('Error while inserting. Try again.', false);
        });
        $('insert-form').modal('hide')
    }
    ;

    /**
     * Function method for handle click on
     */
    handleClickOnRow() {
        // set new selected id
        controller.setSelectedRowID($(this).find("td:first").html());

        // ability modify and edit button
        $('#edit-button').prop('disabled', false);
        $('#delete-button').prop('disabled', false);

        // set highlights row
        $('.selected').removeClass('selected');
        $(this).addClass("selected");

        if(controller.doEdit === true){
            controller.doEdit = false;
            controller.viewEdit();
        }

        if(controller.doDelete === true){
            controller.doDelete = false;
            controller.deleteView();
        }
    }


    /**
     * Function used when no one row have to be selected (e.g., when a role is successfully created or edited).
     */
    unselect() {
        $('#edit-button').prop('disabled', true);
        $('#delete-button').prop('disabled', true);
        $('.selected').removeClass('selected');
        controller.setSelectedRowID(-1);
    }


}
