class SMPController {

    /**
     * Constructor
     *
     * @param endPoint
     */
    constructor(endPoint) {
        this.procedureEndPoint = endPoint + "/procedure";
        this.smpEndPoint = endPoint + "/view-smp";
        this.uploadSmpEndPoint = endPoint + "/smp";
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
        $.getJSON(this.procedureEndPoint, function (data) {
            controller.renderGUI(data);
        }).done(function () {
            // controller.renderAlert('Data charged successfully.', true);
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
            if (obj.smp == 0) {
                row = row.replace(/{SMP}/ig, " ")
            } else {
                row = row.replace(/{SMP}/ig, '<a href="' + controller.smpEndPoint + '?id=' + obj.smp + '" type="button" class="btn btn-outline-primary btn-sm btn-rounded"><i class=\"fas fa-download mr-2\"></i></a>');
            }

            let editIcon = '<button class="btn btn-outline-primary btn-sm"  data-toggle="modal"\n' +
                '                data-target="#new-smp-modal" onclick="controller.doEdit=true">'
                +'<i class="fas fa-edit mr-2"></i>'+
                '        </button>';
            row = row.replace(/{Edit}/ig, editIcon);
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

    display(callback) {
        callback(controller.selectedRowID)
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

    viewEdit() {
        $('#procedure-id').val(this.selectedRowID);
    }

    /**
     * Send edit request and show result alert.
     */
    edit() {
        const controller = this;

        if (validate('#new-smp-form') === false) {
            controller.renderAlert('Error: The input fields cannot be left empty. Edit rejected', false);
            return;
        }

        const form = $('#new-smp-form')[0];
        const data = new FormData(form);
        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: controller.uploadSmpEndPoint,
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 5000,
            success: function (data) {
                controller.renderAlert('SMP upload successfully.', true);
                controller.fillTable();
                controller.unselect();
            },
            error: function (e) {
                controller.renderAlert('Error while uploading. Try again.', false);
            }
        });
    }

    /**
     * Function method for handle click on
     */
    handleClickOnRow() {
        // set new selected id
        controller.setSelectedRowID($(this).find("td:first").html());

        // ability modify and edit button
        $('#new-smp-button').prop('disabled', false);

        // set highlights row
        $('.selected').removeClass('selected');
        $(this).addClass("selected");

        if(controller.doEdit === true){
            controller.doEdit = false;
            controller.viewEdit();
        }
    };

    /**
     * Function used when no one row have to be selected (e.g., when a role is successfully created or edited).
     */
    unselect() {
        $('#new-smp-button').prop('disabled', true);
        $('.selected').removeClass('selected');
        controller.setSelectedRowID(-1);
    };

}
