function showData(id) {
    $.ajax({
        url: "../data?id=" + id,
        context: document.body
    }).done(function (data) {
        var obj = jQuery.parseJSON(data);
        var html = "";
        html += "<h3>" + obj.itemName + "</h3>";
        if (obj.enchanted) {
            html += "<img src='" + obj.imgsrc + "' class='view-icon enchanted' />";
        } else {
            html += "<img src='" + obj.imgsrc + "' class='view-icon' />";
        }
        html += "<h4>Amount: " + obj.amount + "</h4>";
        html += "<h4>Price: $" + obj.price + "</h4>";
        if (obj.enchanted) {
            html += "<h4><b>Enchants</b></h4>";
            obj.enchants.forEach(function (enchant) {
                html += ("<h5>" + enchant.eName + ": " + enchant.level + "</h5>");
            });
        }
        if (parseInt(money) >= parseInt(obj.price) && !obj.isOwner) {
            html += "<center><input type='button' value='Buy!' onclick=buy(" + obj.sid + ") /></center><br>";
        }
        if(obj.isOwner) {
            html += "<center><input type='button' value='Cancel listing' onclick=cancel(" + obj.sid + ") /></center>";
        }
        document.getElementById("item-data").innerHTML = html;
    });
}

function buy(id) {
    $.ajax({
        url: "../buy?id=" + id,
        context: document.body
    }).done(function (data) {
        var obj = jQuery.parseJSON(data);
        document.getElementById("item-data").innerHTML = "";
        document.getElementById(id).remove();
        modal(obj.type,obj.text);
    });
}

function cancel(id) {
    $.ajax({
        url: "../cancel?id=" + id,
        context: document.body
    }).done(function (data) {
        var obj = jQuery.parseJSON(data);
        document.getElementById("item-data").innerHTML = "";
        document.getElementById(id).remove();
        modal(obj.type,obj.text);
    });
}

function modal(title, text) {
    document.getElementById("alert-header").innerText = title;
    document.getElementById("alert-body").innerText = text;

    $("#alertModal").modal();
}

Element.prototype.remove = function() {
    this.parentElement.removeChild(this);
}
NodeList.prototype.remove = HTMLCollection.prototype.remove = function() {
    for(var i = this.length - 1; i >= 0; i--) {
        if(this[i] && this[i].parentElement) {
            this[i].parentElement.removeChild(this[i]);
        }
    }
}