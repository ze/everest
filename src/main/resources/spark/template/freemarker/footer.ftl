<footer class="py-4 bg-dark" id="everest-footer">
    <div class="container">
        <p class="m-0 text-center text-white">Zakaria Elkatani &copy; Everest 2018</p>
    </div>
</footer>

<#if !not_auth>
<script>
    $("#logout_button").click(function () {
        $.post("/api/account/logout");
    })
</script>
</#if>

</body>
</html>