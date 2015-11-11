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
        if (parseInt(money) >= parseInt(obj.price)) {
            html += "<input type='button' value='Buy!' onclick=buy(" + obj.sid + ") />";
        }
        document.getElementById("item-data").innerHTML = html;
    });
}

function buy(id) {

}

function modal(title, text) {
    document.getElementById("alert-header").innerText = title;
    document.getElementById("alert-body").innerText = text;
}