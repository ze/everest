<#include "../header.ftl">
<div class="container">
    <h1 class="my-4">Market
        <small>${name} - New Auction</small>

        <a class="btn btn-outline-danger float-right" href="/market/${name}/" role="button">Cancel</a>
    </h1>

    <form class="everest-form" id="auction-form" action="/api/market/${name}/auction" method="post">
        <div class="form-group">
            <label for="items">Item</label>
            <select class="form-control" id="items" required>
                <#list items_owned as item>
                    <option>${item.name}</option>
                </#list>
            </select>
        </div>
        <div class="form-group">
            <label for="item_id">Item ID</label>
            <input class="form-control" id="item_id" name="item_id" type="text" placeholder="" readonly tabindex="-1">
        </div>
        <script>
            let nameToId = {};
            <#list items_owned as item>
            nameToId["${item.name}"] = ${item.id};
            </#list>

            $("#items").change(function () {
                let id = nameToId[$(this).val()];
                $("#item_id").val(id);
            });

            $("#items").val(0);
        </script>
        <div class="form-group">
            <label for="starting_price">Starting Price</label>
            <input class="form-control" id="starting_price" name="starting_price" type="number"
                   aria-describedby="bid-info" required>
            <small id="bid-info" class="form-text text-muted">
                The starting price cannot go over your own balance.
            </small>
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
</div>
<#include "../footer.ftl">