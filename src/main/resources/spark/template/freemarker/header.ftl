<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Everest</title>

    <link href="/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/market.css" rel="stylesheet">
    <script src="/vendor/jquery/jquery.min.js"></script>
    <script src="/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
    <div class="container">
        <a class="navbar-brand" href="/">Everest</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive"
                aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">
            <ul class="navbar-nav ml-auto">
                <#if not_auth>
                <li class="nav-item active">
                    <a class="nav-link" href="/account/login">Login
                        <span class="sr-only">(current)</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/account/register">Register</a>
                </li>
                </#if>
                <#if !not_auth>
                <li class="nav-item">
                    <a class="nav-link" href="/account/">Account</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/market/">Markets</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="logout_button" href="/">Logout</a>
                </li>
                </#if>
            </ul>
        </div>
    </div>
</nav>