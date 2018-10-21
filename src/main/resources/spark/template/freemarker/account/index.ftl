<#include "../header.ftl">
<div class="container">
    <h1 class="my-4">Dashboard</h1>
    <h4 class="my-4">Balance: ${user.balance}</h4>

    <div class="row">
    <#list items_owned as item>
        <div class="col-lg-3 col-md-4 col-sm-6 market-item">
            <div class="card h-100">
                <div class="card-body">
                    <h4 class="card-title">${item.name}</h4>
                </div>
            </div>
        </div>
    </#list>
    </div>
</div>
<#include "../footer.ftl">