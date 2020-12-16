class MaintainerRoleController {
    /**
     * Constructor
     *
     * @param endPoint
     */
    constructor(endPoint) {
        this.viewEndPoint = endPoint + "/maintainer-role";
        this.createEndPoint = endPoint + "/create-maintainer-role";
        this.editEndPoint = endPoint + "/edit-maintainer-role";
        this.deleteEndPoint = endPoint + "/delete-maintainer-role";
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
            //controller.renderAlert('Data charged successfully.', true);
            $('#insert-button').prop('disabled', false);
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
            row = row.replace(/{ID}/ig, obj.id);
            row = row.replace(/{Name}/ig, obj.name);
            row = row.replace(/{Description}/ig, obj.description);

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
        const html = alert.html().replace(/{message}/ig, message);
        // Add banner and remove it after 5 seconds.
        $(html).prependTo('#response-alert-section')
            .delay(5000)
            .queue(function () {
                $(this).remove();
            });
    };

    /**
     * Open Model, download Json data and render
     * @param id the id of the row to edit.
     */
    viewEdit(id) {
        $.get(this.viewEndPoint, {id: id}, function (data) {
            $('#edit-id').val(data.id);
            $('#edit-name').val(data.name);
            $('#edit-description').val(data.description);
        }).done(function () {
            $('#edit-modal').modal('show');
        });
        // Charging
    }

    /**
     * Send edit request and show result alert.
     */
    edit() {
        let controller = this;
        if (validate('#edit-form') === false) {
            controller.renderAlert('Error: The input fields cannot be left empty. Edit rejected', false);
            return;
        }
        let data = $('#edit-form').serialize();
        $.post(this.editEndPoint, data, function () {
            // waiting
        }).done(function () {
            // show alert
            controller.renderAlert('Role edited entered.', true);
            // success
            $('#edit-id').val('');
            $('#edit-name').val('');
            $('#edit-description').val('');
            controller.fillTable();
        }).fail(function () {
            controller.renderAlert('Error while editing. Try again.', false);
        });
    }

    /**
     * Open Model, download Json data and render.
     * @param id the id of the row to delete
     */
    deleteView(id) {
        $.get(this.viewEndPoint, {id: id}, function (data) {
            $('#delete-id').val(data.id);
            $('#delete-name').html(data.name);
        }).done(function () {
            $('#delete-modal').modal('show');
        });
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
            controller.renderAlert('Role successfully deleted.', true);
            // charge new data.
            controller.fillTable();
            controller.unselect();
        }).fail(function () {
            controller.renderAlert('Error while deleting. Try again.', false);
        });
    }

    /**
     * Call insert service and get requested data with post method. Show an alert showing the response.
     */
    insert() {
        let controller = this;

        if (validate('#insert-form') === false) {
            controller.renderAlert('Error: Not all fields have been entered correctly. Please try again', false);
            return;
        }
        let data = $('#insert-form').serialize();
        $.post(this.createEndPoint, data, function () { // waiting for response-

        }).done(function () { // success response-
            // Set success alert.
            controller.renderAlert('Role successfully entered.', true);
            // Reset modal form.
            $('#add-name').val('');
            $('#add-description').val('');
            // charge new data.
            controller.fillTable();
        }).fail(function () { // fail response
            controller.renderAlert('Error while inserting. Try again.', false);
        });
    }
}


