<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Compare ${originalRoot} with ${revisedRoot}</title>
    <style>
        /**
        * uaplus.css version 0.3.0
        */
        @layer{*,*::after,*::before{box-sizing:border-box}:focus-visible{outline-offset:3px}html{-webkit-text-size-adjust:none;text-size-adjust:none}html{line-height:1.5}html{scrollbar-gutter:stable}h1{font-size:2em;margin-block:.67em}abbr[title]{cursor:help;text-decoration-line:underline;text-decoration-style:dotted}@media (forced-colors:active){mark{color:HighlightText;background-color:Highlight}}audio,iframe,img,svg,video{max-block-size:100%;max-inline-size:100%}fieldset{min-inline-size:0}label:has(+input:not([type="radio"],[type="checkbox"]),select,textarea){display:block}textarea:not([rows]){min-block-size:6em}button,input,select,textarea{font-family:inherit;font-size:inherit}[type="search"]{-webkit-appearance:textfield}@supports (-webkit-touch-callout:none){[type="search"]{border:1px solid -apple-system-secondary-label;background-color:canvas}}input:where([type="tel"],[type="url"],[type="email"],[type="number"]):not(:placeholder-shown){direction:ltr}table{border-collapse:collapse;border:1px solid}th,td{border:1px solid;padding:.25em .5em;vertical-align:top}dialog::backdrop{background:oklch(0% 0 0 / .3)}dialog,[popover],dialog::backdrop{opacity:0;transition:opacity 150ms ease-out,display 150ms allow-discrete,overlay 150ms allow-discrete}dialog[open],:popover-open,dialog[open]::backdrop{opacity:1}@starting-style{dialog[open],:popover-open,dialog[open]::backdrop{opacity:0}}[hidden]:not([hidden="until-found"]){display:none!important}img{display:block}summary{cursor:default}iframe{border:none}@supports(position-area:block-end span-inline-end){[popover]{margin:0;position-area:block-end span-inline-end}}}

        div#summary {

            input {
                margin-right: 1rem;
            }

            &:not(:has(input#UNCHANGED:checked)) tr.UNCHANGED {
                display: none;
            }

            &:not(:has(input#ADDED:checked)) tr.ADDED {
                display: none;
            }

            &:not(:has(input#DELETED:checked)) tr.DELETED {
                display: none;
            }

            &:not(:has(input#UNCOMPARABLE:checked)) tr.UNCOMPARABLE {
                display: none;
            }

            table {
                width: stretch;
            }

            col.status {
                width: 12rem;
            }

            th, td {
                white-space: pre-wrap;
                word-wrap: anywhere;

                &:nth-child(2) {
                    text-align: center;
                }
            }

            tr.CHANGED {
                background-color: #ffffee;
            }

            tr.ADDED {
                background-color: #eeeeff;
            }

            tr.DELETED {
                background-color: #ffeeee;
            }

            tr.UNCOMPARABLE {
                background-color: #eeeeee;
            }

        }

        div#changes {

            h3 {
                position: sticky;
                top: 0;
            }

            table {
                table-layout: fixed;
                width: stretch;
            }

            th, td {
                border: none;
                border-left: 1px solid;
                padding: 0;
            }

            pre {
                margin: 0;
                padding: 0;
                min-height: 1em;
                white-space: pre-wrap;
                word-wrap: anywhere;
            }

            tr.CHANGE {
                background-color: #ffffee;
            }

            tr.INSERT {
                background-color: #eeeeff;
            }

            tr.DELETE {
                background-color: #ffeeee;
            }
            
            span.new, span.old {
                background-color: #ffeeee;
                color: #660000;
            }

        }
    </style>
</head>
<body>
    <h1>Compare ${originalRoot} with ${revisedRoot}</h1>

    <div id="summary">
        <h2>Summary</h2>

        <label>
            <span>UNCHANGED</span>
            <input type="checkbox" id="UNCHANGED" checked>
        </label>
        <label>
            <span>ADDED</span>
            <input type="checkbox" id="ADDED" checked>
        </label>
        <label>
            <span>DELETED</span>
            <input type="checkbox" id="DELETED" checked>
        </label>
        <label>
            <span>UNCOMPARABLE</span>
            <input type="checkbox" id="UNCOMPARABLE" checked>
        </label>

        <table>
            <colgroup>
                <col>
                <col class="status">
            </colgroup>
            <thead>
                <tr>
                    <th>Path</th>
                    <th>Result</th>
                </tr>
            </thead>
            <tbody>
                <#list diffs as diff>
                <tr class="${diff.status}">
                    <td>${diff.path}</td>
                    <#if diff.patch??>
                    <td><a href="#changes-${diff_index}">${diff.status}</a></td>
                    <#else>
                    <td>${diff.status}</td>
                    </#if>
                </tr>
                </#list>
            </tbody>
        </table>
    </div>

    <hr>

    <div id="changes">
        <h2>Changes</h2>
        <#list diffs as diff>
        <#if diff.patch??>
            <div>
                <h3><a id="changes-${diff_index}">${diff.path}</a></h3>
                <table>
                    <tbody>
                        <#list reporter.getDiffRows(diff) as diffRow>
                        <tr class="${diffRow.tag}">
                            <td><#if diffRow.oldLine??><pre><code>${diffRow.oldLine}</code></pre></#if></td>
                            <td><#if diffRow.newLine??><pre><code>${diffRow.newLine}</code></pre></#if></td>
                        </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
        </#if>
        </#list>
    </div>

</body>
</html>
