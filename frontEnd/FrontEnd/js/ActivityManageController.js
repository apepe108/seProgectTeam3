class ActivityManageController {

    /**
     * Constructor
     *
     * @param endPoint
     * @param type
     */
    constructor(endPoint, type) {
        this.viewEndPoint = endPoint + "/activity";
        this.type = type;
        this.createEndPoint = endPoint + "/create-activity";
        this.editEndPoint = endPoint + "/edit-activity";
        this.deleteEndPoint = endPoint + "/delete-activity";
        this.siteEndPoint = endPoint + "/site";
        this.materialsEndPoint = endPoint + "/material";
        this.typologyEndPoint = endPoint + "/typology";
        this.proceduresEndPoint = endPoint + "/procedure"
        this.workspaceEndPoint = endPoint + "/workspaces"
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
        $.getJSON(this.viewEndPoint, {type: this.type}, function (data) {
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
            row = row.replace(/{Site}/ig, obj.site.name);
            row = row.replace(/{Typology}/ig, obj.typology.name);
           if(obj.maintenanceProcedures.id === "0"){
               row = row.replace(/{Procedure}/ig, "");
           }else{
               row = row.replace(/{Procedure}/ig, obj.maintenanceProcedures.name);
           }

            row = row.replace(/{Description}/ig, obj.description);
            row = row.replace(/{Time}/ig, obj.estimatedInterventionTime);
            row = row.replace(/{Interruptibility}/ig, obj.interruptibility);

            let list = "<ul>";
            $.each(obj.materials, function (index, obj) {
                list = list + "<li style='width:70px;'>" + obj.name + "</li>"
            })
            list = list + "</ul>"
            row = row.replace(/{Materials}/ig, list)

            row = row.replace(/{Day}/ig, controller.getDay(obj.day));
            row = row.replace(/{Week}/ig, obj.week);
            row = row.replace(/{Year}/ig, obj.year);
            if (obj.workspace.id != 0) {
                row = row.replace(/{Notes}/ig, obj.workspace.description);
            } else {
                row = row.replace(/{Notes}/ig, "");
            }

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
     * Open Edit Modal, download Json data and render.
     */
    viewEdit() {
        let controller = this

        $('#edit-typology option').remove();
        $('#edit-site option').remove();
        $('#edit-procedure option').remove();

        controller.getSites('#edit-site');
        controller.getTypologies('#edit-typology')
        controller.getMaterials('#edit-table-materials');
        controller.getProcedures('#edit-procedure')
        $.get(controller.viewEndPoint, {id: controller.selectedRowID}, function (data) {
            $('#edit-id').val(data.id);
            $('#edit-type').val(controller.type);
            $("#edit-site").val(data.site.id)
            $('#edit-typology').val(data.typology.id);
            $('#edit-procedure').val(data.maintenanceProcedures.id)
            $('#edit-description').val(data.description);
            $('#edit-time').val(data.estimatedInterventionTime);


            if(data.interruptibility === 'true'){
                $('#edit-interruptibility-true').prop('checked', true);
            }else{
                $('#edit-interruptibility-false').prop('checked', true);
            }
            $('#edit-day').val(data.day);
            $('#edit-week').val(data.week);
            $('#edit-year').val(data.year);

            controller.markMaterialsAlreadyPossessed(data.materials);

            if (data.workspace.id != 0) {
                $('#edit-notes').val(data.workspace.description);
            }

        }).done(function () {
            $('#edit-modal').modal('show')
        });
        // Charging
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
            controller.renderAlert('Activity edited correctly.', true);
            // success
            controller.fillTable();
            controller.unselect();
        }).fail(function () {
            controller.renderAlert('Error while editing. Try again.', false);
        });
    }

    /**
     * Open Delete Modal.
     */
    deleteView() {
        let controller = this;
        $.get(controller.viewEndPoint, {id: controller.selectedRowID}, function (data) {
            $('#delete-id').val(data.id);
        }).done(function () {
            $('#delete-modal').modal('show')
        });
        // Charging
    }

    /**
     * Call delete service and get requested data with get method. Shows an alert showing the response.
     */
    delete() {
        const controller = this;

        let data = $('#delete-id').val();
        $.get(controller.deleteEndPoint, {id:data}, function () {
            // waiting
        }).done(function () {
            // show alert
            controller.renderAlert('Activity deleted successfully.', true);
            // charge new data.
            controller.fillTable();
            controller.unselect();
        }).fail(function () {
            controller.renderAlert('Error while deleting. Try again.', false);
        });
    };

    /**
     * Open Insert Modal, calling the function to update the possible choices
     */
    insertView() {


        $('#insert-typology option').remove();
        $('#insert-site option').remove();
        $('#insert-procedure option').remove();

        this.getMaterials('#insert-table-materials');
        this.getTypologies('#insert-typology');
        this.getSites('#insert-site');
        this.getProcedures('#insert-procedure');
        $('#insert-type').val(this.type);

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
            controller.renderAlert('Activity successfully entered.', true);
            // Reset modal form.
            $('#add-name').val('');
            $('#add-description').val('');
            // charge new data.
            controller.fillTable();
            controller.unselect();
            controller.clearModal()
        }).fail(function () { // fail response
            controller.renderAlert('Error while inserting. Try again.', false);
        });

    };


    /**
     *  Retrieves the data of all the materials and calls the function for rendering
     *  @param modal the modal to render
     */
    getMaterials(modal) {
        let controller = this;
        $.getJSON(this.materialsEndPoint, function (data) {
            controller.addMaterialsCheckBoxes(data, modal);
        }).done(function () {

        }).fail(function () {

        });
    }

    /**
     * Adds a checkbox in the form for each material
     * @param data a Json containing the data of materials
     * @param modal the modal to render
     */
    addMaterialsCheckBoxes(data, modal) {
        $(modal + ' td').remove();
        let staticHtml = $(modal + '-template').html();
        $.each(data, function (index, obj) {
            let elem = staticHtml;
            elem = elem.replace(/{Material}/ig,
                "<div class='container d-inline-block'>" +
                "<input type='checkbox' class='checkbox' name='materials' value='" + obj.id + "'>\t" +
                "<strong>" + obj.name + ":</strong>" +
                "<td>" + obj.description + "</td>" +
                "</div>");
            $(modal + '-rows').append(elem);
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
     *  Retrieves the data of all the typologies and calls a function for render
     *  @modal the modal to render
     */
    getTypologies(modal) {
        let controller = this;
        $.getJSON(this.typologyEndPoint, function (data) {
            controller.addTypologyChoices(data, modal)
        })
    }

    /**
     * Adds an option in the form for each typology
     * @param data a Json containing the data of typologies
     * @param modal the modal to render
     */
    addTypologyChoices(data, modal) {
        $.each(data, function (index, obj) {
            let row = "<option name='typologyId' value='" + obj.id + "'>" + obj.name + "</option>";
            $(modal).append(row);
        });
    }

    /**
     * Retrieves the data of all the sites
     * @param modal the modal to render
     */
    getSites(modal) {
        let controller = this;
        $.getJSON(this.siteEndPoint, function (data) {
            controller.addSiteChoices(data, modal);
        }).done(function(){
            // Set the first element of the
            controller.setRelatedWorkspace($('#insert-site option:first').val(), '#insert');
        });
    }

    /**
     * Adds an option in the form for each site
     * @param data a Json containing the data of sites
     * @param modal the modal to render
     */
    addSiteChoices(data, modal) {
        $.each(data, function (index, obj) {
            let row = "<option name='siteId' value='" + obj.id + "'>" + obj.name + "</option>";
            $(modal).append(row);
        });
    }


    /**
     *  Retrieves the data of all the procedures
     *  @modal the modal to render
     */
    getProcedures(modal) {
        let controller = this;
        $.getJSON(this.proceduresEndPoint, function (data) {
            controller.addProcedureChoices(data, modal)
        })
    }

    /**
     * Adds an option in the form for each procedure
     * @param data a Json containing the data of procedures
     * @param modal the modal to render
     */
    addProcedureChoices(data, modal) {
        $.each(data, function (index, obj) {
            let row = "<option name='procedureId' value='" + obj.id + "'>" + obj.name + "</option>";
            $(modal).append(row);
        });
    }

    /**
     *  Clears the input fields of the insert modal
     */
    clearModal(){
        $('#insert-week').val('');
        $('#insert-year').val('');
        $('#insert-interruptibility-true').prop('checked', 'true');
        $('#insert-description').val('');
        $('#insert-time').val('');
        $('#insert-notes').val('');
    }

    /**
     * Retrieves the data of workspaces and set the field "Workspace note" with the workspace related to the currently
     * selected site
     * @param siteId the site currently selected
     */
    setRelatedWorkspace(siteId, form){
        let flag = false;
        $.getJSON(this.workspaceEndPoint, function(data){
            $.each(data, function(index, obj){
                if(flag === true){
                    return;
                }
               let workspaceDescription = obj.description;
               $.each(obj.site, function(index, obj){
                   if (flag === true){
                       return;
                   }

                   if(obj.id == siteId){
                       $(form+'-notes').val(workspaceDescription);
                       flag = true;
                   }
               });

            });

        });

        if(flag === false){
            $(form+'-notes').val("");
        }
    }

    getDay(number){
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
    };

    /**
     * Function used when no one row have to be selected (e.g., when a role is successfully created or edited).
     */
    unselect() {
        $('#edit-button').prop('disabled', true);
        $('#delete-button').prop('disabled', true);
        $('.selected').removeClass('selected');
        controller.setSelectedRowID(-1);
    };


}
