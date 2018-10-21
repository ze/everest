<#include "../header.ftl">
<div class="container">
    <h1 class="my-4">Market
        <small>${name}</small>
        <a class="btn btn-primary float-right" href="/market/${name}/auction" role="button">New Auction</a>
    </h1>

    <#if items_info?has_content>
    <div class="row">
        <#list items_info as item_info>
            <#assign item=item_info.first>
            <#assign highest=item_info.second>
            <div class="col-lg-3 col-md-4 col-sm-6 market-item">
                <div class="card h-100">
                    <div class="card-body">
                        <h4 class="card-title">
                            <a href="/market/${name}/view/${item.id}">${item.name}</a>
                        </h4>
                        <p class="card-text">
                        <#if highest??>
                            Highest Bid: ${highest.first}<br>
                            By User: ${highest.second.username}
                        </#if>
                        </p>
                    </div>
                </div>
            </div>
        </#list>
    </div>
    <#else>
    <p class="text-center my-4">There are no auctions currently happening. Maybe you should start one?</p>
    </#if>

</div>
<#include "../footer.ftl">
