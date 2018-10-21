<#include "header.ftl">
<div class="container">
    <h1 class="my-4">Markets</h1>

    <div class="row">
    <#list markets as market>
    <#assign name=market.first.name>
    <#assign auctions_open=market.second>
        <div class="col-lg-3 col-md-4 col-sm-6 market-item">
            <div class="card h-100">
                <a href="/market/${name}/"><img class="card-img-top" src="https://i.imgur.com/WtTIlbM.png" alt=""></a>
                <div class="card-body">
                    <h4 class="card-title">
                        <a href="/market/${name}/">${name}</a>
                    </h4>
                    <p class="card-text">Current auctions open: ${auctions_open}</p>
                </div>
            </div>
        </div>
    </#list>
    </div>
</div>
<#include "footer.ftl">