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
                změňte si heslo pro přístup do <strong>E-ilustrace</strong>:<br/>
                <a href="https://app.e-ilustrace.cz/password-reset/${(token)!}">změnit heslo na APP.</a><br/>
              </p>
            </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</#assign>

<@com.email content/>
