<#include "../header.ftl">
<div class="container">
    <h1 class="my-4">${name}
        <small>- Auction for ${item.name}</small>

        <#if !is_our_item>
        <a class="btn btn-success float-right" href="/market/${name}/view/${item.id}/bid" role="button"
           style="margin-left: 10px;">Bid</a>
        <#else>
        <form id="sell-form" action="/api/market/${name}/item/${item.id}/sell" method="post">
            <button type="submit" class="btn btn-success float-right" style="margin-left: 10px;">Sell</button>
        </form>
        </#if>
        <a class="btn btn-outline-danger float-right" href="/market/${name}/" role="button">Return</a>
    </h1>

    <#include "table.ftl">
</div>
<#include "../footer.ftl">