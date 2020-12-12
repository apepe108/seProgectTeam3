function validate(idForm) {
    $(idForm).addClass("needs-validation")
    let flag = true
    let forms = document.getElementsByClassName("needs-validation");
    Array.prototype.filter.call(forms, function (form) {
        if (form.checkValidity() === false) {
            flag = false
        }
    }, false)
    $(idForm).removeClass("needs-validation")
    return flag
}


