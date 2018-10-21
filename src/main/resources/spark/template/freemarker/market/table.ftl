<table class="table table-hover">
    <thead class="thead-dark">
    <tr>
        <th scope="col">Bid #</th>
        <th scope="col">Amount</th>
        <th scope="col">Bidder</th>
    </tr>
    </thead>
    <tbody>
        <#list bids_pair?reverse as bid_pair>
        <#assign bid=bid_pair.first>
        <#assign user=bid_pair.second>
        <tr>
            <th scope="row">${bids_pair?size - bid_pair?index}</th>
            <td>${bid.amount}</td>
            <td>${user.username}</td>
        </tr>
        </#list>
    </tbody>
</table>