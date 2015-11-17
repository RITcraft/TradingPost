<head>
    <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="index.css">
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
                    <#if loggedIn>
                        <li><a href="#">Balance: $${money}</a></li>
                    <#else>
                        <li><a href="/">Log in to see your balance.</a></li>
                    </#if>
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
            <#if loggedIn>
                <h4>Your stats: </h4>
                Your balance: $${money}
                <br><a href="/logout"><input type="button" value="Logout"/></a>
            <#else>
                <h4>Log in</h4>

                <form action="/login" method="POST">
                    Username: <input type="text" name="username"/><br>
                    Password: <input type="password" name="password"/><br>
                    <input type="submit"/><br>
                    <#if error??>
                        <div class="alert alert-danger" role="alert">
                            <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
                            <span class="sr-only">Error:</span>
                        ${error}
                        </div>
                    </#if>
                </form>
            </#if>
            </div>
            <div class="col-sm-6 height-fix">
                <table class="listings-table text-center">
                    <tbody>
                    <tr>
                        <th class="item-column">Item</th>
                        <th class="amount-column">Amount</th>
                        <th class="price-column">Price</th>
                    </tr>
                    <#list listings as sale>
                    <tr class="border-bottom">
                        <td>
                            <a href="/view/${sale.getItem().getType().name()}">
                                <!-- TODO: Get datavalue only if it actually affects the type of block -->
                                <#if sale.isEnchanted()>
                                    <img src='${sale.getItem().getTypeId()}-${sale.getDataValue()}.png'
                                         class="sale-icon enchanted"/> <b>${sale.getName()}</b>
                                <#else>
                                    <img src='${sale.getItem().getTypeId()}-${sale.getDataValue()}.png'
                                         class="sale-icon"/>
                                    <b>${sale.getName()}</b>
                                </#if>
                            </a>
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
        </div>
        <!-- /.container -->
    </div>
</div>
