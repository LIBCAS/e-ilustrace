<#macro email emailBody>
    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="x-apple-disable-message-reformatting" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="color-scheme" content="light dark" />
        <meta name="supported-color-schemes" content="light dark" />
        <title></title>
        <style type="text/css" media="all">
            /* Base ------------------------------ */
            @import url("https://fonts.googleapis.com/css?family=Nunito+Sans:400,700&display=swap");

            body {
                width: 100% !important;
                height: 100%;
                margin: 0;
                -webkit-text-size-adjust: none;
            }

            a {
                color: #3869D4;
            }

            a img {
                border: none;
            }

            td {
                word-break: break-word;
            }

            /* Type ------------------------------ */
            body,
            td,
            th {
                font-family: "Nunito Sans", Helvetica, Arial, sans-serif;
            }

            h1 {
                margin-top: 0;
                color: #333333;
                font-size: 22px;
                font-weight: bold;
                text-align: left;
            }

            h2 {
                margin-top: 0;
                color: #333333;
                font-size: 16px;
                font-weight: bold;
                text-align: left;
            }

            h3 {
                margin-top: 0;
                color: #333333;
                font-size: 14px;
                font-weight: bold;
                text-align: left;
            }

            td,
            th {
                font-size: 16px;
            }

            p,
            ul,
            ol,
            blockquote {
                margin: .4em 0 1.1875em;
                font-size: 16px;
                line-height: 1.625;
            }

            p.sub {
                font-size: 13px;
            }

            /* Utilities ------------------------------ */
            .align-right {
                text-align: right;
            }

            .align-left {
                text-align: left;
            }

            .align-center {
                text-align: center;
            }

            .italic {
                font-style: italic;
            }

            /* Buttons ------------------------------ */
            .button {
                background-color: #3869D4;
                border-top: 10px solid #3869D4;
                border-right: 18px solid #3869D4;
                border-bottom: 10px solid #3869D4;
                border-left: 18px solid #3869D4;
                display: inline-block;
                color: #FFF;
                text-decoration: none;
                border-radius: 3px;
                box-shadow: 0 2px 3px rgba(0, 0, 0, 0.16);
                -webkit-text-size-adjust: none;
                box-sizing: border-box;
            }

            .button--green {
                background-color: #22BC66;
                border-top: 10px solid #22BC66;
                border-right: 18px solid #22BC66;
                border-bottom: 10px solid #22BC66;
                border-left: 18px solid #22BC66;
            }

            .button--red {
                background-color: #FF6136;
                border-top: 10px solid #FF6136;
                border-right: 18px solid #FF6136;
                border-bottom: 10px solid #FF6136;
                border-left: 18px solid #FF6136;
            }

            @media only screen and (max-width: 500px) {
                .button {
                    width: 100% !important;
                    text-align: center !important;
                }
            }

            /* Data table ------------------------------ */
            body {
                background-color: #F4F4F7;
                color: #51545E;
            }

            p {
                color: #51545E;
            }

            p.sub {
                color: #6B6E76;
            }

            .email-wrapper {
                width: 100%;
                margin: 0;
                padding: 0;
                -premailer-width: 100%;
                -premailer-cellpadding: 0;
                -premailer-cellspacing: 0;
                background-color: #F4F4F7;
            }

            .email-content {
                width: 100%;
                margin: 0;
                padding: 0;
                -premailer-width: 100%;
                -premailer-cellpadding: 0;
                -premailer-cellspacing: 0;
            }

            /* Masthead ----------------------- */
            .email-masthead {
                padding: 25px 0;
                text-align: center;
            }

            .email-masthead_logo {
                width: 94px;
            }

            .email-masthead_name {
                font-size: 16px;
                font-weight: bold;
                color: #A8AAAF;
                text-decoration: none;
                text-shadow: 0 1px 0 white;
            }

            /* Body ------------------------------ */
            .email-body {
                width: 100%;
                margin: 0;
                padding: 0;
                -premailer-width: 100%;
                -premailer-cellpadding: 0;
                -premailer-cellspacing: 0;
                background-color: #FFFFFF;
            }

            .email-body_inner {
                width: 570px;
                margin: 0 auto;
                padding: 0;
                -premailer-width: 570px;
                -premailer-cellpadding: 0;
                -premailer-cellspacing: 0;
                background-color: #FFFFFF;
            }

            .email-footer {
                width: 570px;
                margin: 0 auto;
                padding: 0;
                -premailer-width: 570px;
                -premailer-cellpadding: 0;
                -premailer-cellspacing: 0;
                text-align: center;
            }

            .email-footer p {
                color: #6B6E76;
            }

            .body-action {
                width: 100%;
                margin: 30px auto;
                padding: 0;
                -premailer-width: 100%;
                -premailer-cellpadding: 0;
                -premailer-cellspacing: 0;
                text-align: center;
            }

            .body-sub {
                margin-top: 25px;
                padding-top: 25px;
                border-top: 1px solid #EAEAEC;
            }

            .content-cell {
                padding: 35px;
            }

            /*Media Queries ------------------------------ */
            @media only screen and (max-width: 600px) {
                .email-body_inner,
                .email-footer {
                    width: 100% !important;
                }
            }

            @media (prefers-color-scheme: dark) {
                body,
                .email-body,
                .email-body_inner,
                .email-content,
                .email-wrapper,
                .email-masthead,
                .email-footer {
                    background-color: #333333 !important;
                    color: #FFF !important;
                }

                p,
                ul,
                ol,
                blockquote,
                h1,
                h2,
                h3 {
                    color: #FFF !important;
                }

                .email-masthead_name {
                    text-shadow: none !important;
                }
            }

            :root {
                color-scheme: light dark;
                supported-color-schemes: light dark;
            }
        </style>
    </head>
    <body>
    <table class="email-wrapper" width="100%" cellpadding="0" cellspacing="0" role="presentation">
        <tr>
            <td align="center">
                <table class="email-content" width="100%" cellpadding="0" cellspacing="0" role="presentation">
                    <tr>
                        <td class="email-masthead">
                            <a href="${(applicationUrl)!}" class="f-fallback email-masthead_name">${(applicationName)!}</a>
                        </td>
                    </tr>
                    ${emailBody}
                </table>
            </td>
        </tr>
    </table>
    </body>
    </html>
</#macro>