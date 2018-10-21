<#include "../header.ftl">
<div class="container">
    <h1 class="my-4">Login</h1>
    <form class="everest-form" id="account-form" action="/api/account/login" method="post">
        <#include "forminfo.ftl">
    </form>
</div>
<#include "../footer.ftl">