<head>
    <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="../../index.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js" integrity="sha512-K1qjQ+NcF2TYO/eI3M6v8EiNYZfA95pQumfvcVrTHtwQVDG+aHRqLi/ETn2uB+1JqwYqVG3LIvdm9lj6imS/pQ==" crossorigin="anonymous"></script>
    <script>
        <#if money??>
        var money = ${money?c};
        <#else>
        var money = -1;
        </#if>
    </script>
    <script src="../view.js"></script>
</head>
<div class="container"
     style="width: 90%; background-color: white; opacity: 0.8; padding-left: 0px; padding-right: 0px;">
    <div class="navbar navbar-inverse">
        <div class="container" style="margin-left: 0px; margin-right: 0px;">
            <div class="navbar-header">
                <a class="navbar-brand" href="/">Minecraft Trading post</a>
            </div>
            <div class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="#">Home</a></li>
                    <#if loggedIn>
                        <li><a href="#">Balance: $${money}</a></li>
                    <#else>
                        <li><a href="/">Log in to see your balance.</a></li>
                    </#if>
                </ul>
                <div class="col-sm-3 col-md-3" id="right">
                    <form class="navbar-form" role="search" action="/">
                        <div class="input-group">
                            <input type="text" class="form-control" placeholder="Search" name="srch-term"
                                   id="srch-term">

                            <div class="input-group-btn">
                                <button class="btn btn-default" id="search" type="submit"><i
                                        class="glyphicon glyphicon-search"></i></button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <!--/.nav-collapse -->
        </div>
    </div>
    <div class="container-fluid height-fix">
        <div class="row height-fix">
            <div class="col-sm-6 border-right height-fix">
                <h4 class="text-center">${material}</h4>
                <table class="listings-table text-center border-top">
                    <tbody>
                    <tr>
                        <th class="item-column">Item</th>
                        <th class="amount-column">Amount</th>
                        <th class="price-column">Price</th>
                    </tr>
                    <#list listings as sale>
                    <tr class="border-bottom" onmouseover="showData(${sale.getId()})" id="${sale.getId()}">
                        <td>
                            <!-- TODO: Get datavalue only if it actually affects the type of block -->
                            <#if sale.isEnchanted() || (sale.getItem().getTypeId() == 322 && sale.getDataValue() == 1)>
                                <img src='../../${sale.getItem().getTypeId()}-${sale.getDataValue()}.png'
                                     class="sale-icon enchanted"/> <b>${sale.getName()}</b>
                            <#else>
                                <img src='../../${sale.getItem().getTypeId()}-${sale.getDataValue()}.png'
                                     class="sale-icon"/> <b>${sale.getName()}</b>
                            </#if>
                        </td>
                        <td>
                        ${sale.getItem().getAmount()}
                        </td>
                        <td>
                            $${sale.getPrice()}
                        </td>
                    </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
            <div class="col-sm-6 height-fix">
                <div id="item-data" style="display: inline;" class="text-center">

                </div>
            </div>
        </div>
        <!-- /.container -->
    </div>
</div>

<div id="alertModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title" id="alert-header"></h4>
            </div>
            <div class="modal-body">
                <p id="alert-body"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>
