<#include "../header.ftl">
<div class="container">
    <h1 class="my-4">${name}
        <small>- Bid for ${item.name}</small>
    </h1>

    <form class="everest-form" id="bid-form" action="/api/market/${name}/item/${item.id}/bid" method="post">
        <div class="form-group">
            <label for="balance">Current Balance</label>
            <input class="form-control" id="balance" type="text" placeholder="${user.balance}"
                   aria-describedby="bid-info" tabindex="-1" readonly>
            <small id="bid-info" class="form-text text-muted">
                The minimum amount to bid must be greater than the highest bid amount.
            </small>
        </div>
        <div class="form-group">
            <label for="starting_price">Bid Amount</label>
            <input class="form-control" id="starting_price" name="bid_amount" type="number" required>
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>

    <#include "table.ftl">
</div>
<#include "../footer.ftl">