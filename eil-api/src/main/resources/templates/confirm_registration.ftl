<#import "libs/commons.ftl" as com>

<#assign content>
    <tr>
        <td class="email-body" width="100%" cellpadding="0" cellspacing="0">
            <table class="email-body_inner" align="center" width="570" cellpadding="0" cellspacing="0" role="presentation">
                <!-- Body content -->
                <tr>
                    <td class="content-cell">
                        <div class="f-fallback">
                            <h1>Vážený uživateli,</h1>
                            <p>
                                děkujeme za zájem o registraci na <strong>E-ilustrace</strong>.<br/>

                                Pro pokračování v registraci použijte, prosím, následující odkaz
                                <a href="${(applicationUrl)!}confirm-registration/?key=${(token)!}">potvrdit registraci.</a>

                                Děkujeme a přejeme příjemný den.<br/>
                                E-ilustrace<br/>
                            </p>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</#assign>

<@com.email content/>