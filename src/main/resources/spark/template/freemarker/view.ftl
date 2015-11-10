<head>
    <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="../../index.css">
</head>
<div class="container"
     style="width: 90%; background-color: white; opacity: 0.8; padding-left: 0px; padding-right: 0px;">
    <div class="navbar navbar-inverse">
        <div class="container" style="margin-left: 0px; margin-right: 0px;">
            <div class="navbar-header">
                <a class="navbar-brand" href="#">Minecraft Trading post</a>
            </div>
            <div class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="#">Home</a></li>
                    <li><a href="#">Made by VoidWhisperer</a></li>
                </ul>
                <div class="col-sm-3 col-md-3" id="right">
                    <form class="navbar-form" role="search">
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
                    <tr class="border-bottom">
                        <td>
                            <!-- TODO: Get datavalue only if it actually affects the type of block -->
                            <#if sale.isEnchanted()>
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
                <!-- Second column -->

            </div>
        </div>
        <!-- /.container -->
    </div>
</div>
