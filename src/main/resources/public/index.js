function cancel(id) {
    $.ajax({
        url: "../cancel?id=" + id,
        context: document.body
    }).done(function (data) {
        var obj = jQuery.parseJSON(data);
        document.getElementById(id).remove();
        if (document.getElementById("right-" + id) != null) {
            document.getElementById("right-" + id).remove();
        }
        modal(obj.type, obj.text);
    });
}

function modal(title, text) {
    document.getElementById("alert-header").innerText = title;
    document.getElementById("alert-body").innerText = text;

    $("#alertModal").modal();
}

Element.prototype.remove = function () {
    this.parentElement.removeChild(this);
}
NodeList.prototype.remove = HTMLCollection.prototype.remove = function () {
    for (var i = this.length - 1; i >= 0; i--) {
        if (this[i] && this[i].parentElement) {
            this[i].parentElement.removeChild(this[i]);
        }
    }
}